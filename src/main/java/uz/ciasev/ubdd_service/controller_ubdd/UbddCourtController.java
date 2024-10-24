package uz.ciasev.ubdd_service.controller_ubdd;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uz.ciasev.ubdd_service.config.security.CurrentUser;
import uz.ciasev.ubdd_service.entity.user.User;
import uz.ciasev.ubdd_service.mvd_core.api.court.CourtWebhookService;
import uz.ciasev.ubdd_service.mvd_core.api.court.dto.CourtRequestDTO;
import uz.ciasev.ubdd_service.mvd_core.api.court.dto.UbddCourtRequest;
import uz.ciasev.ubdd_service.mvd_core.api.court.dto.eight.EightCourtResolutionRequestDTO;
import uz.ciasev.ubdd_service.mvd_core.api.court.dto.third.ThirdCourtResolutionRequestDTO;
import uz.ciasev.ubdd_service.service.court.methods.UbddCourtService;

import javax.validation.Valid;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "${mvd-ciasev.url-v0}/court", produces = MediaType.APPLICATION_JSON_VALUE)
public class UbddCourtController {

    private final UbddCourtService ubddCourtService;

    private final CourtWebhookService courtWebhookService;

//    @PostMapping("/sent")
//    public void postSendGeneral(@RequestBody @Valid UbddCourtRequest request) {
//        ubddCourtService.sentCourt(request);
//    }
//
//
//    @PostMapping("/resolution")
//    public void acceptCourtResolution(@RequestBody @Valid ThirdCourtResolutionRequestDTO request) {
//        ubddCourtService.acceptUbddCourtResolution(request);
//    }
//
//    @GetMapping("/decision-for-315")
//    public EightCourtResolutionRequestDTO findResolutionBySeriesAndNumber(@RequestParam(name = "series") String series,
//                                                           @RequestParam(name = "number") String number) {
//        CourtRequestDTO<EightCourtResolutionRequestDTO> response = courtWebhookService.acceptEightMethod(series, number);
//        return response.getSendDocumentRequest();
//    }

}