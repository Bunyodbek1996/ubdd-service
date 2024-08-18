package uz.ciasev.ubdd_service.entity.resolution;

import lombok.*;
import uz.ciasev.ubdd_service.entity.AdmEntity;
import uz.ciasev.ubdd_service.entity.EntityNameAlias;
import uz.ciasev.ubdd_service.entity.Place;
import uz.ciasev.ubdd_service.entity.admcase.AdmCase;
import uz.ciasev.ubdd_service.entity.dict.Department;
import uz.ciasev.ubdd_service.entity.dict.District;
import uz.ciasev.ubdd_service.entity.dict.Organ;
import uz.ciasev.ubdd_service.entity.dict.Region;
import uz.ciasev.ubdd_service.entity.status.AdmStatus;
import uz.ciasev.ubdd_service.entity.user.User;
import uz.ciasev.ubdd_service.service.resolution.ResolutionCreateRequest;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "resolution")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id"})
@EqualsAndHashCode(of = {"id"})
public class Resolution implements AdmEntity, Place, Serializable {

    @Column(updatable = false)
    private Boolean isStatusSync = false;

    @Column(updatable = false)
    private String statusSyncMessage;

    @Getter
    @Transient
    private final EntityNameAlias entityNameAlias = EntityNameAlias.RESOLUTION;

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resolution_id_seq")
    @SequenceGenerator(name = "resolution_id_seq", sequenceName = "resolution_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Getter
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @Getter
    @Column(nullable = false)
    private LocalDateTime editedTime;

    @Getter
    @ManyToOne
    @JoinColumn(name = "adm_case_id", updatable = false)
    private AdmCase admCase;

    @Getter
    @Column(updatable = false)
    private boolean isActive;

    @Getter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "adm_status_id", updatable = false)
    private AdmStatus status;

    @Getter
    @Column(updatable = false)
    private String series;

    @Getter
    @Column(updatable = false)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false)
    private User user;

    @Getter
    @Column(updatable = false)
    private String considerInfo;

    @Getter
    @Column(updatable = false)
    private String considerSignature;

    @Getter
    @ManyToOne
    @JoinColumn(name = "organ_id", updatable = false)
    private Organ organ;

    @Getter
    @ManyToOne
    @JoinColumn(name = "department_id", updatable = false)
    private Department department;

    @Getter
    @ManyToOne
    @JoinColumn(name = "region_id", updatable = false)
    private Region region;

    @Getter
    @ManyToOne
    @JoinColumn(name = "district_id", updatable = false)
    private District district;

    @Getter
    @Column(updatable = false)
    private LocalDateTime resolutionTime;

    @Getter
    @Column(updatable = false)
    private LocalDate executedDate;

    @Getter
    @Column(updatable = false)
    private boolean isSimplified;


    // Поля внешних систем
    @Getter
    @Column(updatable = false)
    private Long claimId;

    @Getter
    @Column(updatable = false)
    private Long fileId;

    @Getter
    @Column(updatable = false)
    private String courtDecisionUri;

    @Getter
    private String inspectorWorkCertificate;

    @Getter
    private String inspectorPosition;

    @Getter
    private String inspectorRank;


    // УДОБНЫЕ ПОЛЯ

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @Column(name = "adm_case_id", insertable = false, updatable = false)
    private Long admCaseId;

    @Column(name = "adm_status_id", insertable = false, updatable = false)
    private Long statusId;
    @Column(name = "organ_id", insertable = false, updatable = false)
    private Long organId;

    @Column(name = "department_id", insertable = false, updatable = false)
    private Long departmentId;

    @Column(name = "region_id", insertable = false, updatable = false)
    private Long regionId;

    @Column(name = "district_id", insertable = false, updatable = false)
    private Long districtId;

    public Resolution(ResolutionCreateRequest request) {

        this.isStatusSync = false;
        this.statusSyncMessage = null;
        this.isActive = true;
        this.createdTime = LocalDateTime.now();
        this.editedTime = LocalDateTime.now();

        this.admCase = request.getAdmCase();
        this.status = request.getStatus();
        this.series = request.getSeries();
        this.number = request.getNumber();
        this.user = request.getUser();

        this.considerInfo = request.getConsiderUserInfo();
        this.inspectorPosition = request.getInspectorPosition();
        this.inspectorRank = request.getInspectorRank();
        this.inspectorWorkCertificate = request.getInspectorWorkCertificate();

        this.considerSignature = request.getConsiderSignature();
        this.organ = request.getOrgan();
        this.department = request.getDepartment();
        this.region = request.getRegion();
        this.district = request.getDistrict();
        this.resolutionTime = request.getResolutionTime();
        this.executedDate = request.getExecutedDate();
        this.isSimplified = request.isSimplified();
        this.claimId = request.getClaimId();
        this.fileId = request.getFileId();
        this.courtDecisionUri = request.getCourtDecisionUri();
    }

    public Long getUserId() {
        if (user == null) return null;
        return user.getId();
    }

    public Long getAdmCaseId() {
        if (admCase == null) return null;
        return admCase.getId();
    }

    public Long getStatusId() {
        if (status == null) return null;
        return status.getId();
    }

    public Long getOrganId() {
        if (organ == null) return null;
        return organ.getId();
    }

    public Long getDepartmentId() {
        if (department == null) return null;
        return department.getId();
    }

    public Long getRegionId() {
        if (region == null) return null;
        return region.getId();
    }

    public Long getDistrictId() {
        if (district == null) return null;
        return district.getId();
    }

//    public static class MutationHelper {
//
//        public static void setPdfFile(Resolution resolution, Long fileId, String uri) {
//            resolution.fileId = fileId;
//            resolution.courtDecisionUri = uri;
//        }
//
//
//        public static void setIsActive(Resolution resolution, boolean isActive) {
//            resolution.isActive = isActive;
//        }
//    }

}
