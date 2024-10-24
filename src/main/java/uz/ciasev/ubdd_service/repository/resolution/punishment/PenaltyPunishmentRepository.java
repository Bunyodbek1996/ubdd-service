package uz.ciasev.ubdd_service.repository.resolution.punishment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.ciasev.ubdd_service.entity.resolution.punishment.PenaltyPunishment;

import java.util.Optional;

public interface PenaltyPunishmentRepository extends JpaRepository<PenaltyPunishment, Long> {

    @Query(value = "SELECT pp.* " +
            "FROM core_v0.protocol p " +
            "JOIN core_v0.violator_detail vd ON vd.id = p.violator_detail_id " +
            "JOIN core_v0.violator v ON v.id = vd.violator_id " +
            "JOIN core_v0.adm_case ac ON ac.id = v.adm_case_id " +
            "JOIN core_v0.resolution r ON r.adm_case_id = ac.id " +
            "JOIN core_v0.decision d ON d.resolution_id = r.id " +
            "JOIN core_v0.punishment pun ON pun.decision_id = d.id " +
            "JOIN core_v0.penalty_punishment pp ON pp.punishment_id = pun.id " +
            "WHERE p.external_id = :externalId " +
            "AND p.organ_id = :organId", nativeQuery = true)
    Optional<PenaltyPunishment> findPenaltyPunishmentByExternalIdAndOrganId(String externalId, Long organId);

    @Query(value = "SELECT pp.* " +
            "FROM core_v0.resolution r " +
            "JOIN core_v0.decision d ON d.resolution_id = r.id " +
            "JOIN core_v0.punishment pun ON pun.decision_id = d.id " +
            "JOIN core_v0.penalty_punishment pp ON pp.punishment_id = pun.id " +
            "WHERE r.adm_case_id = :admCaseId", nativeQuery = true)
    Optional<PenaltyPunishment> findPenaltyPunishmentByAdmCaseId(Long admCaseId);


}
