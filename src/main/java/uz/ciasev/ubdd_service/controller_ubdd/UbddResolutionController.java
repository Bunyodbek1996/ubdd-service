package uz.ciasev.ubdd_service.controller_ubdd;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uz.ciasev.ubdd_service.config.security.CurrentUser;
import uz.ciasev.ubdd_service.dto.internal.request.resolution.organ.CancellationResolutionRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.resolution.organ.SingleResolutionRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.response.adm.resolution.DecisionListResponseDTO;
import uz.ciasev.ubdd_service.entity.user.User;
import uz.ciasev.ubdd_service.service.main.resolution.UserResolutionMadeService;
import uz.ciasev.ubdd_service.service.resolution.*;
import uz.ciasev.ubdd_service.service.resolution.decision.DecisionDTOService;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping(path = "${mvd-ciasev.url-v0}/resolutions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UbddResolutionController {

    private final ResolutionActionService resolutionActionService;
    private final DecisionDTOService decisionDTOService;
    private final UserResolutionMadeService resolutionMadeService;

    @PostMapping
    public DecisionListResponseDTO create(@CurrentUser User user,
                                          @RequestBody @Valid SingleResolutionRequestDTO requestDTO) {
        return decisionDTOService.buildListForCreate(() -> resolutionMadeService.createAdmSingle(user, requestDTO.getAdmCaseId(), requestDTO).getCreatedDecision());
    }

    @PostMapping("/{id}/cancellation")
    public void cancelResolution(@CurrentUser User user,
                                 @PathVariable Long id,
                                 @RequestBody @Valid CancellationResolutionRequestDTO dto) {
        resolutionActionService.cancelResolutionByOrgan(user, id, dto);
    }

}