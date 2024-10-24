package uz.ciasev.ubdd_service.controller_ubdd;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.ciasev.ubdd_service.exception.ErrorCode;
import uz.ciasev.ubdd_service.mvd_core.api.mib.api.types.MibResult;
import uz.ciasev.ubdd_service.mvd_core.api.mib.webhook.dto.execution.MibRequestDTO;
import uz.ciasev.ubdd_service.mvd_core.api.mib.webhook.dto.execution.MvdExecutionResponseDTO;
import uz.ciasev.ubdd_service.mvd_core.api.mib.webhook.service.execution.MibApiExecutionService;
import uz.ciasev.ubdd_service.mvd_core.api.mib.webhook.service.protocol.MibApiAdmService;
import uz.ciasev.ubdd_service.service.mib.UbddMibAutoSendService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Objects;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "${mvd-ciasev.url-v0}/mib", produces = MediaType.APPLICATION_JSON_VALUE)
public class UbddMibController {

    private final UbddMibAutoSendService mibAutoSendService;
    private final MibApiExecutionService mibApiExecutionService;
    private final MibApiAdmService mibApiAdmService;

//    @PostMapping("/sent")
//    public void postSendGeneral(@RequestBody @Valid MibResult mibResult) {
//        mibAutoSendService.send(mibResult.getAdmCaseId(), mibResult);
//    }
//
//
//    @PostMapping("/execute")
//    public MvdExecutionResponseDTO acceptCourtResolution(@RequestBody @Valid MibRequestDTO result) {
//
//        if (Objects.nonNull(result.getEnvelopeId()) && Objects.nonNull(result.getOffenseId())) {
//            throw new ValidationException(ErrorCode.MIB_DOCUMENT_ID_AMBIGUOUS);
//        }
//
//        if (Objects.nonNull(result.getEnvelopeId())) {
//            return mibApiExecutionService.executionResultWebhook(result.getEnvelopeId(), result);
//        }
//
//        if (Objects.nonNull(result.getOffenseId())) {
//            mibApiAdmService.execution(result.getOffenseId(), result);
//            return new MvdExecutionResponseDTO();
//        }
//
//        throw new ValidationException(ErrorCode.MIB_DOCUMENT_ID_EMPTY);
//    }

}