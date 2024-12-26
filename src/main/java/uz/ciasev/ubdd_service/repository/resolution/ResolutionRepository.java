package uz.ciasev.ubdd_service.repository.resolution;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.ciasev.ubdd_service.entity.resolution.Resolution;
import uz.ciasev.ubdd_service.entity.resolution.punishment.PenaltyPunishment;
import uz.ciasev.ubdd_service.entity.status.AdmStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ResolutionRepository extends JpaRepository<Resolution, Long>, ResolutionCustomRepository {

    @Modifying
    @Query("update Resolution d set d.isStatusSync = true , d.statusSyncMessage = :message , d.statusId = :statusId " +
            "where d.id = :id")
    void setStatusSynced(@Param("id") Long id, String message, @Param("statusId") Long statusId);

    @Modifying
    @Query("UPDATE Resolution r SET r.isActive = :isActive, r.editedTime = now() WHERE r = :resolution ")
    void setIsActive(Resolution resolution, boolean isActive);

    @Modifying
    @Query("UPDATE Resolution r SET r.courtDecisionUri = :uri, r.fileId = :fileId, r.editedTime = now() WHERE r = :resolution ")
    void setPdfFile(Resolution resolution, Long fileId, String uri);

    @Modifying
    @Query("UPDATE Resolution r SET r.executedDate = :executedDate, r.status = :status, r.editedTime = now() WHERE r = :resolution ")
    void setStatus(Resolution resolution, AdmStatus status, LocalDate executedDate);

    @Query("SELECT r " +
            " FROM Resolution r " +
            "WHERE r.admCaseId = :admCaseId " +
            "  AND r.isActive = TRUE")
    Optional<Resolution> findActiveByAdmCaseId(@Param("admCaseId") Long admCaseId);

    List<Resolution> findAllByAdmCaseId(@Param("admCaseId") Long admCaseId);

    @Query("SELECT r " +
            " FROM Resolution r " +
            "WHERE r.admCaseId = :caseId " +
            "  AND r.claimId = :claimId ")
    List<Resolution> findAllByCaseAndClaimIds(Long caseId, Long claimId, Pageable page);

    @Query("SELECT r " +
            " FROM Resolution r " +
            "WHERE r.admCaseId = :caseId " +
            "  AND r.isActive = FALSE " +
            "ORDER BY r.createdTime DESC")
    List<Resolution> findInactiveByAdmCaseId(@Param("caseId") Long caseId);

    List<Resolution> findBySeriesAndNumber(String series, String number);

    boolean existsBySeriesAndNumber(String series, String number);

    @Query("SELECT pu.decision.resolution " +
            "FROM Punishment pu " +
            "WHERE pu.id = :punishmentId")
    Optional<Resolution> findByPunishmentId(Long punishmentId);

    @Query("SELECT c.decision.resolution " +
            "FROM Compensation c " +
            "WHERE c.id = :compensationId")
    Optional<Resolution> findByCompensationId(Long compensationId);

    @Query("SELECT pu.decision.resolution.isActive " +
            "FROM Punishment pu " +
            "WHERE pu.id = :punishmentId")
    Boolean isActiveByPunishmentId(Long punishmentId);

    @Query("SELECT c.decision.resolution.isActive " +
            "FROM Compensation c " +
            "WHERE c.id = :compensationId")
    Boolean isActiveByCompensationId(Long compensationId);

    @Query(value = "select d.id " +
            "from core_v0.resolution d " +
            "where d.is_status_sync is null " +
            "limit :n ", nativeQuery = true)
    List<Long> getNextNForStatusSync(Long n);

    @Query(value = "SELECT pp.* " +
            "FROM core_v0.protocol p " +
            "JOIN core_v0.violator_detail vd ON vd.id = p.violator_detail_id " +
            "JOIN core_v0.violator v ON v.id = vd.violator_id " +
            "JOIN core_v0.adm_case ac ON ac.id = v.adm_case_id " +
            "JOIN core_v0.resolution r ON r.adm_case_id = ac.id and r.is_active = true " +
            "WHERE p.external_id = :externalId " +
            "AND p.organ_id = :organId", nativeQuery = true)
    Optional<Resolution> findResolutionByExternalIdAndOrganId(String externalId, Long organId);
}
