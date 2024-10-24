package uz.ciasev.ubdd_service.controller_ubdd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uz.ciasev.ubdd_service.config.security.CurrentUser;
import uz.ciasev.ubdd_service.dto.internal.request.SingleAddressRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.violator.ViolatorDetailRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.violator.ViolatorRequestDTO;
import uz.ciasev.ubdd_service.entity.user.User;
import uz.ciasev.ubdd_service.entity.violator.Violator;
import uz.ciasev.ubdd_service.entity.violator.ViolatorDetail;
import uz.ciasev.ubdd_service.exception.ErrorCode;
import uz.ciasev.ubdd_service.service.violator.ViolatorDetailService;
import uz.ciasev.ubdd_service.service.violator.ViolatorService;
import uz.ciasev.ubdd_service.utils.validator.UzbAddress;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "${mvd-ciasev.url-v0}/violator", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class UbddViolatorController {

    private final ViolatorService violatorService;
    private final ViolatorDetailService violatorDetailService;



//    @PutMapping("/{violatorId}/protocol/{protocolId}")
//    public ViolatorDetail updateDetails(@CurrentUser User user,
//                                        @PathVariable(name = "violatorId") Long violatorId,
//                                        @PathVariable(name = "protocolId") Long protocolId,
//                                        @RequestBody @Valid ViolatorDetailRequestDTO requestDTO) {
//
//        return violatorDetailService.updateViolatorDetail(user, violatorId, protocolId, requestDTO);
//    }
//
//
//
//
//    @PutMapping("/{violatorId}/edit-actual-address")
//    public void updateActualAddress(@CurrentUser User user,
//                                    @PathVariable(name = "violatorId") Long violatorId,
//                                    @RequestBody @Valid @UzbAddress(message = ErrorCode.VIOLATOR_POST_ADDRESS_MAST_CONTENT_REGION_AND_DISTRICT) SingleAddressRequestDTO requestDTO) {
//
//        violatorService.updateViolatorActualAddress(user, violatorId, requestDTO);
//    }

}