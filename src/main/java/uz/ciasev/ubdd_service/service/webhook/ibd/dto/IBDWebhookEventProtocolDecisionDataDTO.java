package uz.ciasev.ubdd_service.service.webhook.ibd.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import uz.ciasev.ubdd_service.entity.webhook.ibd.IBDWebhookAddressProjection;
import uz.ciasev.ubdd_service.entity.webhook.ibd.IBDWebhookArticlesProjection;
import uz.ciasev.ubdd_service.entity.webhook.ibd.IBDWebhookProtocolDecisionProjection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class IBDWebhookEventProtocolDecisionDataDTO {
    private final Long protocolId;
    private final Long caseId;
    private final Long caseStatusId;
    private final String caseStatusName;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private final LocalDateTime createdTime;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private final LocalDateTime violationTime;

    private final String protocolSeries;
    private final String protocolNumber;
    private final String fabula;

    private final Long regionId;
    private final String regionName;
    private final Long districtId;
    private final String  districtName;
    private final Long mtpId;
    private final String mtpName;

    private final Long mainPunishmentTypeId;
    private final String mainPunishmentAmount;

    private final Long registeredOrganId;
    private final Organ registeredOrgan;
    private final Violator violatorDetail;
    private List<Article> articles;

    public IBDWebhookEventProtocolDecisionDataDTO(IBDWebhookProtocolDecisionProjection p) {
        this.violatorDetail = new Violator(p);
        this.registeredOrgan = new Organ(p);
        this.createdTime = p.getCreatedTime();
        this.violationTime = p.getCreatedTime();
        this.protocolId = p.getProtocolId();
        this.caseId = p.getAdmCaseId();
        this.caseStatusId = p.getCaseStatusId();
        this.caseStatusName = p.getCaseStatusName();
        this.protocolNumber = p.getProtocolNumber();
        this.protocolSeries = p.getProtocolSeries();
        this.fabula = p.getFabula();
        this.regionName = p.getRegionValue();
        this.districtName = p.getDistrictValue();
        this.mtpName = p.getMtpValue();
        this.mainPunishmentAmount = p.getMainPunishmentAmount();

        this.regionId = p.getRegionId();
        this.districtId = p.getDistrictId();
        this.mtpId = p.getMtpId();
        this.registeredOrganId = p.getRegisteredOrganId();
        this.mainPunishmentTypeId = p.getMainPunishmentTypeId();
    }
// #Organ Class
    @Getter
    private class Organ {
        private final String name;
        private final Long regionId;
        private final Long districtId;
        private final String inspectorFio;
        private final String inspectorPinpp;

        public Organ(IBDWebhookProtocolDecisionProjection projection) {
            this.name = projection.getOrganValue();
            this.inspectorFio = projection.getInspectorFio();
            this.inspectorPinpp = projection.getFormattedInspectorPinpp();

            this.regionId = projection.getOrganRegionId();
            this.districtId = projection.getOrganDistrictId();
        }
    }
// #Violator class
    @Getter
    private class Violator {
        private final String lastNameLat;
        private final String firstNameLat;
        private final String secondNameLat;
        private final String lastNameKir;
        private final String firstNameKir;
        private final String secondNameKir;
        private final String pinpp;
        private final String gender;
        private final Long genderId;
        private final Long nationalityTypeId;
        private final String occupationName;
        private final String employmentPosition;
        private final String employmentPlace;
        private final String mobile;
        private final Long citizenshipTypeId;
        @Setter
        private Long citizenCountryId;
        private final Long documentTypeId;
        private final String documentSeries;
        private final String documentNumber;
        @JsonFormat(pattern = "dd-MM-yyyy")
        private final LocalDate birthDate;
        @JsonFormat(pattern = "dd-MM-yyyy")
        private final LocalDate documentGivenDate;
        @JsonFormat(pattern = "dd-MM-yyyy")
        private final LocalDate documentExpireDate;
        @Setter
        private Address birthAddress;
        @Setter
        private Address residenceAddress;
        @Setter
        private Address documentGivenAddress;

        public Violator(IBDWebhookProtocolDecisionProjection projection) {
            this.lastNameLat = projection.getViolatorLastNameLat();
            this.firstNameLat = projection.getViolatorFirstNameLat();
            this.secondNameLat = projection.getViolatorSecondNameLat();
            this.lastNameKir = projection.getViolatorLastNameKir();
            this.firstNameKir = projection.getViolatorFirstNameKir();
            this.secondNameKir = projection.getViolatorSecondNameKir();
            this.pinpp = projection.getFormattedViolatorPinpp();
            this.occupationName = projection.getViolatorOccupationName();
            this.employmentPosition = projection.getViolatorEmploymentPosition();
            this.employmentPlace = projection.getViolatorEmploymentPlace();
            this.mobile = projection.getViolatorMobile();
            this.documentSeries = projection.getViolatorDocumentSeries();
            this.documentNumber = projection.getViolatorDocumentNumber();
            this.birthDate = projection.getViolatorBirthDate();
            this.documentGivenDate = projection.getViolatorDocumentGivenDate();
            this.documentExpireDate = projection.getViolatorDocumentExpireDate();
            this.gender = projection.getViolatorGenderName();

            this.genderId = projection.getGenderId();
            this.documentTypeId = projection.getDocumentTypeId();
            this.nationalityTypeId = projection.getNationalityTypeId();
            this.citizenshipTypeId = projection.getCitizenshipTypeId();
        }
    }
// #Address class
    @Data
    private class Address {
        private final String fullAddressText;
        private final Long countryId;
        private final Long regionId;
        private final Long districtId;

        public Address(IBDWebhookAddressProjection projection) {
            this.fullAddressText = projection.getFullAddressText();
            this.countryId = projection.getCountryId();
            this.regionId = projection.getRegionId();
            this.districtId = projection.getDistrictId();
        }
    }

    public void addArticle(IBDWebhookArticlesProjection projection) {
        if (articles == null) articles = new ArrayList<>();
        articles.add(new Article(projection));
    }


    public void setViolatorBirthAddress(IBDWebhookAddressProjection address) {
        this.violatorDetail.setBirthAddress(new Address(address));
    }

    public void setViolatorResidenceAddress(IBDWebhookAddressProjection address) {
        this.violatorDetail.setResidenceAddress(new Address(address));
    }

    public void setDocumentGivenAddress(IBDWebhookAddressProjection address) {
        this.violatorDetail.setDocumentGivenAddress(new Address(address));
        this.violatorDetail.setCitizenCountryId(address.getCountryId());
    }
// #Article class
    @Data
    private class Article {
        private final Long articleId;
        private final String articleName;
        private final Long articlePartId;
        private final String articlePartName;
        private final Long violationTypeId;
        private final String violationTypeName;

        public Article(IBDWebhookArticlesProjection projection) {
            this.articleId = projection.getArticleId();
            this.articleName = projection.getArticleName();
            this.articlePartId = projection.getArticlePartId();
            this.articlePartName = projection.getArticlePartShortName();
            this.violationTypeId = projection.getArticleViolationTypeId();
            this.violationTypeName = projection.getArticleViolationTypeShortName();
        }
    }



}