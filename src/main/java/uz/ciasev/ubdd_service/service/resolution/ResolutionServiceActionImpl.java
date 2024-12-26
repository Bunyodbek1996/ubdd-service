package uz.ciasev.ubdd_service.service.resolution;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ciasev.ubdd_service.exception.NotFoundException;
import uz.ciasev.ubdd_service.mvd_core.api.mib.webhook.dto.adm.MibAdmTerminationDTO;
import uz.ciasev.ubdd_service.dto.internal.request.resolution.organ.CancellationResolutionRequestDTO;
import uz.ciasev.ubdd_service.entity.Inspector;
import uz.ciasev.ubdd_service.entity.action.ActionAlias;
import uz.ciasev.ubdd_service.entity.admcase.AdmCase;
import uz.ciasev.ubdd_service.entity.dict.resolution.OrganCancellation;
import uz.ciasev.ubdd_service.entity.dict.resolution.OrganCancellationAlias;
import uz.ciasev.ubdd_service.entity.dict.resolution.ReasonCancellationAlias;
import uz.ciasev.ubdd_service.entity.invoice.Invoice;
import uz.ciasev.ubdd_service.entity.invoice.InvoiceDeactivateReasonAlias;
import uz.ciasev.ubdd_service.entity.resolution.Resolution;
import uz.ciasev.ubdd_service.entity.resolution.cancellation.CancellationResolution;
import uz.ciasev.ubdd_service.entity.resolution.compensation.Compensation;
import uz.ciasev.ubdd_service.entity.resolution.decision.Decision;
import uz.ciasev.ubdd_service.entity.signature.SignatureEvent;
import uz.ciasev.ubdd_service.entity.status.AdmStatusAlias;
import uz.ciasev.ubdd_service.entity.user.User;
import uz.ciasev.ubdd_service.event.AdmEventService;
import uz.ciasev.ubdd_service.event.AdmEventType;
import uz.ciasev.ubdd_service.exception.implementation.ImplementationException;
import uz.ciasev.ubdd_service.service.admcase.AdmCaseService;
import uz.ciasev.ubdd_service.service.aop.signature.DigitalSignatureCheck;
import uz.ciasev.ubdd_service.service.dict.AliasedDictionaryService;
import uz.ciasev.ubdd_service.service.dict.resolution.ReasonCancellationService;
import uz.ciasev.ubdd_service.service.invoice.InvoiceActionService;
import uz.ciasev.ubdd_service.service.invoice.InvoiceService;
import uz.ciasev.ubdd_service.service.main.ArchiveService;
import uz.ciasev.ubdd_service.service.resolution.cancellation.CancellationResolutionService;
import uz.ciasev.ubdd_service.service.resolution.compensation.CompensationService;
import uz.ciasev.ubdd_service.service.resolution.decision.DecisionAccessService;
import uz.ciasev.ubdd_service.service.resolution.decision.DecisionActionService;
import uz.ciasev.ubdd_service.service.resolution.decision.DecisionService;
import uz.ciasev.ubdd_service.service.status.AdmCaseStatusService;
import uz.ciasev.ubdd_service.service.status.StatusService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static uz.ciasev.ubdd_service.entity.action.ActionAlias.*;
import static uz.ciasev.ubdd_service.entity.invoice.InvoiceDeactivateReasonAlias.DECISION_IN_REVIEW;
import static uz.ciasev.ubdd_service.entity.invoice.InvoiceDeactivateReasonAlias.RESOLUTION_CANCELED;
import static uz.ciasev.ubdd_service.entity.status.AdmStatusAlias.CONSIDERING;
import static uz.ciasev.ubdd_service.entity.status.AdmStatusAlias.EXECUTED;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResolutionServiceActionImpl implements ResolutionActionService {

    private final AdmCaseService admCaseService;
    private final StatusService statusService;
    private final AdmCaseStatusService caseStatusService;
    private final DecisionAccessService decisionAccessService;
    private final CancellationResolutionService cancellationResolutionService;
    private final ResolutionService resolutionService;
    private final DecisionService decisionService;
    private final DecisionActionService decisionActionService;
    private final InvoiceActionService invoiceActionService;
    private final CompensationService compensationService;
    private final AdmEventService notificatorService;
    private final ReasonCancellationService reasonCancellationService;
    private final AliasedDictionaryService<OrganCancellation, OrganCancellationAlias> organCancellationDictionaryService;
    private final AdmEventService admEventService;
    private final ArchiveService archiveService;
    private final InvoiceService invoiceService;

    @Override
    @Transactional
    @DigitalSignatureCheck(event = SignatureEvent.RESOLUTION_CANCEL)
    public void cancelResolutionByOrgan(User user, Long externalId, CancellationResolutionRequestDTO dto) {
        cancelResolution(user, externalId, dto);
    }

    @Override
    @Transactional
    public void cancelResolutionByCourt(Resolution resolution, ReasonCancellationAlias reasonAlias, Long climeId) {

        List<Decision> decisions = decisionService.findByResolutionId(resolution.getId());
        for (Decision decision : decisions) {
            decisionAccessService.checkSystemActionPermit(COURT_CANCEL_RESOLUTION, decision);
        }

        CancellationResolution cancellation = CancellationResolution.builder()
                .resolution(resolution)
                .reasonCancellation(reasonCancellationService.getByAlias(reasonAlias))
                .organCancellation(organCancellationDictionaryService.getByAlias(OrganCancellationAlias.COURT))
                .cancellationDate(LocalDateTime.now())
                .claimId(climeId)
                .fileUri("")
                .signature(null)
                .user(null)
                .build();

        cancel(resolution, decisions, cancellation);
    }

    @Override
    @Transactional
    public void cancelResolutionByMib(Resolution resolution, MibAdmTerminationDTO dto, Inspector inspector) {

        List<Decision> decisions = decisionService.findByResolutionId(resolution.getId());
        for (Decision decision : decisions) {
            decisionAccessService.checkIsNotCourt(decision);
            decisionAccessService.checkSystemActionPermit(MIB_CANCEL_RESOLUTION, decision);
        }

        CancellationResolution cancellation = CancellationResolution
                .builder()
                .resolution(resolution)
                .reasonCancellation(reasonCancellationService.getByAlias(ReasonCancellationAlias.CANCELLATION_OF_ACT))
                .organCancellation(organCancellationDictionaryService.getByAlias(OrganCancellationAlias.ORGAN_OF_HIGHER_RANK))
                .cancellationDate(dto.getDateDoc().atStartOfDay())
                .fileUri("")
                .signature(null)
                .user(inspector.getUser())
                .build();

        cancel(resolution, decisions, cancellation);
    }

    @Override
    @DigitalSignatureCheck(event = SignatureEvent.RESOLUTION_CANCEL)
    public CancellationResolution cancelResolutionByProtest(User user, Long resolutionId, CancellationResolutionRequestDTO dto) {

        return cancelResolution(user, resolutionId, dto);
    }

    private void cancel(Resolution resolution,
                        List<Decision> decisions,
                        CancellationResolution cancellation) {

        blockInvoice(decisions, RESOLUTION_CANCELED);

        decisions.forEach(d -> {
            archiveService.returnFromArchive(d.getViolator());
        });

        resolutionService.setIsActive(resolution, false);

        Optional.ofNullable(cancellation).ifPresent(c -> cancellationResolutionService.create(resolution, c));

        admEventService.fireEvent(AdmEventType.DECISIONS_CANCEL, decisions);
    }

    @Override
    public List<Decision> review(List<Decision> decisions) {
        checkDecisionsForAction(decisions, COURT_REVIEW_DECISION);

        for (Decision decision : decisions) {
            decisionActionService.saveStatus(decision, AdmStatusAlias.IN_REVIEW_PROCESS);
        }

        blockInvoice(decisions, DECISION_IN_REVIEW);

        return decisions;
    }

    @Override
    public List<Decision> cancelReview(List<Decision> decisions) {
        checkDecisionsForAction(decisions, CANCEL_COURT_REVIEW_DECISION);

        for (Decision decision : decisions) {
            statusService.cancelStatus(decision, AdmStatusAlias.IN_REVIEW_PROCESS);
            decisionActionService.saveStatus(decision, statusService.getStatus(decision).getAlias());
        }

        Resolution resolution = getResolution(decisions);
        AdmCase admCase = resolution.getAdmCase();
        caseStatusService.setStatus(admCase, resolution.getStatus().getAlias());
        admCaseService.update(admCase.getId(), admCase);

        openInvoices(decisions);

        return decisions;
    }

    @Override
    public List<Decision> restore(List<Decision> decisions) {
        checkDecisions(decisions);

        // todo переехать на десижены

//        Resolution resolution = resolutionService.getDTOById(decisions.get(0).getResolutionId());
//        openInvoices(decisionService.findByResolutionId(resolution.getId()));

        Resolution resolution = getResolution(decisions);
        openInvoices(decisions);

        resolutionService.setIsActive(resolution, true);

//        resolution.setActive(true);
//        resolutionRepository.saveAndFlush(resolution);

        return decisions;
    }

    @Override
    @Transactional
    public void makeMibPreSendEvent(Long decisionId) {
        Decision decision = decisionService.getById(decisionId);
        notificatorService.fireEvent(AdmEventType.MIB_PRE_SEND, decision);
        decisionActionService.setMibPreSendHandled(decision);
    }

    public void checkResolutionDecisions(User user, List<Decision> decisions, ActionAlias alias) {

        for (Decision decision : decisions) {
            decisionAccessService.checkUserActionPermit(user, alias, decision);
        }
    }

    private void blockInvoice(List<Decision> decisions, InvoiceDeactivateReasonAlias reasonAlias) {
        decisions.forEach(decision -> doForInvoices(
                decision,
                invoice -> invoiceActionService.close(invoice, reasonAlias, List.of())
        ));
    }

    private void openInvoices(List<Decision> decisions) {
        decisions.forEach(decision -> doForInvoices(
                decision,
                invoiceActionService::open
        ));
    }

    private void doForInvoices(Decision decision, Consumer<Invoice> consumer) {
        Optional.ofNullable(decision)
                .flatMap(invoiceService::findPenaltyInvoiceByDecision)
                .ifPresent(consumer);

        Optional<Compensation> govCompensation = compensationService.findGovByDecision(decision);
        govCompensation.map(Compensation::getInvoice).ifPresent(consumer);
    }

    private void checkDecisionsForAction(List<Decision> decisions, ActionAlias alias) {
        checkDecisions(decisions);
        decisions.stream().forEach(decision -> decisionAccessService.checkSystemActionPermit(alias, decision));
    }

    private void checkDecisions(List<Decision> decisions) {
        if (decisions.size() == 0) {
            throw new ImplementationException("Decisions required for action");
        }

        Resolution resolution = getResolution(decisions);

        if (!decisions.stream().allMatch(d -> d.getResolutionId().equals(resolution.getId()))) {
            throw new ImplementationException("Decisions from different resolution");
        }
    }

    private Resolution getResolution(List<Decision> decisions) {
        return decisions.get(0).getResolution();
    }

    @Transactional
    private CancellationResolution cancelResolution(User user, Long externalId, CancellationResolutionRequestDTO dto) {
        Resolution resolution = resolutionService.findPenaltyPunishmentByExternalIdAndOrganId(String.valueOf(externalId), 12L).orElseThrow(() -> new NotFoundException("Resolution not found with id: " + externalId));
        AdmCase admCase = resolution.getAdmCase();

        List<Decision> decisions = decisionService.findByResolutionId(resolution.getId());
        decisionAccessService.checkIsNotCourt(resolution);

        CancellationResolution cancellation = CancellationResolution
                .builder()
                .resolution(resolution)
                .reasonCancellation(dto.getReasonCancellation())
                .organCancellation(dto.getOrganCancellation())
                .cancellationDate(dto.getCancellationTime())
                .fileUri("")
                .user(user)
                .build();

        caseStatusService.setStatus(admCase, EXECUTED);
        admCaseService.update(admCase.getId(), admCase);

        cancel(resolution, decisions, cancellation);

        return cancellation;
    }
}
