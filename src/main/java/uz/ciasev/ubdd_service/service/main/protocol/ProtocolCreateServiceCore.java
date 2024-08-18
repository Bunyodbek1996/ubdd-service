package uz.ciasev.ubdd_service.service.main.protocol;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ciasev.ubdd_service.dto.internal.request.protocol.ProtocolRequestDTO;
import uz.ciasev.ubdd_service.entity.protocol.Protocol;
import uz.ciasev.ubdd_service.entity.user.User;
import uz.ciasev.ubdd_service.repository.protocol.ProtocolRepository;
import uz.ciasev.ubdd_service.service.admcase.AdmCaseService;
import uz.ciasev.ubdd_service.service.aop.signature.DigitalSignatureCheck;
import uz.ciasev.ubdd_service.entity.signature.SignatureEvent;
import uz.ciasev.ubdd_service.service.aop.violator.ViolatorUpdateDueToProtocolCreation;
import uz.ciasev.ubdd_service.service.validation.ProtocolUniquenessValidationService;
import uz.ciasev.ubdd_service.service.validation.ProtocolValidationService;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProtocolCreateServiceCore implements ProtocolCreateService {

    private final AdmCaseService admCaseService;
    private final ProtocolRepository protocolRepository;
    private final ProtocolBaseCreateService baseCreateService;
    private final ProtocolUniquenessValidationService protocolUniquenessValidationService;
    private final ProtocolCreateAdditionalValidationService protocolCreateAdditionalValidationService;

    @Override
    @Transactional
    public Protocol createElectronProtocol(User user, ProtocolRequestDTO protocolDTO) {

        protocolCreateAdditionalValidationService.validateExternalId(protocolDTO);

        Protocol protocolByExternalId = findByExternalId(user, protocolDTO);
        if (protocolByExternalId != null) {
            return protocolByExternalId;
        }

        return baseCreateService.createProtocol(
                user,
                protocolDTO,
                () -> admCaseService.createEmptyAdmCase(user, user.getOrgan(), null, protocolDTO.getRegion(), protocolDTO.getDistrict())
        );
    }


    private Protocol findByExternalId(User user, ProtocolRequestDTO protocolDTO) {
        if (protocolDTO.getExternalId() == null) {
            return null;
        }
        return protocolRepository.findByExternalIdAndOrganId(protocolDTO.getExternalId(), user.getOrganId()).orElse(null);
    }

}
