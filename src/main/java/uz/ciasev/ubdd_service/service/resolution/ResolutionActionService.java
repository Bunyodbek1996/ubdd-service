package uz.ciasev.ubdd_service.service.resolution;

import org.springframework.transaction.annotation.Transactional;
import uz.ciasev.ubdd_service.mvd_core.api.mib.webhook.dto.adm.MibAdmTerminationDTO;
import uz.ciasev.ubdd_service.dto.internal.request.resolution.organ.CancellationResolutionRequestDTO;
import uz.ciasev.ubdd_service.entity.Inspector;
import uz.ciasev.ubdd_service.entity.action.ActionAlias;
import uz.ciasev.ubdd_service.entity.dict.resolution.ReasonCancellationAlias;
import uz.ciasev.ubdd_service.entity.resolution.decision.Decision;
import uz.ciasev.ubdd_service.entity.resolution.cancellation.CancellationResolution;
import uz.ciasev.ubdd_service.entity.resolution.Resolution;
import uz.ciasev.ubdd_service.entity.user.User;

import java.util.List;

public interface ResolutionActionService {

    void cancelResolutionByOrgan(User user, Long externalId, CancellationResolutionRequestDTO dto);

    @Transactional
    void cancelResolutionByCourt(Resolution resolution, ReasonCancellationAlias reasonAlias, Long climeId);

    void cancelResolutionByMib(Resolution resolution, MibAdmTerminationDTO dto, Inspector inspector);

    CancellationResolution cancelResolutionByProtest(User user, Long resolutionId, CancellationResolutionRequestDTO dto);

    List<Decision> review(List<Decision> decision);

    List<Decision> cancelReview(List<Decision> decisions);

    List<Decision> restore(List<Decision> decisions);

    void makeMibPreSendEvent(Long decisionId);

    void checkResolutionDecisions(User user, List<Decision> decisions, ActionAlias alias);
}
