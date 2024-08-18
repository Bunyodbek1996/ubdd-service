package uz.ciasev.ubdd_service.service.status;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ciasev.ubdd_service.entity.admcase.AdmCase;
import uz.ciasev.ubdd_service.entity.court.CourtMaterial;
import uz.ciasev.ubdd_service.entity.status.AdmStatusAlias;
import uz.ciasev.ubdd_service.event.AdmEventService;
import uz.ciasev.ubdd_service.event.AdmEventType;
import uz.ciasev.ubdd_service.repository.admcase.AdmCaseRepository;
import uz.ciasev.ubdd_service.repository.court.CourtMaterialRepository;

@Service
@RequiredArgsConstructor
public class AdmCaseStatusService {

    private final AdmStatusDictionaryService admStatusDictionaryService;
    private final AdmEventService admEventService;
    private final CourtMaterialRepository courtMaterialRepository;
    private final AdmCaseRepository admCaseRepository;


    @Transactional
    public AdmCase setStatus(AdmCase admCase, AdmStatusAlias statusAlias) {
        admCase.setStatus(admStatusDictionaryService.findByAlias(statusAlias));
        admEventService.fireEvent(AdmEventType.ADM_CASE_STATUS_CHANGE, admCase);
        return admCase;
    }
    @Transactional
    public AdmCase setStatusAndSave(AdmCase admCase, AdmStatusAlias statusAlias) {
        setStatus(admCase, statusAlias);
        return admCaseRepository.saveAndFlush(admCase);
    }

    @Transactional
    public CourtMaterial setStatusAndSave(CourtMaterial material, AdmStatusAlias statusAlias) {
        material.setStatus(admStatusDictionaryService.findByAlias(statusAlias));
        return courtMaterialRepository.saveAndFlush(material);
    }
}
