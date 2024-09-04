package uz.ciasev.ubdd_service.service.mib;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.ciasev.ubdd_service.dto.internal.request.mib.MibCardRequestDTO;
import uz.ciasev.ubdd_service.entity.Address;
import uz.ciasev.ubdd_service.entity.admcase.AdmCase;
import uz.ciasev.ubdd_service.entity.dict.District;
import uz.ciasev.ubdd_service.entity.dict.Region;
import uz.ciasev.ubdd_service.entity.mib.MibAutoSendLog;
import uz.ciasev.ubdd_service.entity.mib.MibExecutionCard;
import uz.ciasev.ubdd_service.entity.resolution.decision.Decision;
import uz.ciasev.ubdd_service.exception.NotFoundException;
import uz.ciasev.ubdd_service.mvd_core.api.mib.api.types.MibResult;
import uz.ciasev.ubdd_service.repository.mib.MibAutoSendLogRepository;
import uz.ciasev.ubdd_service.service.address.AddressService;
import uz.ciasev.ubdd_service.service.dict.DistrictDictionaryService;
import uz.ciasev.ubdd_service.service.dict.RegionDictionaryService;
import uz.ciasev.ubdd_service.service.resolution.ResolutionService;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UbddMibAutoSendServiceImpl implements UbddMibAutoSendService {

    private final MibCardService mibCardService;
    private final SendToMibService mibService;
    private final MibAutoSendLogRepository mibAutoSendLogRepository;
    private final AddressService addressService;
    private final DistrictDictionaryService districtService;
    private final RegionDictionaryService regionService;
    private final ResolutionService resolutionService;


    @Override
    public void send(Long admCaseId, MibResult mibResult) {

        Decision decision = resolutionService.getDecisionOfResolutionById(admCaseId).orElseThrow(() -> new NotFoundException("Decision not found for admCaseId=" + admCaseId));

        mibResult.setDecisionId(decision.getId());

        MibExecutionCard executionCard;

        executionCard = executionCardByDecision(decision);

        MibAutoSendLog mibAutoSendLog = new MibAutoSendLog(executionCard);

        process(executionCard, mibResult);

        mibAutoSendLogRepository.save(mibAutoSendLog);
    }

    private MibExecutionCard executionCardByDecision(Decision decision) {
        Optional<MibExecutionCard> executionCardOptional = mibCardService.findByDecisionId(decision.getId());

        return executionCardOptional.orElseGet(() ->
                generateCardByDecision(decision)
        );
    }

    private MibExecutionCard generateCardByDecision(Decision decision) {
        Address actualAddress = addressService.findById(decision.getViolator().getActualAddressId());
        Region region = actualAddress.getRegionIdOpt().map(regionService::getById).orElseThrow(() -> new NotFoundException("Card generation failed: region of violator's actual address is null"));
        District district = actualAddress.getDistrictIdOpt().map(districtService::getById).orElseThrow(() -> new NotFoundException("Card generation failed: district of violator's actual address is null"));

        return mibCardService.openCardForDecision(
                null,
                decision,
                MibCardRequestDTO.builder()
                        .region(region)
                        .district(district)
                        .build()
        );
    }

    private void process(MibExecutionCard executionCard, MibResult mibResult) {
        mibService.doSend(executionCard, null, mibResult);
    }
}
