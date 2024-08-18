package uz.ciasev.ubdd_service.service.resolution;

import lombok.Data;
import uz.ciasev.ubdd_service.entity.admcase.AdmCase;
import uz.ciasev.ubdd_service.entity.dict.Department;
import uz.ciasev.ubdd_service.entity.dict.District;
import uz.ciasev.ubdd_service.entity.dict.Organ;
import uz.ciasev.ubdd_service.entity.dict.Region;
import uz.ciasev.ubdd_service.entity.status.AdmStatus;
import uz.ciasev.ubdd_service.entity.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ResolutionCreateRequest {
    private AdmCase admCase;
    private AdmStatus status;
    private String series;
    private String number;
    private User user;
    private String considerInfo;
    private String considerSignature;
    private Organ organ;
    private Department department;
    private Region region;
    private District district;
    private LocalDateTime resolutionTime;
    private LocalDate executedDate;
    private boolean isSimplified;
    private Long claimId;
    private Long fileId;
    private String courtDecisionUri;

    private String considerUserInfo;
    private String inspectorPosition;
    private String inspectorRank;
    private String inspectorWorkCertificate;
}
