package uz.ciasev.ubdd_service.service.invoice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.ciasev.ubdd_service.dto.internal.response.adm.InvoiceResponseDTO;
import uz.ciasev.ubdd_service.dto.ubdd.UbddInvoiceRequest;
import uz.ciasev.ubdd_service.entity.invoice.Invoice;
import uz.ciasev.ubdd_service.entity.protocol.Protocol;
import uz.ciasev.ubdd_service.entity.resolution.decision.Decision;
import uz.ciasev.ubdd_service.entity.resolution.punishment.PenaltyPunishment;
import uz.ciasev.ubdd_service.entity.user.User;
import uz.ciasev.ubdd_service.exception.implementation.LogicalException;
import uz.ciasev.ubdd_service.exception.notfound.EntityByIdNotFound;
import uz.ciasev.ubdd_service.exception.notfound.EntityByParamsNotFound;
import uz.ciasev.ubdd_service.repository.invoice.InvoiceRepository;
import uz.ciasev.ubdd_service.repository.resolution.punishment.PenaltyPunishmentRepository;
import uz.ciasev.ubdd_service.service.generator.InvoiceNumberGeneratorService;
import uz.ciasev.ubdd_service.service.protocol.ProtocolService;
import uz.ciasev.ubdd_service.utils.PageUtils;
import uz.ciasev.ubdd_service.utils.types.MultiLanguage;

import java.util.Optional;

import static uz.ciasev.ubdd_service.entity.invoice.InvoiceOwnerTypeAlias.COMPENSATION;
import static uz.ciasev.ubdd_service.entity.invoice.InvoiceOwnerTypeAlias.PENALTY;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PenaltyPunishmentRepository penaltyPunishmentRepository;
    private final InvoiceNumberGeneratorService invoiceNumberGeneratorService;

    @Override
    public Invoice create(User user, UbddInvoiceRequest request) {

        request.setPenaltyPunishmentAmount(request.getPenaltyPunishmentAmount() == null ? null : request.getPenaltyPunishmentAmount() * 100);
        request.setDiscount70Amount(request.getDiscount70Amount() == null ? null : request.getDiscount70Amount() * 100);
        request.setDiscount50Amount(request.getDiscount50Amount() == null ? null : request.getDiscount50Amount() * 100);

        Invoice invoice = request.toEntity();

        Optional<Invoice> optionalInvoice = invoiceRepository.findByInvoiceSerial(invoice.getInvoiceSerial());
        if (optionalInvoice.isPresent()) {
            return optionalInvoice.get();
        }

        invoice.setOrganName(user.getOrgan().getName().get(MultiLanguage.Language.LAT));
        invoice.setBankInn("0000");
        invoice.setBankName("0000");
        invoice.setBankName("Bank nomi ko'rsatilmagan");
        invoice.setBankCode("0000");
        invoice.setBankAccount("0000");

        PenaltyPunishment penaltyPunishment = penaltyPunishmentRepository
                .findPenaltyPunishmentIdByExternalIdAndOrganId(
                        request.getExternalId() + "", user.getOrganId()
                ).orElseThrow(() -> new EntityByParamsNotFound(PenaltyPunishment.class, "externalId", request.getExternalId(), "organId", user.getOrganId()));

        invoice.setPenaltyPunishment(penaltyPunishment);

        invoice.setInvoiceInternalNumber(invoiceNumberGeneratorService.generateNumber());

        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice findById(Long id) {
        return invoiceRepository.findById(id).orElseThrow(() -> new EntityByIdNotFound(Invoice.class, id));
    }

    @Override
    public InvoiceResponseDTO findDTOById(Long id) {
        return new InvoiceResponseDTO(findById(id));
    }

    @Override
    public Invoice findByBillingId(Long id) {
        return invoiceRepository
                .findByInvoiceId(id)
                .orElseThrow(() -> new EntityByParamsNotFound(Invoice.class, "invoiceId", id));
    }

    @Override
    public Invoice update(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @Override
    public Decision getInvoiceDecision(Invoice invoice) {
        if (invoice.getOwnerTypeAlias().equals(PENALTY)) {
            return invoiceRepository.findDecisionByInvoiceInPenaltyPunishment(invoice).orElseThrow(() -> new LogicalException("Invoice penalty decision not found"));
        } else if (invoice.getOwnerTypeAlias().equals(COMPENSATION)) {
            return invoiceRepository.findDecisionByInvoiceInCompensation(invoice).orElseThrow(() -> new LogicalException("Invoice compensation decision not found"));
        } else {
            throw new LogicalException("Invoice type has no decision");
        }
    }

    @Override
    public Optional<Invoice> findByPenalty(PenaltyPunishment penalty) {
        return invoiceRepository
                .findByPenaltyPunishmentId(penalty.getId(), PageUtils.topWithMaxId(1))
                .stream().findFirst();
    }

    @Override
    public Optional<Invoice> findPenaltyInvoiceByDecision(Decision decision) {
        return decision.getPenalty()
                .flatMap(this::findByPenalty);
    }

    @Override
    public Invoice getPenaltyInvoiceByDecision(Decision decision) {
        return findPenaltyInvoiceByDecision(decision)
                .orElseThrow(() -> new EntityByParamsNotFound(Invoice.class, "decisionId", decision.getId()));
    }

    @Override
    public Invoice findByAdmCaseId(Long id) {
        return invoiceRepository.findInvoiceByAdmCase(id).orElseThrow(
                () -> new EntityByParamsNotFound(Invoice.class, "admCaseId", id)
        );
    }

    @Override
    public Invoice findInvoiceByExternalIdAndOrganId(Long externalId, Long organId) {
        return invoiceRepository.findInvoiceByExternalIdAndOrganId(externalId + "", organId)
                .orElseThrow(() -> new EntityByParamsNotFound(Invoice.class, "externalId", externalId, "organId", organId));
    }
}
