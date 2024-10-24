package uz.ciasev.ubdd_service.dto.internal.request.resolution.organ;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.apache.xpath.operations.Bool;
import uz.ciasev.ubdd_service.dto.internal.request.ArticleRequest;
import uz.ciasev.ubdd_service.dto.internal.request.resolution.DecisionRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.resolution.PunishmentRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.resolution.ResolutionRequestDTO;
import uz.ciasev.ubdd_service.entity.dict.Department;
import uz.ciasev.ubdd_service.entity.dict.District;
import uz.ciasev.ubdd_service.entity.dict.Region;
import uz.ciasev.ubdd_service.entity.dict.article.Article;
import uz.ciasev.ubdd_service.entity.dict.article.ArticlePart;
import uz.ciasev.ubdd_service.entity.dict.article.ArticleViolationType;
import uz.ciasev.ubdd_service.entity.dict.resolution.DecisionType;
import uz.ciasev.ubdd_service.entity.dict.resolution.DecisionTypeAlias;
import uz.ciasev.ubdd_service.entity.dict.resolution.TerminationReason;
import uz.ciasev.ubdd_service.entity.resolution.decision.Decision;
import uz.ciasev.ubdd_service.entity.dict.BankAccountType;
import uz.ciasev.ubdd_service.exception.ErrorCode;
import uz.ciasev.ubdd_service.service.resolution.ResolutionCreateRequest;
import uz.ciasev.ubdd_service.utils.validator.ActiveOnly;
import uz.ciasev.ubdd_service.utils.validator.ValidDecision;
import uz.ciasev.ubdd_service.utils.validator.ValidPunishment;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@ValidDecision
public class SingleResolutionRequestDTO implements DecisionRequestDTO, ResolutionRequestDTO, ArticleRequest {

    private Long externalId;

    @NotNull(message = "createdByEmi is required")
    private Boolean createdByEmi;

    private Long admCaseId;

    @NotNull(message = "CONSIDER_USER_INFO_REQUIRED")
    private String considerUserInfo;

    @NotNull(message = "CONSIDER_USER_INFO_REQUIRED")
    private Long inspectorPositionId;

    @NotNull(message = "CONSIDER_USER_INFO_REQUIRED")
    private Long inspectorRankId;

    @NotNull(message = "CONSIDER_USER_INFO_REQUIRED")
    private String inspectorWorkCertificate;

    private Boolean isJuridic;

    @JsonProperty(value = "articleId")
    private Article article;

    @JsonProperty(value = "articlePartId")
    private ArticlePart articlePart;

    @JsonProperty(value = "articleViolationTypeId")
    private ArticleViolationType articleViolationType;

    private List<Long> repeatabilityProtocolsId;

    @JsonIgnore
    private Long violatorId;

    @NotNull(message = "RESOLUTION_TIME_REQUIRED")
    private LocalDateTime resolutionTime;

    @Getter(AccessLevel.NONE)
    @NotNull(message = ErrorCode.DECISION_TYPE_ID_REQUIRED)
    @ActiveOnly(message = ErrorCode.DECISION_TYPE_DEACTIVATED)
    @JsonProperty(value = "decisionTypeId")
    private DecisionType decisionTypeObj;
    @JsonIgnore
    private DecisionTypeAlias decisionType;

    @ActiveOnly(message = ErrorCode.TERMINATION_REASON_DEACTIVATED)
    @JsonProperty(value = "terminationReasonId")
    private TerminationReason terminationReason;

    private LocalDate executionFromDate;

    @Valid
    @ValidPunishment(message = ErrorCode.MAIN_PUNISHMENT_INVALID)
    private OrganPunishmentRequestDTO mainPunishment;

    @Valid
    @ValidPunishment(message = ErrorCode.ADDITION_PUNISHMENT_INVALID)
    private OrganPunishmentRequestDTO additionPunishment;

    private String signature;

    @JsonProperty(value = "districtId")
    private District district;

    @JsonProperty(value = "regionId")
    private Region region;

    @JsonProperty(value = "departmentId")
    private Department department;

    @NotNull(message = "IS_ARTICLE_33_REQUIRED")
    private Boolean isArticle33;

    @NotNull(message = "IS_ARTICLE_34_REQUIRED")
    private Boolean isArticle34;

    public Decision buildDecision() {
        Decision decision = new Decision();

        decision.setDecisionTypeAlias(this.getDecisionType());
        decision.setTerminationReason(this.getTerminationReason());
        decision.setExecutionFromDate(this.getExecutionFromDate());
        decision.setArticle33(isArticle33);
        decision.setArticle34(isArticle34);

        if (articlePart != null) {
            decision.setIsJuridic(this.getIsJuridic());
            decision.setArticle(this.article);
            decision.setArticlePart(this.articlePart);
            decision.setArticleViolationType(this.articleViolationType);
        }

        Optional.ofNullable(this.mainPunishment)
                .map(PunishmentRequestDTO::buildPunishment)
                .map(p -> {
                    p.setMain(true);
                    return p;
                })
                .ifPresent(decision::setMainPunishment);


        Optional.ofNullable(this.additionPunishment)
                .map(PunishmentRequestDTO::buildPunishment)
                .ifPresent(decision::setAdditionPunishment);

        return decision;
    }

    public ResolutionCreateRequest buildResolution() {
        ResolutionCreateRequest resolution = new ResolutionCreateRequest();

        resolution.setConsiderSignature(this.signature);
        resolution.setResolutionTime(this.resolutionTime);
        resolution.setConsiderUserInfo(this.considerUserInfo);
        resolution.setInspectorWorkCertificate(this.inspectorWorkCertificate);

        return resolution;
    }

    @Override
    public List<? extends DecisionRequestDTO> getDecisions() {
        return List.of(this);
    }


    @Override
    public LocalDate getExecutionFromDate() {
        return Optional.ofNullable(this.executionFromDate).orElseGet(LocalDate::now);
    }
}
