package uz.ciasev.ubdd_service.controller_ubdd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uz.ciasev.ubdd_service.config.security.CurrentUser;
import uz.ciasev.ubdd_service.dto.internal.request.protocol.ProtocolRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.protocol.QualificationRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.response.adm.admcase.AdmCaseMergeResponseDTO;
import uz.ciasev.ubdd_service.dto.internal.response.adm.protocol.ProtocolDetailResponseDTO;
import uz.ciasev.ubdd_service.entity.user.User;
import uz.ciasev.ubdd_service.service.main.admcase.AdmCaseActionService;
import uz.ciasev.ubdd_service.service.main.protocol.ProtocolCreateService;
import uz.ciasev.ubdd_service.service.main.protocol.ProtocolMainService;
import uz.ciasev.ubdd_service.service.protocol.ProtocolDTOService;
import uz.ciasev.ubdd_service.service.protocol.ProtocolService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "${mvd-ciasev.url-v0}/protocol", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UbddProtocolController {

    private final ProtocolCreateService protocolCreateService;
    private final ProtocolDTOService dtoService;
    private final ProtocolMainService protocolMainService;
    private final ProtocolDTOService protocolDTOServiceNew;
    private final ProtocolService protocolService;

    private final AdmCaseActionService admCaseActionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProtocolDetailResponseDTO createProtocol(@CurrentUser User user,
                                                    @RequestBody @Valid ProtocolRequestDTO protocol) {

        return dtoService.buildDetailForCreateProtocol(user, () -> protocolCreateService.createElectronProtocol(user, protocol));

    }


//    @PostMapping(path = "/{id}/edit-qualification")
//    public void editProtocolQualification(
//            @CurrentUser User user,
//            @PathVariable Long id,
//            @Valid @RequestBody QualificationRequestDTO requestDTO) {
//        protocolMainService.editProtocolQualification(user, id, requestDTO);
//    }
//
//
//    @PostMapping("/adm-cases/{fromAdmCaseId}/merge-to/{toAdmCaseId}")
//    public AdmCaseMergeResponseDTO mergeCase(@CurrentUser User user,
//                                             @PathVariable("fromAdmCaseId") Long fromAdmCaseId,
//                                             @PathVariable("toAdmCaseId") Long toAdmCaseId) {
//        return admCaseActionService.mergeAdmCases(user, fromAdmCaseId, toAdmCaseId);
//    }
//
//
//    @PostMapping(path = "/{id}/separate")
//    public ProtocolDetailResponseDTO separateProtocolsFromAdmCase(@CurrentUser User user, @PathVariable Long id) {
//        protocolMainService.separateProtocol(user, id);
//        return protocolDTOServiceNew.buildDetail(user, () -> protocolService.findById(id));
//    }


}