package uz.ciasev.ubdd_service.service.main.resolution;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.ciasev.ubdd_service.dto.internal.request.resolution.organ.SingleResolutionRequestDTO;
import uz.ciasev.ubdd_service.entity.Place;
import uz.ciasev.ubdd_service.entity.admcase.AdmCase;
import uz.ciasev.ubdd_service.entity.dict.Department;
import uz.ciasev.ubdd_service.entity.dict.District;
import uz.ciasev.ubdd_service.entity.dict.Organ;
import uz.ciasev.ubdd_service.entity.dict.Region;
import uz.ciasev.ubdd_service.entity.dict.user.Rank;
import uz.ciasev.ubdd_service.entity.protocol.Protocol;
import uz.ciasev.ubdd_service.entity.resolution.decision.Decision;
import uz.ciasev.ubdd_service.entity.signature.SignatureEvent;
import uz.ciasev.ubdd_service.entity.user.User;
import uz.ciasev.ubdd_service.entity.violator.Violator;
import uz.ciasev.ubdd_service.event.AdmEventService;
import uz.ciasev.ubdd_service.event.AdmEventType;
import uz.ciasev.ubdd_service.service.admcase.AdmCaseService;
import uz.ciasev.ubdd_service.service.aop.signature.DigitalSignatureCheck;
import uz.ciasev.ubdd_service.service.dict.user.PositionDictionaryService;
import uz.ciasev.ubdd_service.service.dict.user.RankDictionaryService;
import uz.ciasev.ubdd_service.service.generator.DecisionNumberGeneratorService;
import uz.ciasev.ubdd_service.service.generator.ResolutionNumberGeneratorService;
import uz.ciasev.ubdd_service.service.main.resolution.dto.CreatedDecisionDTO;
import uz.ciasev.ubdd_service.service.main.resolution.dto.CreatedSingleResolutionDTO;
import uz.ciasev.ubdd_service.service.protocol.ProtocolService;
import uz.ciasev.ubdd_service.service.resolution.ResolutionCreateRequest;
import uz.ciasev.ubdd_service.service.resolution.ResolutionService;
import uz.ciasev.ubdd_service.service.resolution.compensation.CompensationService;
import uz.ciasev.ubdd_service.service.violator.ViolatorService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAdmResolutionServiceImpl implements UserAdmResolutionService {

    private final AdmCaseService admCaseService;
    private final ViolatorService violatorService;
    private final AdmEventService notificatorService;
    private final ResolutionHelpService helpService;
    private final ResolutionNumberGeneratorService resolutionNumberGeneratorService;
    private final DecisionNumberGeneratorService decisionNumberGeneratorService;
    private final ResolutionService resolutionService;
    private final CompensationService compensationService;
    private final ProtocolService protocolService;
    private final RankDictionaryService rankDictionaryService;
    private final PositionDictionaryService positionDictionaryService;



    @Override
    @DigitalSignatureCheck(event = SignatureEvent.RESOLUTION)
    public CreatedSingleResolutionDTO createSingle(User user, Long externalId, SingleResolutionRequestDTO requestDTO) {

        Protocol protocol = protocolService.findByExternalId(user, String.valueOf(externalId));
        AdmCase admCase = admCaseService.getByProtocolId(protocol.getId());

        Optional<Decision> optionalDecision = resolutionService.getDecisionOfResolutionById(admCase.getId());
        if (optionalDecision.isPresent() && optionalDecision.get().isActive()) {
            return getDtoOfResolutionAlreadyMade(optionalDecision.get());
        }

        return createSingle(user, admCase, requestDTO);
    }

    private CreatedSingleResolutionDTO createSingle(User user, AdmCase admCase, SingleResolutionRequestDTO requestDTO) {

        Violator violator = violatorService.findSingleByAdmCaseId(admCase.getId());
        requestDTO.setViolatorId(violator.getId());

        Place resolutionPlace = calculateResolutionPlace(user, requestDTO);

        ResolutionCreateRequest resolution = helpService.buildResolution(requestDTO);


        positionDictionaryService.findById(requestDTO.getInspectorPositionId()).ifPresent(
                position -> resolution.setInspectorPosition(position.getDefaultName())
        );

        rankDictionaryService.findById(requestDTO.getInspectorRankId()).ifPresent(
                rank -> resolution.setInspectorRank(rank.getDefaultName())
        );

        Decision decision = helpService.buildDecision(violator, requestDTO, null /*penaltyBankAccountSettingsSupplier*/);

        admCase.setConsiderUser(user);
        admCase.setConsiderInfo(requestDTO.getConsiderUserInfo());

        CreatedSingleResolutionDTO data = helpService.resolve(admCase, user, resolutionPlace, resolutionNumberGeneratorService, decisionNumberGeneratorService, resolution, decision);

        notificatorService.fireEvent(AdmEventType.ORGAN_RESOLUTION_CREATE, data);

        return data;
    }


    private Place calculateResolutionPlace(User user, SingleResolutionRequestDTO requestDTO) {
        return new Place() {
            @Override
            public Region getRegion() {
                return requestDTO.getRegion();
            }

            @Override
            public District getDistrict() {
                return requestDTO.getDistrict();
            }

            @Override
            public Organ getOrgan() {
                return user.getOrgan();
            }

            @Override
            public Department getDepartment() {
                return requestDTO.getDepartment();
            }
        };
    }


    private CreatedSingleResolutionDTO getDtoOfResolutionAlreadyMade(Decision decision) {
        return new CreatedSingleResolutionDTO(
                decision.getResolution(),
                List.of(new CreatedDecisionDTO(decision, compensationService.findAllByDecisionId(decision.getId()))));
    }

    private boolean checkResolutionIsSame(Decision decision, SingleResolutionRequestDTO requestDTO) {
        return (decision.isActive() &&
                !decision.getPunishments().isEmpty() &&
                ((decision.getArticleViolationTypeId() == null && requestDTO.getArticleViolationType() == null)
                        || (requestDTO.getArticleViolationType() != null
                        && decision.getArticleViolationTypeId().equals(requestDTO.getArticleViolationType().getId()))) &&
                decision.getDecisionTypeId().equals(requestDTO.getDecisionType().getId()) &&
                ((decision.getArticlePartId() == null && requestDTO.getArticlePart() == null)
                        || (requestDTO.getArticlePart() != null && decision.getArticlePartId().equals(requestDTO.getArticlePart().getId()))) &&
                decision.getPunishments().get(0).isActive() &&
                decision.getPunishments().get(0).getPenalty().getAmount().equals(requestDTO.getMainPunishment().getAmount()));
    }

    private boolean checkAdmCaseStatusIsSuitable(AdmCase admCase) {
        switch (admCase.getStatus().getAlias()) {
            case DECISION_MADE:
            case IN_EXECUTION_PROCESS:
            case EXECUTED:
                return true;
            default:
                break;
        }
        return false;
    }

}
