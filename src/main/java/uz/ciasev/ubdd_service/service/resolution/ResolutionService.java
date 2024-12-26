package uz.ciasev.ubdd_service.service.resolution;

import org.springframework.data.jpa.repository.Query;
import uz.ciasev.ubdd_service.dto.internal.response.adm.resolution.CancellationResolutionListResponseDTO;
import uz.ciasev.ubdd_service.dto.internal.response.adm.resolution.ResolutionDetailResponseDTO;
import uz.ciasev.ubdd_service.dto.internal.response.adm.resolution.ResolutionListResponseDTO;
import uz.ciasev.ubdd_service.entity.Inspector;
import uz.ciasev.ubdd_service.entity.Place;
import uz.ciasev.ubdd_service.entity.admcase.AdmCase;
import uz.ciasev.ubdd_service.entity.court.CourtFile;
import uz.ciasev.ubdd_service.entity.resolution.Resolution;
import uz.ciasev.ubdd_service.entity.resolution.decision.Decision;
import uz.ciasev.ubdd_service.entity.resolution.punishment.PenaltyPunishment;
import uz.ciasev.ubdd_service.entity.status.AdmStatus;
import uz.ciasev.ubdd_service.service.generator.ResolutionNumberGeneratorService;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ResolutionService {

    Resolution create(Inspector inspector, Place considerPlace, ResolutionNumberGeneratorService numberGeneratorService, AdmCase admCase, ResolutionCreateRequest resolution);

    String setPdfFile(Resolution resolution, byte[] fileContent);

    void setCourtFile(Resolution resolution, CourtFile file);

    void updateStatus(Resolution resolution, AdmStatus admStatus, @Nullable LocalDate executedDate);

    void setIsActive(Resolution resolution, boolean isActive);

    ResolutionDetailResponseDTO findDetailById(Long id);

    Resolution getById(Long id);

    List<ResolutionListResponseDTO> findAllByAdmCaseId(Long id);

    Optional<Resolution> findActiveByAdmCaseId(Long id);

    List<Resolution> findAllByCaseAndClaimIds(Long caseId, Long claimId);

    Optional<Resolution> findLastByCaseAndClaimIds(Long caseId, Long claimId);

    Resolution getByCaseAndClaimIds(Long caseId, Long claimId);

    Optional<Resolution> findLastCanceledByAdmCaseId(Long caseId);

    List<Resolution> findBySeriesAndNumber(String series, String number);

    Optional<Decision> getDecisionOfResolutionById(Long admCaseId);

    boolean existsBySeriesAndNumber(String series, String number);

    Resolution findByPunishmentId(Long punishmentId);

    boolean isResolutionActiveByPunishmentId(Long punishmentId);

    boolean isResolutionActiveByCompensationId(Long compensationId);

    Optional<Resolution> findPenaltyPunishmentByExternalIdAndOrganId(String externalId, Long organId);
}
