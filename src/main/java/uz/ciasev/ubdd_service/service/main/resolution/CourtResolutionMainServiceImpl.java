package uz.ciasev.ubdd_service.service.main.resolution;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ciasev.ubdd_service.dto.internal.request.resolution.court.CourtDecisionRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.resolution.court.CourtEvidenceDecisionRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.resolution.court.CourtResolutionRequestDTO;
import uz.ciasev.ubdd_service.entity.ExternalInspector;
import uz.ciasev.ubdd_service.entity.Place;
import uz.ciasev.ubdd_service.entity.admcase.AdmCase;
import uz.ciasev.ubdd_service.entity.court.CourtInvoiceSending;
import uz.ciasev.ubdd_service.entity.dict.resolution.ReasonCancellationAlias;
import uz.ciasev.ubdd_service.entity.resolution.Resolution;
import uz.ciasev.ubdd_service.entity.resolution.compensation.Compensation;
import uz.ciasev.ubdd_service.entity.resolution.decision.Decision;
import uz.ciasev.ubdd_service.entity.settings.OrganAccountSettings;
import uz.ciasev.ubdd_service.entity.status.AdmStatusAlias;
import uz.ciasev.ubdd_service.entity.violator.Violator;
import uz.ciasev.ubdd_service.event.AdmEventService;
import uz.ciasev.ubdd_service.event.AdmEventType;
import uz.ciasev.ubdd_service.service.admcase.AdmCaseAccessService;
import uz.ciasev.ubdd_service.service.admcase.AdmCaseService;
import uz.ciasev.ubdd_service.service.court.CourtInvoiceSendingOrderService;
import uz.ciasev.ubdd_service.service.court.files.CourtFileService;
import uz.ciasev.ubdd_service.service.generator.ValueNumberGeneratorService;
import uz.ciasev.ubdd_service.service.main.resolution.dto.CreatedDecisionDTO;
import uz.ciasev.ubdd_service.service.main.resolution.dto.CreatedResolutionDTO;
import uz.ciasev.ubdd_service.service.resolution.ResolutionActionService;
import uz.ciasev.ubdd_service.service.resolution.ResolutionCreateRequest;
import uz.ciasev.ubdd_service.service.resolution.ResolutionService;
import uz.ciasev.ubdd_service.service.resolution.evidence_decision.EvidenceDecisionCreateRequest;
import uz.ciasev.ubdd_service.service.settings.AccountCalculatingService;
import uz.ciasev.ubdd_service.service.status.AdmCaseStatusService;
import uz.ciasev.ubdd_service.service.user.InspectorService;
import uz.ciasev.ubdd_service.service.violator.ViolatorService;
import uz.ciasev.ubdd_service.utils.AdmEntityList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static uz.ciasev.ubdd_service.entity.action.ActionAlias.COURT_CANCEL_RESOLUTION;
import static uz.ciasev.ubdd_service.entity.action.ActionAlias.COURT_REVIEW_CASE;
import static uz.ciasev.ubdd_service.entity.status.AdmStatusAlias.RETURN_FROM_COURT;
import static uz.ciasev.ubdd_service.entity.status.AdmStatusAlias.SENT_TO_COURT;

@Service
@RequiredArgsConstructor
public class CourtResolutionMainServiceImpl implements CourtResolutionMainService {

    private static final Long RESOLUTION_NOT_FOUND = -1L;

    private final AdmCaseService admCaseService;
    private final ViolatorService violatorService;
    private final InspectorService inspectorService;
    private final ResolutionService resolutionService;
    private final ResolutionActionService resolutionActionService;
    private final AdmCaseAccessService admCaseAccessService;
    private final AccountCalculatingService accountCalculatingService;
    private final CourtInvoiceSendingOrderService courtInvoiceSendingService;
    private final AdmEventService notificatorService;
    private final AdmCaseStatusService statusService;
    private final ResolutionHelpService helpService;
    private final CourtFileService courtFileService;

    @Override
    @Transactional
    public void prepareCaseForEditing(Long caseId, Long claimId) {
        var admCase = admCaseService.getById(caseId);

        statusService.setStatus(admCase, SENT_TO_COURT);
        admCaseService.update(admCase.getId(), admCase);

        cancelResolutionIfExist(caseId, null, ReasonCancellationAlias.COURT_CORRECTION);
    }

    @Override
    @Transactional
    public void revisionCaseInCourt(Long caseId, Long claimId) {
        var admCase = admCaseService.getById(caseId);
        admCaseAccessService.checkPermitActionWithAdmCase(COURT_REVIEW_CASE, admCase);

        statusService.setStatus(admCase, SENT_TO_COURT);
        admCaseService.update(admCase.getId(), admCase);

        cancelResolutionIfExist(caseId, claimId, ReasonCancellationAlias.COURT_REVIEW);
    }

    @Override
    @Transactional
    public void returnCaseFromCourt(Long caseId, Long claimId) {

        var admCase = admCaseService.getById(caseId);

        statusService.setStatus(admCase, RETURN_FROM_COURT);
        admCaseService.update(admCase.getId(), admCase);

    }


    @Override
    @Transactional
    public void otherCourtInstanceRetrialCase(Long caseId, Long claimId) {
//        это же обработка решения суда сывшей инстанции, о том что дело надо вернуть на пересмотр обратно в первую инстанцию.
//        делать для этого ничгео не надо.
//        С моменту вызова этой функции эдитинг или ревью уже поменяли статаус дела.

//        var admCase = admCaseService.getById(caseId);
//        admCaseAccessService.checkPermitActionWithAdmCase(COURT_CANCEL_RESOLUTION, admCase);
//
//        statusService.setStatus(admCase, SENT_TO_COURT);
//        admCaseService.update(admCase.getId(), admCase);

//        Long cancellationClimeId = hasCancellationResolution ? claimId : null;
//        ReasonCancellationAlias reasonCancellationAlias = isEditing ? ReasonCancellationAlias.COURT_CORRECTION : ReasonCancellationAlias.COURT_REVIEW;
//        cancelResolutionIfExist(caseId, cancellationClimeId, reasonCancellationAlias);
    }

    @Override
    @Transactional
    public void otherCourtInstanceReturnFromCourt(Long caseId, Long claimId) {
        returnCaseFromCourt(caseId, claimId);
//        var admCase = admCaseService.getById(caseId);
//        admCaseAccessService.checkPermitActionWithAdmCase(COURT_RETURN_CASE, admCase);
//
//        statusService.setStatus(admCase, RETURN_FROM_COURT);
//        admCaseService.update(admCase.getId(), admCase);

//        Long cancellationClimeId = hasCancellationResolution ? claimId : null;
//        ReasonCancellationAlias reasonCancellationAlias = isEditing ? ReasonCancellationAlias.COURT_CORRECTION : ReasonCancellationAlias.COURT_REVIEW;
//        cancelResolutionIfExist(caseId, cancellationClimeId, reasonCancellationAlias);
    }

    @Deprecated
    @Override
    @Transactional
    public Optional<Resolution> courtCancelResolutionByAdmCaseAndSetNewAdmStatus(Long caseId, Long claimId, AdmStatusAlias admStatusAlias, boolean hasCancellationResolution, boolean isEditing) {
        var admCase = admCaseService.getById(caseId);
        admCaseAccessService.checkPermitActionWithAdmCase(COURT_CANCEL_RESOLUTION, admCase);

        statusService.setStatus(admCase, admStatusAlias);
        admCaseService.update(admCase.getId(), admCase);

        var resolutionOpt = resolutionService.findActiveByAdmCaseId(caseId);

        if (resolutionOpt.isEmpty())
            return Optional.empty();

        var resolution = resolutionOpt.get();

        Long cancellationClimeId = hasCancellationResolution ? claimId : null;
        ReasonCancellationAlias reasonCancellationAlias = isEditing ? ReasonCancellationAlias.COURT_CORRECTION : ReasonCancellationAlias.COURT_REVIEW;

        resolutionActionService.cancelResolutionByCourt(resolution, reasonCancellationAlias, cancellationClimeId);

        return Optional.of(resolution);
    }

    @Override
    @Transactional
    public Resolution createCourtResolution(Long admCaseId, CourtResolutionRequestDTO requestDTO) {
        return createCourtResolutionInner(admCaseId, requestDTO);
    }

    private Resolution createCourtResolutionInner(Long admCaseId, CourtResolutionRequestDTO requestDTO) {
        AdmCase admCase = admCaseService.getById(admCaseId);

        List<CourtDecisionRequestDTO> decisionRequestDTOS = requestDTO.getDecisions();
        List<CourtEvidenceDecisionRequestDTO> evidenceDecisionRequestDTOS = requestDTO.getEvidenceDecisions();

        AdmEntityList<Violator> violatorList = new AdmEntityList<>(violatorService.findByAdmCaseId(admCase.getId()));

        ExternalInspector inspector = inspectorService.buildCourtInspector(requestDTO);

        Place resolutionPlace = inspector;

        ValueNumberGeneratorService numberGeneratorService = buildNumberGeneratorService(requestDTO);

        ResolutionCreateRequest resolution = helpService.buildResolution(requestDTO);
        trySetExistPdfFile(admCaseId, requestDTO, resolution);

        List<Pair<Decision, List<Compensation>>> decisionsWithCompensation = decisionRequestDTOS.stream()
                .map(d -> {
                    Violator violator = violatorList.getById(d.getViolatorId());
                    Supplier<OrganAccountSettings> penaltyAccountSettingsSupplier = getOrganAccountSettingsPenaltySupplier(admCase, violator, resolutionPlace);
                    Supplier<OrganAccountSettings> compensationAccountSettingsSupplier = getOrganAccountSettingsCompensationSupplier(admCase, violator, resolutionPlace);


                    Decision decision = helpService.buildDecision(violator, d, penaltyAccountSettingsSupplier);

                    decision.setIsSavedPdf(true);

                    List<Compensation> compensations = d.getCompensations().stream()
                            .map(compensationDTO -> {
                                return helpService.buildCompensation(compensationDTO, compensationAccountSettingsSupplier);
                            })
                            .collect(Collectors.toList());

                    return Pair.of(decision, compensations);
                })
                .collect(Collectors.toList());

        List<EvidenceDecisionCreateRequest> evidenceDecisions = evidenceDecisionRequestDTOS.stream()
                .map(CourtEvidenceDecisionRequestDTO::buildEvidenceDecision)
                .collect(Collectors.toList());

        CreatedResolutionDTO savedData = helpService.resolve(
                admCase,
                inspector,
                resolutionPlace,
                numberGeneratorService,
                numberGeneratorService,
                resolution,
                decisionsWithCompensation,
                evidenceDecisions
        );

        //guaranteeInvoices(admCase, savedData.getCreatedDecisions());

        Resolution savedResolution = savedData.getResolution();

        notificatorService.fireEvent(AdmEventType.COURT_RESOLUTION_CREATE, savedData);

        return savedResolution;
    }

    private Supplier<OrganAccountSettings> getOrganAccountSettingsPenaltySupplier(AdmCase admCase, Violator violator, Place resolutionPlace) {
        // todo переписать на врозумительное 315
        if (admCase.getCourtConsideringBasisId() == -1L) {
            return OrganAccountSettings::getEmpty;
        }


        return () -> accountCalculatingService.calculateForCourtPenalty(admCase, violator, resolutionPlace);
    }

    private Supplier<OrganAccountSettings> getOrganAccountSettingsCompensationSupplier(AdmCase admCase, Violator violator, Place resolutionPlace) {
        return () -> accountCalculatingService.calculateForCourtCompensation(admCase, violator, resolutionPlace);
    }

    private ValueNumberGeneratorService buildNumberGeneratorService(CourtResolutionRequestDTO requestDTO) {
        return new ValueNumberGeneratorService(requestDTO.getCourtNumber(), false);
    }


    private void guaranteeInvoices(AdmCase admCase,
                                   List<CreatedDecisionDTO> decisionsWithCompensation) {

        var lastInactiveResolution = resolutionService.findLastCanceledByAdmCaseId(admCase.getId());

        var resolutionId = lastInactiveResolution
                .filter(resolution -> {
                    // Если дело передали в суд по 308-прим1, то деньги пойдут суду,
                    // и квитанция первого решения органа не подходит, нужно генерить квитанцию суда.
                    // Но для пересмотров возвращать суду их старую квитанцию
                    if (admCase.getCourtConsideringBasisId() == 5L && !resolution.getOrgan().isCourt()) {
                        return false;
                    }
                    return true;
                })
                .map(Resolution::getId)
                .orElse(RESOLUTION_NOT_FOUND);

        List<CourtInvoiceSending> list = new ArrayList<>();

        for (CreatedDecisionDTO decisionListPair : decisionsWithCompensation) {

            Decision decision = decisionListPair.getDecision();
            decision.getPenalty().ifPresent((p) -> {
                CourtInvoiceSending invoice = courtInvoiceSendingService.build(resolutionId, decision);
                list.add(invoice);
            });

            decisionListPair.getGovCompensation().ifPresent(compensation -> {
                CourtInvoiceSending invoice = courtInvoiceSendingService.build(resolutionId, compensation);
                list.add(invoice);
            });

        }

        courtInvoiceSendingService.create(list);
    }

    private void cancelResolutionIfExist(Long caseId, Long cancellationClimeId, ReasonCancellationAlias reasonCancellation) {
        Optional<Resolution> resolutionOpt = resolutionService.findActiveByAdmCaseId(caseId);
        if (resolutionOpt.isEmpty())
            return;

        Resolution resolution = resolutionOpt.get();

        resolutionActionService.cancelResolutionByCourt(resolution, reasonCancellation, cancellationClimeId);
    }

    private void trySetExistPdfFile(Long admCaseId, CourtResolutionRequestDTO requestDTO, ResolutionCreateRequest resolution) {
        courtFileService.findLastByCaseIdAndClimeId(admCaseId, requestDTO.getClaimId())
                .ifPresent(f -> {
                    resolution.setFileId(f.getExternalId());
                    resolution.setCourtDecisionUri(f.getUri());
                });
    }
}
