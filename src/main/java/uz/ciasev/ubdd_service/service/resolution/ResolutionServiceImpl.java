package uz.ciasev.ubdd_service.service.resolution;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.ciasev.ubdd_service.dto.internal.response.adm.resolution.CancellationResolutionListResponseDTO;
import uz.ciasev.ubdd_service.dto.internal.response.adm.resolution.ResolutionDetailResponseDTO;
import uz.ciasev.ubdd_service.dto.internal.response.adm.resolution.ResolutionListResponseDTO;
import uz.ciasev.ubdd_service.entity.Inspector;
import uz.ciasev.ubdd_service.entity.Place;
import uz.ciasev.ubdd_service.entity.admcase.AdmCase;
import uz.ciasev.ubdd_service.entity.court.CourtFile;
import uz.ciasev.ubdd_service.entity.resolution.cancellation.CancellationResolution;
import uz.ciasev.ubdd_service.entity.resolution.Resolution;
import uz.ciasev.ubdd_service.entity.resolution.decision.Decision;
import uz.ciasev.ubdd_service.entity.status.AdmStatus;
import uz.ciasev.ubdd_service.exception.notfound.EntityByIdNotFound;
import uz.ciasev.ubdd_service.exception.notfound.EntityByParamsNotFound;
import uz.ciasev.ubdd_service.repository.resolution.ResolutionRepository;
import uz.ciasev.ubdd_service.service.file.Category;
import uz.ciasev.ubdd_service.service.file.FileService;
import uz.ciasev.ubdd_service.service.generator.ResolutionNumberGeneratorService;
import uz.ciasev.ubdd_service.service.resolution.cancellation.CancellationResolutionService;
import uz.ciasev.ubdd_service.service.resolution.decision.DecisionService;
import uz.ciasev.ubdd_service.service.status.DecisionStatusCalculatingService;
import uz.ciasev.ubdd_service.service.user.UserDTOService;
import uz.ciasev.ubdd_service.utils.PageUtils;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResolutionServiceImpl implements ResolutionService {

    private final UserDTOService userDTOService;
    private final DecisionStatusCalculatingService admStatusDictionaryService;
    private final ResolutionRepository resolutionRepository;
    private final CancellationResolutionService cancellationResolutionService;
    private final FileService fileService;
    private final DecisionService decisionService;

    @Override
    public Resolution create(Inspector inspector, Place considerPlace, ResolutionNumberGeneratorService numberGeneratorService, AdmCase admCase, ResolutionCreateRequest resolution) {

        resolution.setAdmCase(admCase);

        resolution.setUser(inspector.getUser());

        resolution.setRegion(considerPlace.getRegion());
        resolution.setDistrict(considerPlace.getDistrict());
        resolution.setOrgan(considerPlace.getOrgan());
        resolution.setDepartment(considerPlace.getDepartment());

        resolution.setSeries("UBDD");
        resolution.setNumber(numberGeneratorService.generateNumber(resolution));

        resolution.setStatus(admStatusDictionaryService.getStartStatus(resolution));

        return resolutionRepository.saveAndFlush(new Resolution(resolution));
    }

    @Override
    public Resolution getById(Long id) {
        return resolutionRepository
                .findById(id)
                .orElseThrow(() -> new EntityByIdNotFound(Resolution.class, id));
    }

    @Override
    public String setPdfFile(Resolution resolution, byte[] fileContent) {

        String fileUrl = fileService.save(Category.RESOLUTION_PDF, resolution.getNumber() + ".pdf", fileContent);
//        resolution.setCourtDecisionUri(fileUrl);
//        resolutionRepository.saveAndFlush(resolution);
        resolutionRepository.setPdfFile(resolution, null, fileUrl);
        updated(resolution);

        return fileUrl;
    }

    @Override
    public void setCourtFile(Resolution resolution, CourtFile file) {
//        resolution.setCourtDecisionUri(file.getUri());
//        resolution.setFileId(file.getExternalId());
//        resolutionRepository.saveAndFlush(resolution);
        resolutionRepository.setPdfFile(resolution, file.getExternalId(), file.getUri());
        updated(resolution);
    }

    @Override
    public Optional<Resolution> findActiveByAdmCaseId(Long id) {
        return resolutionRepository.findActiveByAdmCaseId(id);
    }

//    @Override
//    public Resolution update(Long id, Resolution resolution) {
//        resolution.setId(id);
//        return resolutionRepository.save(resolution);
//    }

    @Override
    public Optional<Resolution> findLastCanceledByAdmCaseId(Long caseId) {
        Optional<CancellationResolution> lastCancellation = cancellationResolutionService.findLastByCaseId(caseId);
        return lastCancellation.map(c -> getById(c.getResolutionId()));

//        List<Resolution> inactiveResolutions = resolutionRepository.findInactiveByAdmCaseId(caseId);
//        return (inactiveResolutions == null || inactiveResolutions.isEmpty())
//                ? Optional.empty()
//                : Optional.ofNullable(inactiveResolutions.get(0));
    }

    @Override
    public ResolutionDetailResponseDTO findDetailById(Long id) {
        Resolution resolution = getById(id);
        CancellationResolution cancellationResolution = cancellationResolutionService.findByResolutionId(id).orElse(null);

        return new ResolutionDetailResponseDTO(
                resolution,
                cancellationResolution,
                Optional.ofNullable(resolution.getUserId()).map(userDTOService::findInspectorById).orElse(null)
        );
    }

    @Override
    public List<ResolutionListResponseDTO> findAllByAdmCaseId(Long id) {
        List<Resolution> resolutions = resolutionRepository.findAllByAdmCaseId(id);

        List<ResolutionListResponseDTO> response = new ArrayList<>();
        for (Resolution resolution : resolutions) {
            var cancellation = cancellationResolutionService
                    .findByResolutionId(resolution.getId())
                    .orElse(null);
            response.add(new ResolutionListResponseDTO(resolution, cancellation));
        }

        return response;
    }

    @Override
    public Optional<Resolution> findLastByCaseAndClaimIds(Long caseId, Long claimId) {
        return resolutionRepository.findAllByCaseAndClaimIds(caseId, claimId, PageUtils.oneWithMaxId())
                .stream()
                .findFirst();
    }

    @Override
    public List<Resolution> findAllByCaseAndClaimIds(Long caseId, Long claimId) {
        return resolutionRepository.findAllByCaseAndClaimIds(caseId, claimId, PageUtils.unpaged());
    }

    @Override
    public Resolution getByCaseAndClaimIds(Long caseId, Long claimId) {
        return findLastByCaseAndClaimIds(caseId, claimId)
                .orElseThrow(() -> new EntityByParamsNotFound(Resolution.class, "caseId", caseId, "claimId", caseId));
    }

    @Override
    public List<Resolution> findBySeriesAndNumber(String series, String number) {
        return resolutionRepository.findBySeriesAndNumber(series, number);
    }

    @Override
    public boolean existsBySeriesAndNumber(String series, String number) {
        return resolutionRepository.existsBySeriesAndNumber(series, number);
    }

    @Override
    public Resolution findByPunishmentId(Long punishmentId) {
        return resolutionRepository.findByPunishmentId(punishmentId)
                .orElseThrow(() -> new EntityByParamsNotFound(Resolution.class, "punishmentId", punishmentId));
    }

    @Override
    public Optional<Decision> getDecisionOfResolutionById(Long admCaseId) {
        return findAllByAdmCaseId(admCaseId).stream()
                .filter(ResolutionListResponseDTO::isActive).min((o1, o2) -> o2.getCreatedTime().compareTo(o1.getCreatedTime()))
                .flatMap(dto -> decisionService.findByResolutionId(dto.getId()).stream().findFirst());
    }

    @Override
    public boolean isResolutionActiveByPunishmentId(Long punishmentId) {
        return resolutionRepository.isActiveByPunishmentId(punishmentId);
    }

    @Override
    public boolean isResolutionActiveByCompensationId(Long compensationId) {
        return resolutionRepository.isActiveByCompensationId(compensationId);
    }

    @Override
    public Optional<Resolution> findPenaltyPunishmentByExternalIdAndOrganId(String externalId, Long organId) {
        return resolutionRepository.findResolutionByExternalIdAndOrganId(externalId, organId);
    }

    @Override
    public void updateStatus(Resolution resolution, AdmStatus admStatus, @Nullable LocalDate executedDate) {
        if (executedDate == null) {
            executedDate = resolution.getExecutedDate();
        }
        resolutionRepository.setStatus(resolution, admStatus, executedDate);
        updated(resolution);
    }

    @Override
    public void setIsActive(Resolution resolution, boolean isActive) {
        resolutionRepository.setIsActive(resolution, isActive);
        updated(resolution);
    }

    private CancellationResolutionListResponseDTO convertToCancellationDTO(CancellationResolution cancellationResolution) {
        return new CancellationResolutionListResponseDTO(cancellationResolution);
    }

    private void updated(Resolution resolution) {
        resolutionRepository.flush();
        resolutionRepository.refresh(resolution);
    }
}
