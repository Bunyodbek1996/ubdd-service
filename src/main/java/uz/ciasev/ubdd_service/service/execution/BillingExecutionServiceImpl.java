package uz.ciasev.ubdd_service.service.execution;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ciasev.ubdd_service.entity.admcase.AdmCase;
import uz.ciasev.ubdd_service.entity.resolution.Resolution;
import uz.ciasev.ubdd_service.entity.status.AdmStatus;
import uz.ciasev.ubdd_service.entity.status.AdmStatusAlias;
import uz.ciasev.ubdd_service.entity.user.User;
import uz.ciasev.ubdd_service.exception.notfound.EntityByParamsNotFound;
import uz.ciasev.ubdd_service.mvd_core.api.billing.dto.BillingPaymentDTO;
import uz.ciasev.ubdd_service.entity.invoice.Invoice;
import uz.ciasev.ubdd_service.entity.invoice.InvoiceOwnerTypeAlias;
import uz.ciasev.ubdd_service.entity.invoice.Payment;
import uz.ciasev.ubdd_service.entity.resolution.compensation.Compensation;
import uz.ciasev.ubdd_service.entity.resolution.decision.Decision;
import uz.ciasev.ubdd_service.entity.resolution.execution.ExecutorType;
import uz.ciasev.ubdd_service.entity.resolution.execution.ForceExecutionDTO;
import uz.ciasev.ubdd_service.entity.resolution.execution.ForceExecutionType;
import uz.ciasev.ubdd_service.entity.resolution.punishment.PenaltyPunishment;
import uz.ciasev.ubdd_service.entity.resolution.punishment.Punishment;
import uz.ciasev.ubdd_service.entity.violator.Violator;
import uz.ciasev.ubdd_service.exception.implementation.NotImplementedException;
import uz.ciasev.ubdd_service.repository.invoice.InvoiceRepository;
import uz.ciasev.ubdd_service.repository.invoice.PaymentRepository;
import uz.ciasev.ubdd_service.repository.resolution.punishment.PenaltyPunishmentRepository;
import uz.ciasev.ubdd_service.service.publicapi.eventdata.PublicApiWebhookEventPopulationService;
import uz.ciasev.ubdd_service.service.court.CourtPaymentService;
import uz.ciasev.ubdd_service.service.invoice.InvoiceService;
import uz.ciasev.ubdd_service.service.invoice.PaymentService;
import uz.ciasev.ubdd_service.service.resolution.ResolutionService;
import uz.ciasev.ubdd_service.service.resolution.compensation.CompensationActionService;
import uz.ciasev.ubdd_service.service.resolution.decision.DecisionService;
import uz.ciasev.ubdd_service.service.resolution.punishment.PunishmentActionService;
import uz.ciasev.ubdd_service.service.resolution.punishment.PunishmentService;
import uz.ciasev.ubdd_service.service.status.AdmCaseStatusService;
import uz.ciasev.ubdd_service.service.status.AdmStatusDictionaryService;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BillingExecutionServiceImpl implements BillingExecutionService {

    private final EnumMap<InvoiceOwnerTypeAlias, PaymentConsumer> executionMap = new EnumMap<>(InvoiceOwnerTypeAlias.class);

    protected final PaymentService paymentService;
    protected final PunishmentService punishmentService;
    protected final PenaltyPunishmentRepository penaltyPunishmentRepository;
    protected final PunishmentActionService punishmentActionService;
    protected final CompensationActionService compensationService;
    protected final ExecutionCallbackService executionCallbackService;
    protected final InvoiceService invoiceService;
    protected final InvoiceRepository invoiceRepository;
    protected final DecisionService decisionService;
    protected final CourtPaymentService courtService;
    protected final PublicApiWebhookEventPopulationService publicApiWebhookEventPopulationService;
    protected final BillingEntityService billingEntityService;
    protected final PaymentRepository paymentRepository;
    protected final AdmCaseStatusService admCaseStatusService;
    protected final AdmStatusDictionaryService admStatusDictionaryService;
    protected final ResolutionService resolutionService;



    {
        executionMap.put(InvoiceOwnerTypeAlias.PENALTY, this::executionPenalty);
        executionMap.put(InvoiceOwnerTypeAlias.COMPENSATION, this::executionCompensation);
        executionMap.put(InvoiceOwnerTypeAlias.DAMAGE, this::executionDamage);
    }

    @Override
    @Transactional
    public void calculateAndSetExecution(BillingEntity billingEntity) {
        BillingData billingData = billingEntityService.getBillingData(billingEntity);

        Optional<LocalDateTime> lastPaymentTimeOpt = billingData.getLastPayTime();
        Long paidAmount = billingData.getTotalAmount().orElse(0L);

        billingEntity.setLastPayTime(lastPaymentTimeOpt.orElse(null));
        billingEntity.setPaidAmount(paidAmount);

        executionMap.get(billingEntity.getInvoiceOwnerTypeAlias()).accept(billingEntity, billingData.getExecutorNames());
    }

    @Override
    @Transactional
    public void handlePayment(User user, BillingPaymentDTO paymentDTO) {

        if (paymentDTO.getId() == null) {
            throw new RuntimeException("Payment id is null");
        }

        if (paymentRepository.findByNumber(String.valueOf(paymentDTO.getId())).isPresent()) {
            return;
        }

        Invoice invoice = invoiceRepository.findByInvoiceSerial("MAB_" + paymentDTO.getInvoiceSerial()).orElseThrow(
                () -> new EntityByParamsNotFound(Invoice.class, "invoiceSerial", paymentDTO.getInvoiceSerial())
        );

        Payment savedPayment = paymentService.save(invoice, paymentDTO);

        PenaltyPunishment penaltyPunishment = penaltyPunishmentRepository
                .findPenaltyPunishmentIdByExternalIdAndOrganId(paymentDTO.getExternalId() + "", user.getOrganId())
                .orElseThrow(() -> new EntityByParamsNotFound(PenaltyPunishment.class, "externalId", paymentDTO.getExternalId(), "organId", user.getOrganId()));

        penaltyPunishment.setPaidAmount(penaltyPunishment.getPaidAmount() + savedPayment.getAmount());
        penaltyPunishment.setLastPayTime(paymentDTO.getPaidAt());

        Resolution resolution = invoiceRepository.findResolutionByInvoiceInPenaltyPunishment(invoice).orElseThrow(
                () -> new EntityByParamsNotFound(Resolution.class, "invoice", invoice, "penaltyPunishment", penaltyPunishment)
        );

        AdmStatus admStatus = admStatusDictionaryService.findByAlias(AdmStatusAlias.EXECUTED);

        resolutionService.updateStatus(resolution, admStatus, null);

        Decision decision = invoiceRepository.findDecisionByInvoiceInPenaltyPunishment(invoice).orElseThrow(
                () -> new EntityByParamsNotFound(Decision.class, "invoice", invoice, "penaltyPunishment", penaltyPunishment)
        );

        decision.setStatus(admStatus);

        Punishment punishment = invoiceRepository.findPunishmentByInvoiceInPenaltyPunishment(invoice).orElseThrow(
                () -> new EntityByParamsNotFound(Punishment.class, "invoice", invoice, "penaltyPunishment", penaltyPunishment)
        );

        punishment.setStatus(admStatus);

        AdmCase admCase = invoiceRepository.findAdmCaseByInvoiceInPenaltyPunishment(invoice).orElseThrow(
                () -> new EntityByParamsNotFound(AdmCase.class, "invoice", invoice, "penaltyPunishment", penaltyPunishment)
        );

        admCaseStatusService.setStatusAndSave(admCase, AdmStatusAlias.EXECUTED);

        courtService.acceptIfCourt(invoice, savedPayment);
    }


    private void executionPenalty(BillingEntity billingEntity, List<String> executorNames) {
        Punishment punishment = (Punishment) billingEntity;

        PenaltyPunishment penalty = punishment.getPenalty();
        ForceExecutionDTO forceExecutionDTO;
        ExecutorType changeReason;

        if (penalty.getPaidAmount() == null || penalty.getPaidAmount() == 0) {
            forceExecutionDTO = null;
            changeReason = ExecutorType.BILLING;
        } else if (!penalty.isAnyDiscountAlive(penalty.getLastPayTime().toLocalDate())) {
            // Скидка уже не действует
            forceExecutionDTO = null;
            changeReason = ExecutorType.BILLING;
        } else if (isFullyPaid(penalty.getAmount(), penalty.getPaidAmount())) {
            // Скидка действует, но человек оплатил полную сумму, аначит факт использования скидки не формируем
            forceExecutionDTO = null;
            changeReason = ExecutorType.BILLING;
        } else if (penalty.isDiscount50Alive(penalty.getLastPayTime().toLocalDate()) && isFullyPaid(penalty.getDiscount50Amount(), penalty.getPaidAmount())) {
            // Человек использовал первую скидку
            forceExecutionDTO = new ForceExecutionDTO(penalty.getLastPayTime().toLocalDate(), ForceExecutionType.DISCOUNT);
            changeReason = ExecutorType.BILLING_WITH_DISCOUNT;
        } else if (penalty.isDiscount70Alive(penalty.getLastPayTime().toLocalDate()) && isFullyPaid(penalty.getDiscount70Amount(), penalty.getPaidAmount())) {
            // Человек использовал скидку вторую скидку
            forceExecutionDTO = new ForceExecutionDTO(penalty.getLastPayTime().toLocalDate(), ForceExecutionType.DISCOUNT);
            changeReason = ExecutorType.BILLING_WITH_DISCOUNT;
        } else {
            forceExecutionDTO = null;
            changeReason = ExecutorType.BILLING;
        }


        punishmentActionService.addExecution(punishment, changeReason, executorNames, forceExecutionDTO);

        Decision decision = decisionService.getById(punishment.getDecisionId());
        executionCallbackService.executeCallback(decision);
    }

    private void executionCompensation(BillingEntity billingEntity, List<String> executorNames) {
        Compensation compensation = compensationService.setExecution((Compensation) billingEntity, executorNames, null);

        executionCallbackService.executeCallback(decisionService.getById(compensation.getDecisionId()));
    }

    private void executionDamage(BillingEntity billingEntity, List<String> executorNames) {
        throw new NotImplementedException("Payment for damage");
    }

    private boolean isFullyPaid(Long actualAmount, Long paidAmount) {
        if (paidAmount == null) {
            return false;
        }
        int compareResult = paidAmount.compareTo(actualAmount);
        return compareResult >= 0;
    }

    @FunctionalInterface
    public interface PaymentConsumer {
        void accept(BillingEntity billingEntity, List<String> executorNames);
    }
}