package uz.ciasev.ubdd_service.migration;

import com.opencsv.bean.CsvCustomBindByName;

public class ProtocolData {

    @CsvCustomBindByName(column = "protocol_externalId", converter = NullValueConverter.class)
    private String protocol_externalId;

    @CsvCustomBindByName(column = "protocol_inspectorRegionId", converter = NullValueConverter.class)
    private String protocol_inspectorRegionId;

    @CsvCustomBindByName(column = "protocol_inspectorDistrictId", converter = NullValueConverter.class)
    private String protocol_inspectorDistrictId;

    @CsvCustomBindByName(column = "protocol_inspectorPositionId", converter = NullValueConverter.class)
    private String protocol_inspectorPositionId;

    @CsvCustomBindByName(column = "protocol_inspectorRankId", converter = NullValueConverter.class)
    private String protocol_inspectorRankId;

    @CsvCustomBindByName(column = "protocol_inspectorFio", converter = NullValueConverter.class)
    private String protocol_inspectorFio;

    @CsvCustomBindByName(column = "protocol_inspectorInfo", converter = NullValueConverter.class)
    private String protocol_inspectorInfo;

    @CsvCustomBindByName(column = "protocol_inspectorWorkCertificate", converter = NullValueConverter.class)
    private String protocol_inspectorWorkCertificate;

    @CsvCustomBindByName(column = "protocol_registrationTime", converter = NullValueConverter.class)
    private String protocol_registrationTime;

    @CsvCustomBindByName(column = "protocol_violationTime", converter = NullValueConverter.class)
    private String protocol_violationTime;

    @CsvCustomBindByName(column = "protocol_articleId", converter = NullValueConverter.class)
    private String protocol_articleId;

    @CsvCustomBindByName(column = "protocol_articlePartId", converter = NullValueConverter.class)
    private String protocol_articlePartId;

    @CsvCustomBindByName(column = "protocol_fabula", converter = NullValueConverter.class)
    private String protocol_fabula;

    @CsvCustomBindByName(column = "protocol_regionId", converter = NullValueConverter.class)
    private String protocol_regionId;

    @CsvCustomBindByName(column = "protocol_districtId", converter = NullValueConverter.class)
    private String protocol_districtId;

    @CsvCustomBindByName(column = "protocol_mtpId", converter = NullValueConverter.class)
    private String protocol_mtpId;

    @CsvCustomBindByName(column = "mbprotocol_address", converter = NullValueConverter.class)
    private String mbprotocol_address;

    @CsvCustomBindByName(column = "protocol_isFamiliarize", converter = NullValueConverter.class)
    private String protocol_isFamiliarize;

    @CsvCustomBindByName(column = "protocol_isAgree", converter = NullValueConverter.class)
    private String protocol_isAgree;

    @CsvCustomBindByName(column = "protocol_violator_pinpp", converter = NullValueConverter.class)
    private String protocol_violator_pinpp;

    @CsvCustomBindByName(column = "protocol_violator_mobile", converter = NullValueConverter.class)
    private String protocol_violator_mobile;

    @CsvCustomBindByName(column = "protocol_violator_landline", converter = NullValueConverter.class)
    private String protocol_violator_landline;

    @CsvCustomBindByName(column = "protocol_violator_actualAddress_countryId", converter = NullValueConverter.class)
    private String protocol_violator_actualAddress_countryId;

    @CsvCustomBindByName(column = "protocol_violator_actualAddress_regionId", converter = NullValueConverter.class)
    private String protocol_violator_actualAddress_regionId;

    @CsvCustomBindByName(column = "protocol_violator_actualAddress_districtId", converter = NullValueConverter.class)
    private String protocol_violator_actualAddress_districtId;

    @CsvCustomBindByName(column = "protocol_violator_actualAddress_address", converter = NullValueConverter.class)
    private String protocol_violator_actualAddress_address;

    @CsvCustomBindByName(column = "protocol_violator_postAddress_countryId", converter = NullValueConverter.class)
    private String protocol_violator_postAddress_countryId;

    @CsvCustomBindByName(column = "protocol_violator_postAddress_regionId", converter = NullValueConverter.class)
    private String protocol_violator_postAddress_regionId;

    @CsvCustomBindByName(column = "protocol_violator_postAddress_districtId", converter = NullValueConverter.class)
    private String protocol_violator_postAddress_districtId;

    @CsvCustomBindByName(column = "protocol_violator_postAddress_address", converter = NullValueConverter.class)
    private String protocol_violator_postAddress_address;

    @CsvCustomBindByName(column = "protocol_violator_violatorDetail_occupationId", converter = NullValueConverter.class)
    private String protocol_violator_violatorDetail_occupationId;

    @CsvCustomBindByName(column = "protocol_violator_violatorDetail_employmentPlace", converter = NullValueConverter.class)
    private String protocol_violator_violatorDetail_employmentPlace;

    @CsvCustomBindByName(column = "protocol_violator_violatorDetail_employmentPosition", converter = NullValueConverter.class)
    private String protocol_violator_violatorDetail_employmentPosition;

    @CsvCustomBindByName(column = "protocol_violator_violatorDetail_additionally", converter = NullValueConverter.class)
    private String protocol_violator_violatorDetail_additionally;

    @CsvCustomBindByName(column = "protocol_violator_violatorDetail_signature", converter = NullValueConverter.class)
    private String protocol_violator_violatorDetail_signature;

    @CsvCustomBindByName(column = "protocol_violator_person_firstNameKir", converter = NullValueConverter.class)
    private String protocol_violator_person_firstNameKir;
    @CsvCustomBindByName(column = "protocol_violator_person_secondNameKir", converter = NullValueConverter.class)
    private String protocol_violator_person_secondNameKir;
    @CsvCustomBindByName(column = "protocol_violator_person_lastNameKir", converter = NullValueConverter.class)
    private String protocol_violator_person_lastNameKir;
    @CsvCustomBindByName(column = "protocol_violator_person_firstNameLat", converter = NullValueConverter.class)
    private String protocol_violator_person_firstNameLat;
    @CsvCustomBindByName(column = "protocol_violator_person_secondNameLat", converter = NullValueConverter.class)
    private String protocol_violator_person_secondNameLat;
    @CsvCustomBindByName(column = "protocol_violator_person_lastNameLat", converter = NullValueConverter.class)
    private String protocol_violator_person_lastNameLat;
    @CsvCustomBindByName(column = "protocol_violator_person_birthDate", converter = NullValueConverter.class)
    private String protocol_violator_person_birthDate;


    @CsvCustomBindByName(column = "protocol_violator_person_birthAddress_countryId", converter = NullValueConverter.class)
    private String protocol_violator_person_birthAddress_countryId;
    @CsvCustomBindByName(column = "protocol_violator_person_birthAddress_regionId", converter = NullValueConverter.class)
    private String protocol_violator_person_birthAddress_regionId;
    @CsvCustomBindByName(column = "protocol_violator_person_birthAddress_districtId", converter = NullValueConverter.class)
    private String protocol_violator_person_birthAddress_districtId;
    @CsvCustomBindByName(column = "protocol_violator_person_birthAddress_address", converter = NullValueConverter.class)
    private String protocol_violator_person_birthAddress_address;

    @CsvCustomBindByName(column = "protocol_violator_person_citizenshipTypeId", converter = NullValueConverter.class)
    private String protocol_violator_person_citizenshipTypeId;
    @CsvCustomBindByName(column = "protocol_violator_person_genderId", converter = NullValueConverter.class)
    private String protocol_violator_person_genderId;
    @CsvCustomBindByName(column = "protocol_violator_person_nationalityId", converter = NullValueConverter.class)
    private String protocol_violator_person_nationalityId;

    @CsvCustomBindByName(column = "protocol_violator_personDocument_documentSeries", converter = NullValueConverter.class)
    private String protocol_violator_personDocument_documentSeries;
    @CsvCustomBindByName(column = "protocol_violator_personDocument_documentNumber", converter = NullValueConverter.class)
    private String protocol_violator_personDocument_documentNumber;
    @CsvCustomBindByName(column = "protocol_violator_personDocument_documentTypeId", converter = NullValueConverter.class)
    private String protocol_violator_personDocument_documentTypeId;
    @CsvCustomBindByName(column = "protocol_violator_personDocument_documentGivenDate", converter = NullValueConverter.class)
    private String protocol_violator_personDocument_documentGivenDate;
    @CsvCustomBindByName(column = "protocol_violator_personDocument_documentExpireDate", converter = NullValueConverter.class)
    private String protocol_violator_personDocument_documentExpireDate;

    @CsvCustomBindByName(column = "protocol_violator_personDocument_givenAddress_countryId", converter = NullValueConverter.class)
    private String protocol_violator_personDocument_givenAddress_countryId;
    @CsvCustomBindByName(column = "protocol_violator_personDocument_givenAddress_regionId", converter = NullValueConverter.class)
    private String protocol_violator_personDocument_givenAddress_regionId;
    @CsvCustomBindByName(column = "protocol_violator_personDocument_givenAddress_districtId", converter = NullValueConverter.class)
    private String protocol_violator_personDocument_givenAddress_districtId;
    @CsvCustomBindByName(column = "protocol_violator_personDocument_givenAddress_address", converter = NullValueConverter.class)
    private String protocol_violator_personDocument_givenAddress_address;

    @CsvCustomBindByName(column = "resolution_externalId", converter = NullValueConverter.class)
    private String resolution_externalId;

    @CsvCustomBindByName(column = "resolution_considerUserInfo", converter = NullValueConverter.class)
    private String resolution_considerUserInfo;

    @CsvCustomBindByName(column = "resolution_inspectorPositionId", converter = NullValueConverter.class)
    private String resolution_inspectorPositionId;

    @CsvCustomBindByName(column = "resolution_inspectorRankId", converter = NullValueConverter.class)
    private String resolution_inspectorRankId;

    @CsvCustomBindByName(column = "resolution_inspectorWorkCertificate", converter = NullValueConverter.class)
    private String resolution_inspectorWorkCertificate;

    @CsvCustomBindByName(column = "resolution_resolutionTime", converter = NullValueConverter.class)
    private String resolution_resolutionTime;

    @CsvCustomBindByName(column = "resolution_isArticle33", converter = NullValueConverter.class)
    private String resolution_isArticle33;

    @CsvCustomBindByName(column = "resolution_isArticle34", converter = NullValueConverter.class)
    private String resolution_isArticle34;

    @CsvCustomBindByName(column = "resolution_departmentId", converter = NullValueConverter.class)
    private String resolution_departmentId;

    @CsvCustomBindByName(column = "resolution_regionId", converter = NullValueConverter.class)
    private String resolution_regionId;

    @CsvCustomBindByName(column = "resolution_districtId", converter = NullValueConverter.class)
    private String resolution_districtId;

    @CsvCustomBindByName(column = "resolution_signature", converter = NullValueConverter.class)
    private String resolution_signature;

    @CsvCustomBindByName(column = "resolution_decisionTypeId", converter = NullValueConverter.class)
    private String resolution_decisionTypeId;

    @CsvCustomBindByName(column = "resolution_articleId", converter = NullValueConverter.class)
    private String resolution_articleId;

    @CsvCustomBindByName(column = "resolution_articlePartId", converter = NullValueConverter.class)
    private String resolution_articlePartId;

    @CsvCustomBindByName(column = "resolution_articleViolationTypeId", converter = NullValueConverter.class)
    private String resolution_articleViolationTypeId;

    @CsvCustomBindByName(column = "resolution_executionFromDate", converter = NullValueConverter.class)
    private String resolution_executionFromDate;

    @CsvCustomBindByName(column = "resolution_mainPunishment_punishmentTypeId", converter = NullValueConverter.class)
    private String resolution_mainPunishment_punishmentTypeId;

    @CsvCustomBindByName(column = "resolution_mainPunishment_amount", converter = NullValueConverter.class)
    private String resolution_mainPunishment_amount;

    @CsvCustomBindByName(column = "inovice_isDiscount70", converter = NullValueConverter.class)
    private String inovice_isDiscount70;

    @CsvCustomBindByName(column = "inovice_isDiscount50", converter = NullValueConverter.class)
    private String inovice_isDiscount50;

    @CsvCustomBindByName(column = "inovice_externalId", converter = NullValueConverter.class)
    private String inovice_externalId;

    @CsvCustomBindByName(column = "inovice_invoiceId", converter = NullValueConverter.class)
    private String inovice_invoiceId;

    @CsvCustomBindByName(column = "inovice_invoiceSerial", converter = NullValueConverter.class)
    private String inovice_invoiceSerial;

    @CsvCustomBindByName(column = "inovice_invoiceNumber", converter = NullValueConverter.class)
    private String inovice_invoiceNumber;

    @CsvCustomBindByName(column = "inovice_invoiceDate", converter = NullValueConverter.class)
    private String inovice_invoiceDate;

    @CsvCustomBindByName(column = "inovice_discount70ForDate", converter = NullValueConverter.class)
    private String inovice_discount70ForDate;

    @CsvCustomBindByName(column = "inovice_discount50ForDate", converter = NullValueConverter.class)
    private String inovice_discount50ForDate;

    @CsvCustomBindByName(column = "inovice_penaltyPunishmentAmount", converter = NullValueConverter.class)
    private String inovice_penaltyPunishmentAmount;

    @CsvCustomBindByName(column = "inovice_discount70Amount", converter = NullValueConverter.class)
    private String inovice_discount70Amount;

    @CsvCustomBindByName(column = "inovice_discount50Amount", converter = NullValueConverter.class)
    private String inovice_discount50Amount;

    @CsvCustomBindByName(column = "inovice_organName", converter = NullValueConverter.class)
    private String inovice_organName;

    @CsvCustomBindByName(column = "inovice_bankInn", converter = NullValueConverter.class)
    private String inovice_bankInn;

    @CsvCustomBindByName(column = "inovice_bankName", converter = NullValueConverter.class)
    private String inovice_bankName;

    @CsvCustomBindByName(column = "inovice_bankCode", converter = NullValueConverter.class)
    private String inovice_bankCode;

    @CsvCustomBindByName(column = "inovice_bankAccount", converter = NullValueConverter.class)
    private String inovice_bankAccount;

    @CsvCustomBindByName(column = "inovice_payerName", converter = NullValueConverter.class)
    private String inovice_payerName;

    @CsvCustomBindByName(column = "inovice_payerAddress", converter = NullValueConverter.class)
    private String inovice_payerAddress;

    @CsvCustomBindByName(column = "inovice_payerBirthdate", converter = NullValueConverter.class)
    private String inovice_payerBirthdate;

    @CsvCustomBindByName(column = "payments_id", converter = NullValueConverter.class)
    private String payments_id;

    @CsvCustomBindByName(column = "payments_externalId", converter = NullValueConverter.class)
    private String payments_externalId;

    @CsvCustomBindByName(column = "payments_invoiceSerial", converter = NullValueConverter.class)
    private String payments_invoiceSerial;

    @CsvCustomBindByName(column = "payments_bid", converter = NullValueConverter.class)
    private String payments_bid;

    @CsvCustomBindByName(column = "payments_amount", converter = NullValueConverter.class)
    private String payments_amount;

    @CsvCustomBindByName(column = "payments_docNumber", converter = NullValueConverter.class)
    private String payments_docNumber;

    @CsvCustomBindByName(column = "payments_paidAt", converter = NullValueConverter.class)
    private String payments_paidAt;

    @CsvCustomBindByName(column = "payments_payerInfo_fromBankCode", converter = NullValueConverter.class)
    private String payments_payerInfo_fromBankCode;

    @CsvCustomBindByName(column = "payments_payerInfo_fromBankAccount", converter = NullValueConverter.class)
    private String payments_payerInfo_fromBankAccount;

    @CsvCustomBindByName(column = "payments_payerInfo_fromBankName", converter = NullValueConverter.class)
    private String payments_payerInfo_fromBankName;

    @CsvCustomBindByName(column = "payments_payerInfo_fromInn", converter = NullValueConverter.class)
    private String payments_payerInfo_fromInn;

    @CsvCustomBindByName(column = "payments_payeeInfo_toBankCode", converter = NullValueConverter.class)
    private String payments_payeeInfo_toBankCode;

    @CsvCustomBindByName(column = "payments_payeeInfo_toBankAccount", converter = NullValueConverter.class)
    private String payments_payeeInfo_toBankAccount;

    @CsvCustomBindByName(column = "payments_payeeInfo_toBankName", converter = NullValueConverter.class)
    private String payments_payeeInfo_toBankName;

    @CsvCustomBindByName(column = "payments_payeeInfo_toInn", converter = NullValueConverter.class)
    private String payments_payeeInfo_toInn;


    public String getProtocol_externalId() {
        return protocol_externalId;
    }

    public void setProtocol_externalId(String protocol_externalId) {
        this.protocol_externalId = protocol_externalId;
    }

    public String getProtocol_inspectorRegionId() {
        return protocol_inspectorRegionId;
    }

    public void setProtocol_inspectorRegionId(String protocol_inspectorRegionId) {
        this.protocol_inspectorRegionId = protocol_inspectorRegionId;
    }

    public String getProtocol_inspectorDistrictId() {
        return protocol_inspectorDistrictId;
    }

    public void setProtocol_inspectorDistrictId(String protocol_inspectorDistrictId) {
        this.protocol_inspectorDistrictId = protocol_inspectorDistrictId;
    }

    public String getProtocol_inspectorPositionId() {
        return protocol_inspectorPositionId;
    }

    public void setProtocol_inspectorPositionId(String protocol_inspectorPositionId) {
        this.protocol_inspectorPositionId = protocol_inspectorPositionId;
    }

    public String getProtocol_inspectorRankId() {
        return protocol_inspectorRankId;
    }

    public void setProtocol_inspectorRankId(String protocol_inspectorRankId) {
        this.protocol_inspectorRankId = protocol_inspectorRankId;
    }

    public String getProtocol_inspectorFio() {
        return protocol_inspectorFio;
    }

    public void setProtocol_inspectorFio(String protocol_inspectorFio) {
        this.protocol_inspectorFio = protocol_inspectorFio;
    }

    public String getProtocol_inspectorInfo() {
        return protocol_inspectorInfo;
    }

    public void setProtocol_inspectorInfo(String protocol_inspectorInfo) {
        this.protocol_inspectorInfo = protocol_inspectorInfo;
    }

    public String getProtocol_inspectorWorkCertificate() {
        return protocol_inspectorWorkCertificate;
    }

    public void setProtocol_inspectorWorkCertificate(String protocol_inspectorWorkCertificate) {
        this.protocol_inspectorWorkCertificate = protocol_inspectorWorkCertificate;
    }

    public String getProtocol_registrationTime() {
        return protocol_registrationTime;
    }

    public void setProtocol_registrationTime(String protocol_registrationTime) {
        this.protocol_registrationTime = protocol_registrationTime;
    }

    public String getProtocol_violationTime() {
        return protocol_violationTime;
    }

    public void setProtocol_violationTime(String protocol_violationTime) {
        this.protocol_violationTime = protocol_violationTime;
    }

    public String getProtocol_articleId() {
        return protocol_articleId;
    }

    public void setProtocol_articleId(String protocol_articleId) {
        this.protocol_articleId = protocol_articleId;
    }

    public String getProtocol_articlePartId() {
        return protocol_articlePartId;
    }

    public void setProtocol_articlePartId(String protocol_articlePartId) {
        this.protocol_articlePartId = protocol_articlePartId;
    }

    public String getProtocol_fabula() {
        return protocol_fabula;
    }

    public void setProtocol_fabula(String protocol_fabula) {
        this.protocol_fabula = protocol_fabula;
    }

    public String getProtocol_regionId() {
        return protocol_regionId;
    }

    public void setProtocol_regionId(String protocol_regionId) {
        this.protocol_regionId = protocol_regionId;
    }

    public String getProtocol_districtId() {
        return protocol_districtId;
    }

    public void setProtocol_districtId(String protocol_districtId) {
        this.protocol_districtId = protocol_districtId;
    }

    public String getProtocol_mtpId() {
        return protocol_mtpId;
    }

    public void setProtocol_mtpId(String protocol_mtpId) {
        this.protocol_mtpId = protocol_mtpId;
    }

    public String getMbprotocol_address() {
        return mbprotocol_address;
    }

    public void setMbprotocol_address(String mbprotocol_address) {
        this.mbprotocol_address = mbprotocol_address;
    }

    public String getProtocol_isFamiliarize() {
        return protocol_isFamiliarize;
    }

    public void setProtocol_isFamiliarize(String protocol_isFamiliarize) {
        this.protocol_isFamiliarize = protocol_isFamiliarize;
    }

    public String getProtocol_isAgree() {
        return protocol_isAgree;
    }

    public void setProtocol_isAgree(String protocol_isAgree) {
        this.protocol_isAgree = protocol_isAgree;
    }

    public String getProtocol_violator_pinpp() {
        return protocol_violator_pinpp;
    }

    public void setProtocol_violator_pinpp(String protocol_violator_pinpp) {
        this.protocol_violator_pinpp = protocol_violator_pinpp;
    }

    public String getProtocol_violator_mobile() {
        return protocol_violator_mobile;
    }

    public void setProtocol_violator_mobile(String protocol_violator_mobile) {
        this.protocol_violator_mobile = protocol_violator_mobile;
    }

    public String getProtocol_violator_landline() {
        return protocol_violator_landline;
    }

    public void setProtocol_violator_landline(String protocol_violator_landline) {
        this.protocol_violator_landline = protocol_violator_landline;
    }

    public String getProtocol_violator_actualAddress_countryId() {
        return protocol_violator_actualAddress_countryId;
    }

    public void setProtocol_violator_actualAddress_countryId(String protocol_violator_actualAddress_countryId) {
        this.protocol_violator_actualAddress_countryId = protocol_violator_actualAddress_countryId;
    }

    public String getProtocol_violator_actualAddress_regionId() {
        return protocol_violator_actualAddress_regionId;
    }

    public void setProtocol_violator_actualAddress_regionId(String protocol_violator_actualAddress_regionId) {
        this.protocol_violator_actualAddress_regionId = protocol_violator_actualAddress_regionId;
    }

    public String getProtocol_violator_actualAddress_districtId() {
        return protocol_violator_actualAddress_districtId;
    }

    public void setProtocol_violator_actualAddress_districtId(String protocol_violator_actualAddress_districtId) {
        this.protocol_violator_actualAddress_districtId = protocol_violator_actualAddress_districtId;
    }

    public String getProtocol_violator_actualAddress_address() {
        return protocol_violator_actualAddress_address;
    }

    public void setProtocol_violator_actualAddress_address(String protocol_violator_actualAddress_address) {
        this.protocol_violator_actualAddress_address = protocol_violator_actualAddress_address;
    }

    public String getProtocol_violator_postAddress_countryId() {
        return protocol_violator_postAddress_countryId;
    }

    public void setProtocol_violator_postAddress_countryId(String protocol_violator_postAddress_countryId) {
        this.protocol_violator_postAddress_countryId = protocol_violator_postAddress_countryId;
    }

    public String getProtocol_violator_postAddress_regionId() {
        return protocol_violator_postAddress_regionId;
    }

    public void setProtocol_violator_postAddress_regionId(String protocol_violator_postAddress_regionId) {
        this.protocol_violator_postAddress_regionId = protocol_violator_postAddress_regionId;
    }

    public String getProtocol_violator_postAddress_districtId() {
        return protocol_violator_postAddress_districtId;
    }

    public void setProtocol_violator_postAddress_districtId(String protocol_violator_postAddress_districtId) {
        this.protocol_violator_postAddress_districtId = protocol_violator_postAddress_districtId;
    }

    public String getProtocol_violator_postAddress_address() {
        return protocol_violator_postAddress_address;
    }

    public void setProtocol_violator_postAddress_address(String protocol_violator_postAddress_address) {
        this.protocol_violator_postAddress_address = protocol_violator_postAddress_address;
    }

    public String getProtocol_violator_violatorDetail_occupationId() {
        return protocol_violator_violatorDetail_occupationId;
    }

    public void setProtocol_violator_violatorDetail_occupationId(String protocol_violator_violatorDetail_occupationId) {
        this.protocol_violator_violatorDetail_occupationId = protocol_violator_violatorDetail_occupationId;
    }

    public String getProtocol_violator_violatorDetail_employmentPlace() {
        return protocol_violator_violatorDetail_employmentPlace;
    }

    public void setProtocol_violator_violatorDetail_employmentPlace(String protocol_violator_violatorDetail_employmentPlace) {
        this.protocol_violator_violatorDetail_employmentPlace = protocol_violator_violatorDetail_employmentPlace;
    }

    public String getProtocol_violator_violatorDetail_employmentPosition() {
        return protocol_violator_violatorDetail_employmentPosition;
    }

    public void setProtocol_violator_violatorDetail_employmentPosition(String protocol_violator_violatorDetail_employmentPosition) {
        this.protocol_violator_violatorDetail_employmentPosition = protocol_violator_violatorDetail_employmentPosition;
    }

    public String getProtocol_violator_violatorDetail_additionally() {
        return protocol_violator_violatorDetail_additionally;
    }

    public void setProtocol_violator_violatorDetail_additionally(String protocol_violator_violatorDetail_additionally) {
        this.protocol_violator_violatorDetail_additionally = protocol_violator_violatorDetail_additionally;
    }

    public String getProtocol_violator_violatorDetail_signature() {
        return protocol_violator_violatorDetail_signature;
    }

    public void setProtocol_violator_violatorDetail_signature(String protocol_violator_violatorDetail_signature) {
        this.protocol_violator_violatorDetail_signature = protocol_violator_violatorDetail_signature;
    }

    public String getProtocol_violator_person_firstNameKir() {
        return protocol_violator_person_firstNameKir;
    }

    public void setProtocol_violator_person_firstNameKir(String protocol_violator_person_firstNameKir) {
        this.protocol_violator_person_firstNameKir = protocol_violator_person_firstNameKir;
    }

    public String getProtocol_violator_person_secondNameKir() {
        return protocol_violator_person_secondNameKir;
    }

    public void setProtocol_violator_person_secondNameKir(String protocol_violator_person_secondNameKir) {
        this.protocol_violator_person_secondNameKir = protocol_violator_person_secondNameKir;
    }

    public String getProtocol_violator_person_lastNameKir() {
        return protocol_violator_person_lastNameKir;
    }

    public void setProtocol_violator_person_lastNameKir(String protocol_violator_person_lastNameKir) {
        this.protocol_violator_person_lastNameKir = protocol_violator_person_lastNameKir;
    }

    public String getProtocol_violator_person_firstNameLat() {
        return protocol_violator_person_firstNameLat;
    }

    public void setProtocol_violator_person_firstNameLat(String protocol_violator_person_firstNameLat) {
        this.protocol_violator_person_firstNameLat = protocol_violator_person_firstNameLat;
    }

    public String getProtocol_violator_person_secondNameLat() {
        return protocol_violator_person_secondNameLat;
    }

    public void setProtocol_violator_person_secondNameLat(String protocol_violator_person_secondNameLat) {
        this.protocol_violator_person_secondNameLat = protocol_violator_person_secondNameLat;
    }

    public String getProtocol_violator_person_lastNameLat() {
        return protocol_violator_person_lastNameLat;
    }

    public void setProtocol_violator_person_lastNameLat(String protocol_violator_person_lastNameLat) {
        this.protocol_violator_person_lastNameLat = protocol_violator_person_lastNameLat;
    }

    public String getProtocol_violator_person_birthDate() {
        return protocol_violator_person_birthDate;
    }

    public void setProtocol_violator_person_birthDate(String protocol_violator_person_birthDate) {
        this.protocol_violator_person_birthDate = protocol_violator_person_birthDate;
    }

    public String getProtocol_violator_person_birthAddress_countryId() {
        return protocol_violator_person_birthAddress_countryId;
    }

    public void setProtocol_violator_person_birthAddress_countryId(String protocol_violator_person_birthAddress_countryId) {
        this.protocol_violator_person_birthAddress_countryId = protocol_violator_person_birthAddress_countryId;
    }

    public String getProtocol_violator_person_birthAddress_regionId() {
        return protocol_violator_person_birthAddress_regionId;
    }

    public void setProtocol_violator_person_birthAddress_regionId(String protocol_violator_person_birthAddress_regionId) {
        this.protocol_violator_person_birthAddress_regionId = protocol_violator_person_birthAddress_regionId;
    }

    public String getProtocol_violator_person_birthAddress_districtId() {
        return protocol_violator_person_birthAddress_districtId;
    }

    public void setProtocol_violator_person_birthAddress_districtId(String protocol_violator_person_birthAddress_districtId) {
        this.protocol_violator_person_birthAddress_districtId = protocol_violator_person_birthAddress_districtId;
    }

    public String getProtocol_violator_person_birthAddress_address() {
        return protocol_violator_person_birthAddress_address;
    }

    public void setProtocol_violator_person_birthAddress_address(String protocol_violator_person_birthAddress_address) {
        this.protocol_violator_person_birthAddress_address = protocol_violator_person_birthAddress_address;
    }

    public String getProtocol_violator_person_citizenshipTypeId() {
        return protocol_violator_person_citizenshipTypeId;
    }

    public void setProtocol_violator_person_citizenshipTypeId(String protocol_violator_person_citizenshipTypeId) {
        this.protocol_violator_person_citizenshipTypeId = protocol_violator_person_citizenshipTypeId;
    }

    public String getProtocol_violator_person_genderId() {
        return protocol_violator_person_genderId;
    }

    public void setProtocol_violator_person_genderId(String protocol_violator_person_genderId) {
        this.protocol_violator_person_genderId = protocol_violator_person_genderId;
    }

    public String getProtocol_violator_person_nationalityId() {
        return protocol_violator_person_nationalityId;
    }

    public void setProtocol_violator_person_nationalityId(String protocol_violator_person_nationalityId) {
        this.protocol_violator_person_nationalityId = protocol_violator_person_nationalityId;
    }

    public String getProtocol_violator_personDocument_documentSeries() {
        return protocol_violator_personDocument_documentSeries;
    }

    public void setProtocol_violator_personDocument_documentSeries(String protocol_violator_personDocument_documentSeries) {
        this.protocol_violator_personDocument_documentSeries = protocol_violator_personDocument_documentSeries;
    }

    public String getProtocol_violator_personDocument_documentNumber() {
        return protocol_violator_personDocument_documentNumber;
    }

    public void setProtocol_violator_personDocument_documentNumber(String protocol_violator_personDocument_documentNumber) {
        this.protocol_violator_personDocument_documentNumber = protocol_violator_personDocument_documentNumber;
    }

    public String getProtocol_violator_personDocument_documentTypeId() {
        return protocol_violator_personDocument_documentTypeId;
    }

    public void setProtocol_violator_personDocument_documentTypeId(String protocol_violator_personDocument_documentTypeId) {
        this.protocol_violator_personDocument_documentTypeId = protocol_violator_personDocument_documentTypeId;
    }

    public String getProtocol_violator_personDocument_documentGivenDate() {
        return protocol_violator_personDocument_documentGivenDate;
    }

    public void setProtocol_violator_personDocument_documentGivenDate(String protocol_violator_personDocument_documentGivenDate) {
        this.protocol_violator_personDocument_documentGivenDate = protocol_violator_personDocument_documentGivenDate;
    }

    public String getProtocol_violator_personDocument_documentExpireDate() {
        return protocol_violator_personDocument_documentExpireDate;
    }

    public void setProtocol_violator_personDocument_documentExpireDate(String protocol_violator_personDocument_documentExpireDate) {
        this.protocol_violator_personDocument_documentExpireDate = protocol_violator_personDocument_documentExpireDate;
    }

    public String getProtocol_violator_personDocument_givenAddress_countryId() {
        return protocol_violator_personDocument_givenAddress_countryId;
    }

    public void setProtocol_violator_personDocument_givenAddress_countryId(String protocol_violator_personDocument_givenAddress_countryId) {
        this.protocol_violator_personDocument_givenAddress_countryId = protocol_violator_personDocument_givenAddress_countryId;
    }

    public String getProtocol_violator_personDocument_givenAddress_regionId() {
        return protocol_violator_personDocument_givenAddress_regionId;
    }

    public void setProtocol_violator_personDocument_givenAddress_regionId(String protocol_violator_personDocument_givenAddress_regionId) {
        this.protocol_violator_personDocument_givenAddress_regionId = protocol_violator_personDocument_givenAddress_regionId;
    }

    public String getProtocol_violator_personDocument_givenAddress_districtId() {
        return protocol_violator_personDocument_givenAddress_districtId;
    }

    public void setProtocol_violator_personDocument_givenAddress_districtId(String protocol_violator_personDocument_givenAddress_districtId) {
        this.protocol_violator_personDocument_givenAddress_districtId = protocol_violator_personDocument_givenAddress_districtId;
    }

    public String getProtocol_violator_personDocument_givenAddress_address() {
        return protocol_violator_personDocument_givenAddress_address;
    }

    public void setProtocol_violator_personDocument_givenAddress_address(String protocol_violator_personDocument_givenAddress_address) {
        this.protocol_violator_personDocument_givenAddress_address = protocol_violator_personDocument_givenAddress_address;
    }

    public String getResolution_externalId() {
        return resolution_externalId;
    }

    public void setResolution_externalId(String resolution_externalId) {
        this.resolution_externalId = resolution_externalId;
    }

    public String getResolution_considerUserInfo() {
        return resolution_considerUserInfo;
    }

    public void setResolution_considerUserInfo(String resolution_considerUserInfo) {
        this.resolution_considerUserInfo = resolution_considerUserInfo;
    }

    public String getResolution_inspectorPositionId() {
        return resolution_inspectorPositionId;
    }

    public void setResolution_inspectorPositionId(String resolution_inspectorPositionId) {
        this.resolution_inspectorPositionId = resolution_inspectorPositionId;
    }

    public String getResolution_inspectorRankId() {
        return resolution_inspectorRankId;
    }

    public void setResolution_inspectorRankId(String resolution_inspectorRankId) {
        this.resolution_inspectorRankId = resolution_inspectorRankId;
    }

    public String getResolution_inspectorWorkCertificate() {
        return resolution_inspectorWorkCertificate;
    }

    public void setResolution_inspectorWorkCertificate(String resolution_inspectorWorkCertificate) {
        this.resolution_inspectorWorkCertificate = resolution_inspectorWorkCertificate;
    }

    public String getResolution_resolutionTime() {
        return resolution_resolutionTime;
    }

    public void setResolution_resolutionTime(String resolution_resolutionTime) {
        this.resolution_resolutionTime = resolution_resolutionTime;
    }

    public String getResolution_isArticle33() {
        return resolution_isArticle33;
    }

    public void setResolution_isArticle33(String resolution_isArticle33) {
        this.resolution_isArticle33 = resolution_isArticle33;
    }

    public String getResolution_isArticle34() {
        return resolution_isArticle34;
    }

    public void setResolution_isArticle34(String resolution_isArticle34) {
        this.resolution_isArticle34 = resolution_isArticle34;
    }

    public String getResolution_departmentId() {
        return resolution_departmentId;
    }

    public void setResolution_departmentId(String resolution_departmentId) {
        this.resolution_departmentId = resolution_departmentId;
    }

    public String getResolution_regionId() {
        return resolution_regionId;
    }

    public void setResolution_regionId(String resolution_regionId) {
        this.resolution_regionId = resolution_regionId;
    }

    public String getResolution_districtId() {
        return resolution_districtId;
    }

    public void setResolution_districtId(String resolution_districtId) {
        this.resolution_districtId = resolution_districtId;
    }

    public String getResolution_signature() {
        return resolution_signature;
    }

    public void setResolution_signature(String resolution_signature) {
        this.resolution_signature = resolution_signature;
    }

    public String getResolution_decisionTypeId() {
        return resolution_decisionTypeId;
    }

    public void setResolution_decisionTypeId(String resolution_decisionTypeId) {
        this.resolution_decisionTypeId = resolution_decisionTypeId;
    }

    public String getResolution_articleId() {
        return resolution_articleId;
    }

    public void setResolution_articleId(String resolution_articleId) {
        this.resolution_articleId = resolution_articleId;
    }

    public String getResolution_articlePartId() {
        return resolution_articlePartId;
    }

    public void setResolution_articlePartId(String resolution_articlePartId) {
        this.resolution_articlePartId = resolution_articlePartId;
    }

    public String getResolution_articleViolationTypeId() {
        return resolution_articleViolationTypeId;
    }

    public void setResolution_articleViolationTypeId(String resolution_articleViolationTypeId) {
        this.resolution_articleViolationTypeId = resolution_articleViolationTypeId;
    }

    public String getResolution_executionFromDate() {
        return resolution_executionFromDate;
    }

    public void setResolution_executionFromDate(String resolution_executionFromDate) {
        this.resolution_executionFromDate = resolution_executionFromDate;
    }

    public String getResolution_mainPunishment_punishmentTypeId() {
        return resolution_mainPunishment_punishmentTypeId;
    }

    public void setResolution_mainPunishment_punishmentTypeId(String resolution_mainPunishment_punishmentTypeId) {
        this.resolution_mainPunishment_punishmentTypeId = resolution_mainPunishment_punishmentTypeId;
    }

    public String getResolution_mainPunishment_amount() {
        return resolution_mainPunishment_amount;
    }

    public void setResolution_mainPunishment_amount(String resolution_mainPunishment_amount) {
        this.resolution_mainPunishment_amount = resolution_mainPunishment_amount;
    }

    public String getInovice_isDiscount70() {
        return inovice_isDiscount70;
    }

    public void setInovice_isDiscount70(String inovice_isDiscount70) {
        this.inovice_isDiscount70 = inovice_isDiscount70;
    }

    public String getInovice_isDiscount50() {
        return inovice_isDiscount50;
    }

    public void setInovice_isDiscount50(String inovice_isDiscount50) {
        this.inovice_isDiscount50 = inovice_isDiscount50;
    }

    public String getInovice_externalId() {
        return inovice_externalId;
    }

    public void setInovice_externalId(String inovice_externalId) {
        this.inovice_externalId = inovice_externalId;
    }

    public String getInovice_invoiceId() {
        return inovice_invoiceId;
    }

    public void setInovice_invoiceId(String inovice_invoiceId) {
        this.inovice_invoiceId = inovice_invoiceId;
    }

    public String getInovice_invoiceSerial() {
        return inovice_invoiceSerial;
    }

    public void setInovice_invoiceSerial(String inovice_invoiceSerial) {
        this.inovice_invoiceSerial = inovice_invoiceSerial;
    }

    public String getInovice_invoiceNumber() {
        return inovice_invoiceNumber;
    }

    public void setInovice_invoiceNumber(String inovice_invoiceNumber) {
        this.inovice_invoiceNumber = inovice_invoiceNumber;
    }

    public String getInovice_invoiceDate() {
        return inovice_invoiceDate;
    }

    public void setInovice_invoiceDate(String inovice_invoiceDate) {
        this.inovice_invoiceDate = inovice_invoiceDate;
    }

    public String getInovice_discount70ForDate() {
        return inovice_discount70ForDate;
    }

    public void setInovice_discount70ForDate(String inovice_discount70ForDate) {
        this.inovice_discount70ForDate = inovice_discount70ForDate;
    }

    public String getInovice_discount50ForDate() {
        return inovice_discount50ForDate;
    }

    public void setInovice_discount50ForDate(String inovice_discount50ForDate) {
        this.inovice_discount50ForDate = inovice_discount50ForDate;
    }

    public String getInovice_penaltyPunishmentAmount() {
        return inovice_penaltyPunishmentAmount;
    }

    public void setInovice_penaltyPunishmentAmount(String inovice_penaltyPunishmentAmount) {
        this.inovice_penaltyPunishmentAmount = inovice_penaltyPunishmentAmount;
    }

    public String getInovice_discount70Amount() {
        return inovice_discount70Amount;
    }

    public void setInovice_discount70Amount(String inovice_discount70Amount) {
        this.inovice_discount70Amount = inovice_discount70Amount;
    }

    public String getInovice_discount50Amount() {
        return inovice_discount50Amount;
    }

    public void setInovice_discount50Amount(String inovice_discount50Amount) {
        this.inovice_discount50Amount = inovice_discount50Amount;
    }

    public String getInovice_organName() {
        return inovice_organName;
    }

    public void setInovice_organName(String inovice_organName) {
        this.inovice_organName = inovice_organName;
    }

    public String getInovice_bankInn() {
        return inovice_bankInn;
    }

    public void setInovice_bankInn(String inovice_bankInn) {
        this.inovice_bankInn = inovice_bankInn;
    }

    public String getInovice_bankName() {
        return inovice_bankName;
    }

    public void setInovice_bankName(String inovice_bankName) {
        this.inovice_bankName = inovice_bankName;
    }

    public String getInovice_bankCode() {
        return inovice_bankCode;
    }

    public void setInovice_bankCode(String inovice_bankCode) {
        this.inovice_bankCode = inovice_bankCode;
    }

    public String getInovice_bankAccount() {
        return inovice_bankAccount;
    }

    public void setInovice_bankAccount(String inovice_bankAccount) {
        this.inovice_bankAccount = inovice_bankAccount;
    }

    public String getInovice_payerName() {
        return inovice_payerName;
    }

    public void setInovice_payerName(String inovice_payerName) {
        this.inovice_payerName = inovice_payerName;
    }

    public String getInovice_payerAddress() {
        return inovice_payerAddress;
    }

    public void setInovice_payerAddress(String inovice_payerAddress) {
        this.inovice_payerAddress = inovice_payerAddress;
    }

    public String getInovice_payerBirthdate() {
        return inovice_payerBirthdate;
    }

    public void setInovice_payerBirthdate(String inovice_payerBirthdate) {
        this.inovice_payerBirthdate = inovice_payerBirthdate;
    }

    public String getPayments_id() {
        return payments_id;
    }

    public void setPayments_id(String payments_id) {
        this.payments_id = payments_id;
    }

    public String getPayments_externalId() {
        return payments_externalId;
    }

    public void setPayments_externalId(String payments_externalId) {
        this.payments_externalId = payments_externalId;
    }

    public String getPayments_invoiceSerial() {
        return payments_invoiceSerial;
    }

    public void setPayments_invoiceSerial(String payments_invoiceSerial) {
        this.payments_invoiceSerial = payments_invoiceSerial;
    }

    public String getPayments_bid() {
        return payments_bid;
    }

    public void setPayments_bid(String payments_bid) {
        this.payments_bid = payments_bid;
    }

    public String getPayments_amount() {
        return payments_amount;
    }

    public void setPayments_amount(String payments_amount) {
        this.payments_amount = payments_amount;
    }

    public String getPayments_docNumber() {
        return payments_docNumber;
    }

    public void setPayments_docNumber(String payments_docNumber) {
        this.payments_docNumber = payments_docNumber;
    }

    public String getPayments_paidAt() {
        return payments_paidAt;
    }

    public void setPayments_paidAt(String payments_paidAt) {
        this.payments_paidAt = payments_paidAt;
    }

    public String getPayments_payerInfo_fromBankCode() {
        return payments_payerInfo_fromBankCode;
    }

    public void setPayments_payerInfo_fromBankCode(String payments_payerInfo_fromBankCode) {
        this.payments_payerInfo_fromBankCode = payments_payerInfo_fromBankCode;
    }

    public String getPayments_payerInfo_fromBankAccount() {
        return payments_payerInfo_fromBankAccount;
    }

    public void setPayments_payerInfo_fromBankAccount(String payments_payerInfo_fromBankAccount) {
        this.payments_payerInfo_fromBankAccount = payments_payerInfo_fromBankAccount;
    }

    public String getPayments_payerInfo_fromBankName() {
        return payments_payerInfo_fromBankName;
    }

    public void setPayments_payerInfo_fromBankName(String payments_payerInfo_fromBankName) {
        this.payments_payerInfo_fromBankName = payments_payerInfo_fromBankName;
    }

    public String getPayments_payerInfo_fromInn() {
        return payments_payerInfo_fromInn;
    }

    public void setPayments_payerInfo_fromInn(String payments_payerInfo_fromInn) {
        this.payments_payerInfo_fromInn = payments_payerInfo_fromInn;
    }

    public String getPayments_payeeInfo_toBankCode() {
        return payments_payeeInfo_toBankCode;
    }

    public void setPayments_payeeInfo_toBankCode(String payments_payeeInfo_toBankCode) {
        this.payments_payeeInfo_toBankCode = payments_payeeInfo_toBankCode;
    }

    public String getPayments_payeeInfo_toBankAccount() {
        return payments_payeeInfo_toBankAccount;
    }

    public void setPayments_payeeInfo_toBankAccount(String payments_payeeInfo_toBankAccount) {
        this.payments_payeeInfo_toBankAccount = payments_payeeInfo_toBankAccount;
    }

    public String getPayments_payeeInfo_toBankName() {
        return payments_payeeInfo_toBankName;
    }

    public void setPayments_payeeInfo_toBankName(String payments_payeeInfo_toBankName) {
        this.payments_payeeInfo_toBankName = payments_payeeInfo_toBankName;
    }

    public String getPayments_payeeInfo_toInn() {
        return payments_payeeInfo_toInn;
    }

    public void setPayments_payeeInfo_toInn(String payments_payeeInfo_toInn) {
        this.payments_payeeInfo_toInn = payments_payeeInfo_toInn;
    }

    @Override
    public String toString() {
        return "ProtocolData{" +
                "protocol_externalId='" + protocol_externalId + '\'' +
                ", protocol_inspectorRegionId='" + protocol_inspectorRegionId + '\'' +
                ", protocol_inspectorDistrictId='" + protocol_inspectorDistrictId + '\'' +
                ", protocol_inspectorPositionId='" + protocol_inspectorPositionId + '\'' +
                ", protocol_inspectorRankId='" + protocol_inspectorRankId + '\'' +
                ", protocol_inspectorFio='" + protocol_inspectorFio + '\'' +
                ", protocol_inspectorInfo='" + protocol_inspectorInfo + '\'' +
                ", protocol_inspectorWorkCertificate='" + protocol_inspectorWorkCertificate + '\'' +
                ", protocol_registrationTime='" + protocol_registrationTime + '\'' +
                ", protocol_violationTime='" + protocol_violationTime + '\'' +
                ", protocol_articleId='" + protocol_articleId + '\'' +
                ", protocol_articlePartId='" + protocol_articlePartId + '\'' +
                ", protocol_fabula='" + protocol_fabula + '\'' +
                ", protocol_regionId='" + protocol_regionId + '\'' +
                ", protocol_districtId='" + protocol_districtId + '\'' +
                ", protocol_mtpId='" + protocol_mtpId + '\'' +
                ", mbprotocol_address='" + mbprotocol_address + '\'' +
                ", protocol_isFamiliarize='" + protocol_isFamiliarize + '\'' +
                ", protocol_isAgree='" + protocol_isAgree + '\'' +
                ", protocol_violator_pinpp='" + protocol_violator_pinpp + '\'' +
                ", protocol_violator_mobile='" + protocol_violator_mobile + '\'' +
                ", protocol_violator_landline='" + protocol_violator_landline + '\'' +
                ", protocol_violator_actualAddress_countryId='" + protocol_violator_actualAddress_countryId + '\'' +
                ", protocol_violator_actualAddress_regionId='" + protocol_violator_actualAddress_regionId + '\'' +
                ", protocol_violator_actualAddress_districtId='" + protocol_violator_actualAddress_districtId + '\'' +
                ", protocol_violator_actualAddress_address='" + protocol_violator_actualAddress_address + '\'' +
                ", protocol_violator_postAddress_countryId='" + protocol_violator_postAddress_countryId + '\'' +
                ", protocol_violator_postAddress_regionId='" + protocol_violator_postAddress_regionId + '\'' +
                ", protocol_violator_postAddress_districtId='" + protocol_violator_postAddress_districtId + '\'' +
                ", protocol_violator_postAddress_address='" + protocol_violator_postAddress_address + '\'' +
                ", protocol_violator_violatorDetail_occupationId='" + protocol_violator_violatorDetail_occupationId + '\'' +
                ", protocol_violator_violatorDetail_employmentPlace='" + protocol_violator_violatorDetail_employmentPlace + '\'' +
                ", protocol_violator_violatorDetail_employmentPosition='" + protocol_violator_violatorDetail_employmentPosition + '\'' +
                ", protocol_violator_violatorDetail_additionally='" + protocol_violator_violatorDetail_additionally + '\'' +
                ", protocol_violator_violatorDetail_signature='" + protocol_violator_violatorDetail_signature + '\'' +
                ", protocol_violator_person_firstNameKir='" + protocol_violator_person_firstNameKir + '\'' +
                ", protocol_violator_person_secondNameKir='" + protocol_violator_person_secondNameKir + '\'' +
                ", protocol_violator_person_lastNameKir='" + protocol_violator_person_lastNameKir + '\'' +
                ", protocol_violator_person_firstNameLat='" + protocol_violator_person_firstNameLat + '\'' +
                ", protocol_violator_person_secondNameLat='" + protocol_violator_person_secondNameLat + '\'' +
                ", protocol_violator_person_lastNameLat='" + protocol_violator_person_lastNameLat + '\'' +
                ", protocol_violator_person_birthDate='" + protocol_violator_person_birthDate + '\'' +
                ", protocol_violator_person_birthAddress_countryId='" + protocol_violator_person_birthAddress_countryId + '\'' +
                ", protocol_violator_person_birthAddress_regionId='" + protocol_violator_person_birthAddress_regionId + '\'' +
                ", protocol_violator_person_birthAddress_districtId='" + protocol_violator_person_birthAddress_districtId + '\'' +
                ", protocol_violator_person_birthAddress_address='" + protocol_violator_person_birthAddress_address + '\'' +
                ", protocol_violator_person_citizenshipTypeId='" + protocol_violator_person_citizenshipTypeId + '\'' +
                ", protocol_violator_person_genderId='" + protocol_violator_person_genderId + '\'' +
                ", protocol_violator_person_nationalityId='" + protocol_violator_person_nationalityId + '\'' +
                ", protocol_violator_personDocument_documentSeries='" + protocol_violator_personDocument_documentSeries + '\'' +
                ", protocol_violator_personDocument_documentNumber='" + protocol_violator_personDocument_documentNumber + '\'' +
                ", protocol_violator_personDocument_documentTypeId='" + protocol_violator_personDocument_documentTypeId + '\'' +
                ", protocol_violator_personDocument_documentGivenDate='" + protocol_violator_personDocument_documentGivenDate + '\'' +
                ", protocol_violator_personDocument_documentExpireDate='" + protocol_violator_personDocument_documentExpireDate + '\'' +
                ", protocol_violator_personDocument_givenAddress_countryId='" + protocol_violator_personDocument_givenAddress_countryId + '\'' +
                ", protocol_violator_personDocument_givenAddress_regionId='" + protocol_violator_personDocument_givenAddress_regionId + '\'' +
                ", protocol_violator_personDocument_givenAddress_districtId='" + protocol_violator_personDocument_givenAddress_districtId + '\'' +
                ", protocol_violator_personDocument_givenAddress_address='" + protocol_violator_personDocument_givenAddress_address + '\'' +
                ", resolution_externalId='" + resolution_externalId + '\'' +
                ", resolution_considerUserInfo='" + resolution_considerUserInfo + '\'' +
                ", resolution_inspectorPositionId='" + resolution_inspectorPositionId + '\'' +
                ", resolution_inspectorRankId='" + resolution_inspectorRankId + '\'' +
                ", resolution_inspectorWorkCertificate='" + resolution_inspectorWorkCertificate + '\'' +
                ", resolution_resolutionTime='" + resolution_resolutionTime + '\'' +
                ", resolution_isArticle33='" + resolution_isArticle33 + '\'' +
                ", resolution_isArticle34='" + resolution_isArticle34 + '\'' +
                ", resolution_departmentId='" + resolution_departmentId + '\'' +
                ", resolution_regionId='" + resolution_regionId + '\'' +
                ", resolution_districtId='" + resolution_districtId + '\'' +
                ", resolution_signature='" + resolution_signature + '\'' +
                ", resolution_decisionTypeId='" + resolution_decisionTypeId + '\'' +
                ", resolution_articleId='" + resolution_articleId + '\'' +
                ", resolution_articlePartId='" + resolution_articlePartId + '\'' +
                ", resolution_articleViolationTypeId='" + resolution_articleViolationTypeId + '\'' +
                ", resolution_executionFromDate='" + resolution_executionFromDate + '\'' +
                ", resolution_mainPunishment_punishmentTypeId='" + resolution_mainPunishment_punishmentTypeId + '\'' +
                ", resolution_mainPunishment_amount='" + resolution_mainPunishment_amount + '\'' +
                ", inovice_isDiscount70='" + inovice_isDiscount70 + '\'' +
                ", inovice_isDiscount50='" + inovice_isDiscount50 + '\'' +
                ", inovice_externalId='" + inovice_externalId + '\'' +
                ", inovice_invoiceId='" + inovice_invoiceId + '\'' +
                ", inovice_invoiceSerial='" + inovice_invoiceSerial + '\'' +
                ", inovice_invoiceNumber='" + inovice_invoiceNumber + '\'' +
                ", inovice_invoiceDate='" + inovice_invoiceDate + '\'' +
                ", inovice_discount70ForDate='" + inovice_discount70ForDate + '\'' +
                ", inovice_discount50ForDate='" + inovice_discount50ForDate + '\'' +
                ", inovice_penaltyPunishmentAmount='" + inovice_penaltyPunishmentAmount + '\'' +
                ", inovice_discount70Amount='" + inovice_discount70Amount + '\'' +
                ", inovice_discount50Amount='" + inovice_discount50Amount + '\'' +
                ", inovice_organName='" + inovice_organName + '\'' +
                ", inovice_bankInn='" + inovice_bankInn + '\'' +
                ", inovice_bankName='" + inovice_bankName + '\'' +
                ", inovice_bankCode='" + inovice_bankCode + '\'' +
                ", inovice_bankAccount='" + inovice_bankAccount + '\'' +
                ", inovice_payerName='" + inovice_payerName + '\'' +
                ", inovice_payerAddress='" + inovice_payerAddress + '\'' +
                ", inovice_payerBirthdate='" + inovice_payerBirthdate + '\'' +
                ", payments_id='" + payments_id + '\'' +
                ", payments_externalId='" + payments_externalId + '\'' +
                ", payments_invoiceSerial='" + payments_invoiceSerial + '\'' +
                ", payments_bid='" + payments_bid + '\'' +
                ", payments_amount='" + payments_amount + '\'' +
                ", payments_docNumber='" + payments_docNumber + '\'' +
                ", payments_paidAt='" + payments_paidAt + '\'' +
                ", payments_payerInfo_fromBankCode='" + payments_payerInfo_fromBankCode + '\'' +
                ", payments_payerInfo_fromBankAccount='" + payments_payerInfo_fromBankAccount + '\'' +
                ", payments_payerInfo_fromBankName='" + payments_payerInfo_fromBankName + '\'' +
                ", payments_payerInfo_fromInn='" + payments_payerInfo_fromInn + '\'' +
                ", payments_payeeInfo_toBankCode='" + payments_payeeInfo_toBankCode + '\'' +
                ", payments_payeeInfo_toBankAccount='" + payments_payeeInfo_toBankAccount + '\'' +
                ", payments_payeeInfo_toBankName='" + payments_payeeInfo_toBankName + '\'' +
                ", payments_payeeInfo_toInn='" + payments_payeeInfo_toInn + '\'' +
                '}';
    }
}
