package uz.ciasev.ubdd_service.repository.invoice;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.ciasev.ubdd_service.entity.admcase.AdmCase;
import uz.ciasev.ubdd_service.entity.dict.Organ;
import uz.ciasev.ubdd_service.entity.invoice.Invoice;
import uz.ciasev.ubdd_service.entity.resolution.Resolution;
import uz.ciasev.ubdd_service.entity.resolution.decision.Decision;
import uz.ciasev.ubdd_service.entity.resolution.punishment.Punishment;
import uz.ciasev.ubdd_service.entity.violator.Violator;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findById(Long id);

    Optional<Invoice> findByInvoiceId(Long id);

    boolean existsByPenaltyPunishmentId(Long penaltyId);

    List<Invoice> findByPenaltyPunishmentId(Long penaltyId, Pageable page);

    boolean existsByCompensationId(Long compensationId);

    @Query("SELECT i.penaltyPunishment.punishment.decision.resolution.admCase.organ FROM Invoice i WHERE i = :invoice")
    Optional<Organ> findAdmCaseOrganByInvoiceInPenaltyPunishment(@Param("invoice") Invoice invoice);

    @Query("SELECT i.compensation.decision.resolution.admCase.organ FROM Invoice i WHERE i = :invoice")
    Optional<Organ> findAdmCaseOrganByInvoiceInCompensation(@Param("invoice") Invoice invoice);

    @Query("SELECT i.penaltyPunishment.punishment.decision.resolution.admCase FROM Invoice i WHERE i = :invoice")
    Optional<AdmCase> findAdmCaseByInvoiceInPenaltyPunishment(@Param("invoice") Invoice invoice);

    @Query("SELECT i.penaltyPunishment.punishment.decision.resolution FROM Invoice i WHERE i = :invoice")
    Optional<Resolution> findResolutionByInvoiceInPenaltyPunishment(@Param("invoice") Invoice invoice);

    @Query("SELECT i.penaltyPunishment.punishment FROM Invoice i WHERE i = :invoice")
    Optional<Punishment> findPunishmentByInvoiceInPenaltyPunishment(@Param("invoice") Invoice invoice);

    @Query("SELECT i.compensation.decision.resolution.admCase FROM Invoice i WHERE i = :invoice")
    Optional<AdmCase> findAdmCaseByInvoiceInCompensation(@Param("invoice") Invoice invoice);

    @Query("SELECT i.penaltyPunishment.punishment.decision FROM Invoice i WHERE i = :invoice")
    Optional<Decision> findDecisionByInvoiceInPenaltyPunishment(@Param("invoice") Invoice invoice);

    @Query("SELECT i.compensation.decision FROM Invoice i WHERE i = :invoice")
    Optional<Decision> findDecisionByInvoiceInCompensation(@Param("invoice") Invoice invoice);

    Optional<Invoice> findByInvoiceSerial(String invoiceSerial);

    @Query("SELECT i.id " +
            "FROM Invoice i " +
            "WHERE i.compensation.decision.violator = :violator")
    Collection<Long> findCompensationInvoiceIdByViolator(Violator violator);

    @Query("SELECT i.id " +
            "FROM Invoice i " +
            "WHERE i.penaltyPunishment.punishment.decision.violator = :violator")
    Collection<Long> findPunishmentInvoiceIdByViolator(Violator violator);

    @Query("SELECT i.id " +
            "FROM Invoice i " +
            "WHERE i.damageSettlementDetailId = :damageSettlementDetailId")
    Collection<Long> findInvoiceIdByDamageSettlementDetailId(Long damageSettlementDetailId);

    @Query("SELECT i.id " +
            "FROM Invoice i " +
            "WHERE i.penaltyPunishment.punishment.decision.violatorId = :violatorId")
    Collection<Long> findPunishmentInvoiceIdByViolatorId(Long violatorId);

    @Query("SELECT i.penaltyPunishment.punishment.decision FROM Invoice i WHERE i.invoiceSerial = :invoiceSerial")
    Optional<Decision> findDecisionByPenaltyInvoiceSerial(String invoiceSerial);

    @Query(value = "select p.id,i.invoice_serial from core_v0.protocol p  " +
            "left join core_v0.violator_detail vd on vd.id =p.violator_detail_id " +
            "left join core_v0.decision d on d.violator_id=vd.violator_id " +
            "left join core_v0.punishment pun on pun.decision_id =d.id " +
            "left join core_v0.penalty_punishment pp on pp.punishment_id=pun.id " +
            "left join core_v0.invoice i on i.penalty_punishment_id=pp.id " +
            "where p.id in (:ids) and i.is_active = true", nativeQuery = true)
    List<String[]> getInvoicesAndProtocols(List<Long> ids);

    @Query(value = "select i.* from core_v0.invoice i left join  " +
            "core_v0.penalty_punishment pp on pp.id =i.penalty_punishment_id " +
            "join core_v0.punishment p on p.id=pp.punishment_id  " +
            "join core_v0.decision d on d.id =p.decision_id " +
            "join core_v0.resolution r on r.id =d.resolution_id " +
            "where r.adm_case_id=:admCaseId limit 1",nativeQuery = true)
    Optional<Invoice> findInvoiceByAdmCase(Long admCaseId);

    @Query(value = "select  i.*  " +
            "from core_v0.protocol p  " +
            "join core_v0.violator_detail vd on vd.id = p.violator_detail_id  " +
            "join core_v0.violator v on v.id = vd.violator_id  " +
            "join core_v0.adm_case ac on ac.id = v.adm_case_id  " +
            "join core_v0.resolution r on r.adm_case_id = ac.id   " +
            "join core_v0.decision d on d.resolution_id = r.id   " +
            "join core_v0.punishment pun on pun.decision_id = d.id  " +
            "join core_v0.penalty_punishment pp on pp.punishment_id = pun.id " +
            "join core_v0.invoice i on i.penalty_punishment_id = pp.id " +
            "where p.external_id = :externalId " +
            "and p.organ_id = :organId  " +
            "and i.is_active = true ",nativeQuery = true)
    Optional<Invoice> findInvoiceByExternalIdAndOrganId(String externalId, Long organId);
}
