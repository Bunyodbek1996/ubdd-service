package uz.ciasev.ubdd_service.migration;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ciasev.ubdd_service.dto.internal.request.AddressRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.PersonDocumentRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.PersonRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.protocol.ProtocolRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.resolution.organ.OrganPunishmentRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.resolution.organ.SingleResolutionRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.violator.ViolatorCreateRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.violator.ViolatorDetailRequestDTO;
import uz.ciasev.ubdd_service.dto.ubdd.UbddInvoiceRequest;
import uz.ciasev.ubdd_service.entity.dict.*;
import uz.ciasev.ubdd_service.entity.dict.article.Article;
import uz.ciasev.ubdd_service.entity.dict.article.ArticlePart;
import uz.ciasev.ubdd_service.entity.dict.article.ArticleViolationType;
import uz.ciasev.ubdd_service.entity.dict.person.*;
import uz.ciasev.ubdd_service.entity.dict.resolution.DecisionTypeAlias;
import uz.ciasev.ubdd_service.entity.dict.resolution.PunishmentType;
import uz.ciasev.ubdd_service.entity.temp.GaiExportTemporary;
import uz.ciasev.ubdd_service.entity.user.User;
import uz.ciasev.ubdd_service.exception.notfound.EntityByIdNotFound;
import uz.ciasev.ubdd_service.mvd_core.api.billing.dto.BillingPayeeInfoDTO;
import uz.ciasev.ubdd_service.mvd_core.api.billing.dto.BillingPayerInfoDTO;
import uz.ciasev.ubdd_service.mvd_core.api.billing.dto.BillingPaymentDTO;
import uz.ciasev.ubdd_service.repository.dict.*;
import uz.ciasev.ubdd_service.repository.dict.article.ArticlePartRepository;
import uz.ciasev.ubdd_service.repository.dict.article.ArticleRepository;
import uz.ciasev.ubdd_service.repository.dict.article.ArticleViolationTypeRepository;
import uz.ciasev.ubdd_service.repository.dict.person.*;
import uz.ciasev.ubdd_service.repository.dict.resolution.PunishmentTypeRepository;
import uz.ciasev.ubdd_service.repository.temporary.GaiExportTemporaryRepository;
import uz.ciasev.ubdd_service.repository.user.UserRepository;
import uz.ciasev.ubdd_service.service.execution.BillingExecutionService;
import uz.ciasev.ubdd_service.service.invoice.InvoiceService;
import uz.ciasev.ubdd_service.service.main.protocol.ProtocolCreateService;
import uz.ciasev.ubdd_service.service.main.resolution.UserAdmResolutionService;
import uz.ciasev.ubdd_service.service.protocol.ProtocolDTOService;
import uz.ciasev.ubdd_service.service.resolution.decision.DecisionDTOService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class CsvProcessorService {

    private static final DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final UserRepository userRepository;
    private final ProtocolDTOService protocolDTOService;
    private final ProtocolCreateService protocolCreateService;

    private final DecisionDTOService decisionDTOService;
    private final UserAdmResolutionService admResolutionService;

    private final InvoiceService invoiceService;
    private final BillingExecutionService billingExecutionService;

    private final GaiExportTemporaryRepository gaiExportTemporaryRepository;

    private final CountryRepository countryRepository;
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final ArticleRepository articleRepository;
    private final ArticlePartRepository articlePartRepository;
    private final ArticleViolationTypeRepository articleViolationTypeRepository;


    public void startProcess(String filePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(filePath);
        processCsv(resource.getFile().getPath());
    }

    private void processCsv(String filePath) {
        try (Reader reader = Files.newBufferedReader(Paths.get(filePath))) {

            CsvToBean<ProtocolData> csvToBean = new CsvToBeanBuilder<ProtocolData>(reader)
                    .withType(ProtocolData.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSeparator(',')
                    .build();

            int i = 0;
            for (ProtocolData row : csvToBean) {
                try {
                    saveToDatabase(row);
                } catch (Exception e) {
                    collectToListAndSaveSomeFile(e.getMessage());
                }
                System.out.println(++i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void collectToListAndSaveSomeFile(String errorMessage) {
        String resourcesFolderPath = "src/main/resources/errors";
        File resourcesFolder = new File(resourcesFolderPath);

        if (!resourcesFolder.exists()) {
            resourcesFolder.mkdirs();
        }

        String filePath = resourcesFolderPath + "/error_log.txt";
        File errorFile = new File(filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(errorFile, true))) {
            writer.write(errorMessage);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Async("customTaskExecutor")
    @Transactional
    private void saveToDatabase(ProtocolData protocolData) {
        GaiExportTemporary exported = gaiExportTemporaryRepository.findByExId(protocolData.getProtocol_externalId());
        if (exported != null) {
            if (exported.getIsSuccess()) return;
        } else {
            exported = new GaiExportTemporary(protocolData.getProtocol_externalId());
        }

        User user = userRepository.findByUsernameIgnoreCase("ubdd-service").orElseThrow();

        Pair<String, String> protocolResult = saveProtocol(user, protocolData);
        if (!protocolResult.getFirst().equals("SUCCESS")) {
            exported.attachResult(false, protocolResult.getSecond());
            gaiExportTemporaryRepository.save(exported);
            return;
        }

        Pair<String, String> resolutionResult = saveResolution(user, protocolData);
        if (!resolutionResult.getFirst().equals("SUCCESS")) {
            exported.attachResult(false, resolutionResult.getSecond());
            gaiExportTemporaryRepository.save(exported);
            return;
        }

        Pair<String, String> invoiceResult = saveInvoice(user, protocolData);
        if (!invoiceResult.getFirst().equals("SUCCESS")) {
            exported.attachResult(false, invoiceResult.getSecond());
            gaiExportTemporaryRepository.save(exported);
            return;
        }

        Pair<String, String> paymentResult = savePayment(user, protocolData);
        if (!paymentResult.getFirst().equals("SUCCESS")) {
            exported.attachResult(false, paymentResult.getSecond());
            gaiExportTemporaryRepository.save(exported);
            return;
        }

        exported.attachResult(true, null);
        gaiExportTemporaryRepository.save(exported);
    }

    private Pair<String, String> saveProtocol(User user, ProtocolData protocolData) {

        try {
            ProtocolRequestDTO protocolRequestDTO = new ProtocolRequestDTO();
            protocolRequestDTO.setExternalId(protocolData.getProtocol_externalId());
            protocolRequestDTO.setInspectorRegionId(protocolData.getProtocol_inspectorRegionId() == null ? null : Long.parseLong(protocolData.getProtocol_inspectorRegionId()));
            protocolRequestDTO.setInspectorDistrictId(protocolData.getProtocol_inspectorDistrictId() == null ? null : Long.parseLong(protocolData.getProtocol_inspectorDistrictId()));
            protocolRequestDTO.setInspectorPositionId(protocolData.getProtocol_inspectorPositionId() == null ? null : Long.parseLong(protocolData.getProtocol_inspectorPositionId()));
            protocolRequestDTO.setInspectorRankId(protocolData.getProtocol_inspectorRankId() == null ? null : Long.parseLong(protocolData.getProtocol_inspectorRankId()));
            protocolRequestDTO.setInspectorFio(protocolData.getProtocol_inspectorFio());
            protocolRequestDTO.setInspectorInfo(protocolData.getProtocol_inspectorInfo());
            protocolRequestDTO.setInspectorWorkCertificate(protocolData.getProtocol_inspectorWorkCertificate());
            protocolRequestDTO.setRegistrationTime(strToLocalDateTime(protocolData.getProtocol_registrationTime()));
            protocolRequestDTO.setViolationTime(strToLocalDateTime(protocolData.getProtocol_violationTime()));

            protocolRequestDTO.setArticle(buildArticleOrNull(protocolData.getProtocol_articleId()));
            protocolRequestDTO.setArticlePart(buildArticlePartOrNull(protocolData.getProtocol_articlePartId()));

            protocolRequestDTO.setFabula(protocolData.getProtocol_fabula());

            protocolRequestDTO.setRegion(buildRegionOrNull(protocolData.getProtocol_regionId()));
            protocolRequestDTO.setDistrict(buildDistrictOrNull(protocolData.getProtocol_districtId()));
            protocolRequestDTO.setMtp(buildMtpOrNull(protocolData.getProtocol_mtpId()));

            protocolRequestDTO.setAddress(protocolData.getMbprotocol_address());
            protocolRequestDTO.setIsFamiliarize(protocolData.getProtocol_isFamiliarize() == null ? null : Boolean.parseBoolean(protocolData.getProtocol_isFamiliarize()));
            protocolRequestDTO.setIsAgree(protocolData.getProtocol_isAgree() == null ? null : Boolean.parseBoolean(protocolData.getProtocol_isAgree()));
            protocolRequestDTO.setVehicleNumber(protocolData.getProtocol_vehicleNumber());

            protocolRequestDTO.setViolator(buildViolatorCreateRequestDTO(protocolData));

            protocolDTOService.buildDetailForCreateProtocol(user, () -> protocolCreateService.createElectronProtocol(user, protocolRequestDTO));
        } catch (Exception e) {
            String pro = protocolData.getProtocol_externalId() + " CREATION PROTOCOL FAILED WITH: ";
            return Pair.of(pro, e.getMessage());
        }

        return Pair.of("SUCCESS", "SUCCESS");
    }

    private Pair<String, String> saveResolution(User user, ProtocolData protocolData) {

        try {
            SingleResolutionRequestDTO resolutionRequestDTO = new SingleResolutionRequestDTO();
            resolutionRequestDTO.setExternalId(Long.parseLong(protocolData.getResolution_externalId()));
            resolutionRequestDTO.setConsiderUserInfo(protocolData.getResolution_considerUserInfo());
            resolutionRequestDTO.setInspectorPositionId(protocolData.getResolution_inspectorPositionId() == null ? -1 : Long.parseLong(protocolData.getResolution_inspectorPositionId()));
            resolutionRequestDTO.setInspectorRankId(protocolData.getResolution_inspectorRankId() == null ? -1 : Long.parseLong(protocolData.getResolution_inspectorRankId()));
            resolutionRequestDTO.setInspectorWorkCertificate(protocolData.getResolution_inspectorWorkCertificate());
            resolutionRequestDTO.setResolutionTime(strToLocalDateTime(protocolData.getResolution_resolutionTime()));
            resolutionRequestDTO.setIsArticle33(protocolData.getResolution_isArticle33() == null ? null : Boolean.parseBoolean(protocolData.getResolution_isArticle33()));
            resolutionRequestDTO.setIsArticle34(protocolData.getResolution_isArticle34() == null ? null : Boolean.parseBoolean(protocolData.getResolution_isArticle34()));
            resolutionRequestDTO.setDepartment(buildDepartmentOrNull(protocolData.getResolution_departmentId()));
            resolutionRequestDTO.setRegion(buildRegionOrNull(protocolData.getResolution_regionId()));
            resolutionRequestDTO.setDistrict(buildDistrictOrNull(protocolData.getResolution_districtId()));
            resolutionRequestDTO.setSignature(protocolData.getResolution_signature());
            resolutionRequestDTO.setDecisionType(DecisionTypeAlias.getInstanceById(protocolData.getResolution_decisionTypeId() == null ? null : Long.parseLong(protocolData.getResolution_decisionTypeId())));

            resolutionRequestDTO.setArticle(buildArticleOrNull(protocolData.getResolution_articleId()));
            resolutionRequestDTO.setArticlePart(buildArticlePartOrNull(protocolData.getResolution_articlePartId()));
            resolutionRequestDTO.setArticleViolationType(buildArticleViolationTypeOrNull(protocolData.getResolution_articleViolationTypeId()));
            resolutionRequestDTO.setExecutionFromDate(strToLocalDate(protocolData.getResolution_executionFromDate()));

            OrganPunishmentRequestDTO mainPunishment = new OrganPunishmentRequestDTO();
            mainPunishment.setPunishmentType(buildPunishmentTypeOrNull(protocolData.getResolution_mainPunishment_punishmentTypeId()));
            mainPunishment.setAmount(protocolData.getResolution_mainPunishment_amount() == null ? null : Long.parseLong(protocolData.getResolution_mainPunishment_amount()));

            mainPunishment.setIsDiscount70(protocolData.getInovice_isDiscount70() == null ? null : Boolean.parseBoolean(protocolData.getInovice_isDiscount70()));
            mainPunishment.setIsDiscount50(protocolData.getInovice_isDiscount50() == null ? null : Boolean.parseBoolean(protocolData.getInovice_isDiscount50()));

            mainPunishment.setDiscount70ForDate(strToLocalDate(protocolData.getInovice_discount70ForDate()));
            mainPunishment.setDiscount50ForDate(strToLocalDate(protocolData.getInovice_discount50ForDate()));

            mainPunishment.setDiscount70Amount(protocolData.getInovice_discount70Amount() == null ? null : (long) Double.parseDouble(protocolData.getInovice_discount70Amount()));
            mainPunishment.setDiscount50Amount(protocolData.getInovice_discount50Amount() == null ? null : (long) Double.parseDouble(protocolData.getInovice_discount50Amount()));

            resolutionRequestDTO.setMainPunishment(mainPunishment);

            decisionDTOService.buildListForCreate(() -> admResolutionService.createSingle(user, resolutionRequestDTO.getExternalId(), resolutionRequestDTO).getCreatedDecision());
        } catch (Exception e) {
            String pro = protocolData.getProtocol_externalId() + " CREATION RESOLUTION FAILED WITH: ";
            return Pair.of(pro, e.getMessage());
        }

        return Pair.of("SUCCESS", "SUCCESS");
    }


    private Pair<String, String> saveInvoice(User user, ProtocolData protocolData) {

        try {
            UbddInvoiceRequest invoiceRequest = new UbddInvoiceRequest();
            invoiceRequest.setIsDiscount70(protocolData.getInovice_isDiscount70() == null ? null : Boolean.parseBoolean(protocolData.getInovice_isDiscount70()));
            invoiceRequest.setIsDiscount50(protocolData.getInovice_isDiscount50() == null ? null : Boolean.parseBoolean(protocolData.getInovice_isDiscount50()));
            invoiceRequest.setExternalId(protocolData.getInovice_externalId() == null ? null : Long.parseLong(protocolData.getInovice_externalId()));
            invoiceRequest.setInvoiceId(protocolData.getInovice_invoiceId() == null ? null : Long.parseLong(protocolData.getInovice_invoiceId()));
            invoiceRequest.setInvoiceSerial(protocolData.getInovice_invoiceSerial());
            invoiceRequest.setInvoiceNumber(protocolData.getInovice_invoiceNumber());
            invoiceRequest.setInvoiceDate(strToLocalDate(protocolData.getInovice_invoiceDate()));
            invoiceRequest.setDiscount70ForDate(strToLocalDate(protocolData.getInovice_discount70ForDate()));
            invoiceRequest.setDiscount50ForDate(strToLocalDate(protocolData.getInovice_discount50ForDate()));
            invoiceRequest.setPenaltyPunishmentAmount(protocolData.getInovice_penaltyPunishmentAmount() == null ? null : (long) Double.parseDouble(protocolData.getInovice_penaltyPunishmentAmount()));
            invoiceRequest.setDiscount70Amount(protocolData.getInovice_discount70Amount() == null ? null : (long) Double.parseDouble(protocolData.getInovice_discount70Amount()));
            invoiceRequest.setDiscount50Amount(protocolData.getInovice_discount50Amount() == null ? null : (long) Double.parseDouble(protocolData.getInovice_discount50Amount()));
            invoiceRequest.setOrganName(protocolData.getInovice_organName());
            invoiceRequest.setBankInn(protocolData.getInovice_bankInn());
            invoiceRequest.setBankName(protocolData.getInovice_bankName());
            invoiceRequest.setBankCode(protocolData.getInovice_bankCode());
            invoiceRequest.setBankAccount(protocolData.getInovice_bankAccount());
            invoiceRequest.setPayerName(protocolData.getInovice_payerName());
            invoiceRequest.setPayerAddress(protocolData.getInovice_payerAddress());
            invoiceRequest.setPayerBirthdate(strToLocalDate(protocolData.getInovice_payerBirthdate()));

            invoiceService.create(user, invoiceRequest);
        } catch (Exception e) {
            String pro = protocolData.getProtocol_externalId() + " CREATION INVOICE FAILED WITH: ";
            return Pair.of(pro, e.getMessage());
        }

        return Pair.of("SUCCESS", "SUCCESS");
    }


    private Pair<String, String> savePayment(User user, ProtocolData protocolData) {

        try {
            BillingPaymentDTO paymentDTO = new BillingPaymentDTO();
            paymentDTO.setId(protocolData.getPayments_id() == null ? null : Long.parseLong(protocolData.getPayments_id()));
            paymentDTO.setExternalId(protocolData.getPayments_externalId() == null ? null : Long.parseLong(protocolData.getPayments_externalId()));
            paymentDTO.setInvoiceSerial(protocolData.getPayments_invoiceSerial());
            paymentDTO.setBid(protocolData.getPayments_bid());
            paymentDTO.setAmount(protocolData.getPayments_amount() == null ? null : Double.parseDouble(protocolData.getPayments_amount()));
            paymentDTO.setDocNumber(protocolData.getPayments_docNumber());
            paymentDTO.setPaidAt(strToLocalDateTime(protocolData.getPayments_paidAt()));

            BillingPayerInfoDTO payerInfoDTO = new BillingPayerInfoDTO();
            payerInfoDTO.setFromBankCode("0000");
            payerInfoDTO.setFromBankAccount("0000");
            payerInfoDTO.setFromBankName("Bank nomi ko'rsatilmagan");
            payerInfoDTO.setFromInn("0000");
            paymentDTO.setPayerInfo(payerInfoDTO);

            BillingPayeeInfoDTO payeeInfoDTO = new BillingPayeeInfoDTO();
            payeeInfoDTO.setToBankCode("0000");
            payeeInfoDTO.setToBankAccount("0000");
            payeeInfoDTO.setToBankName("Bank nomi ko'rsatilmagan");
            payeeInfoDTO.setToInn("0000");
            paymentDTO.setPayeeInfo(payeeInfoDTO);

            billingExecutionService.handlePayment(user, paymentDTO);
        } catch (Exception e) {
            String pro = protocolData.getProtocol_externalId() + " CREATION PAYMENT FAILED WITH: ";
            return Pair.of(pro, e.getMessage());
        }

        return Pair.of("SUCCESS", "SUCCESS");
    }

    private LocalDateTime strToLocalDateTime(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDateTime.parse(value, localDateTimeFormatter);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Failed to parse date-time: " + value, e);
        }
    }

    private LocalDate strToLocalDate(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            String datePart = value.split(" ")[0];
            return LocalDate.parse(datePart, localDateFormatter);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Failed to parse date-time: " + value, e);
        }
    }

    private ViolatorCreateRequestDTO buildViolatorCreateRequestDTO(ProtocolData protocolData) {
        ViolatorCreateRequestDTO violator = new ViolatorCreateRequestDTO();
        violator.setPinpp(protocolData.getProtocol_violator_pinpp());
        violator.setMobile(protocolData.getProtocol_violator_mobile());
        violator.setLandline(protocolData.getProtocol_violator_landline());


        AddressRequestDTO actualAddress = new AddressRequestDTO();
        actualAddress.setCountry(buildCountryOrNull(protocolData.getProtocol_violator_actualAddress_countryId()));
        actualAddress.setRegion(buildRegionOrNull(protocolData.getProtocol_violator_actualAddress_regionId()));
        actualAddress.setDistrict(buildDistrictOrNull(protocolData.getProtocol_violator_actualAddress_districtId()));
        actualAddress.setAddress(protocolData.getProtocol_violator_actualAddress_address());
        violator.setActualAddress(actualAddress);

        AddressRequestDTO postAddress = new AddressRequestDTO();
        postAddress.setCountry(buildCountryOrNull(protocolData.getProtocol_violator_postAddress_countryId()));
        postAddress.setRegion(buildRegionOrNull(protocolData.getProtocol_violator_postAddress_regionId()));
        postAddress.setDistrict(buildDistrictOrNull(protocolData.getProtocol_violator_postAddress_districtId()));
        postAddress.setAddress(protocolData.getProtocol_violator_postAddress_address());
        violator.setPostAddress(postAddress);

        ViolatorDetailRequestDTO violatorDetail = new ViolatorDetailRequestDTO();
        violatorDetail.setOccupation(buildOccupationOrNull(protocolData.getProtocol_violator_violatorDetail_occupationId()));
        violatorDetail.setEmploymentPlace(protocolData.getProtocol_violator_violatorDetail_employmentPlace());
        violatorDetail.setEmploymentPosition(protocolData.getProtocol_violator_violatorDetail_employmentPosition());
        violatorDetail.setAdditionally(protocolData.getProtocol_violator_violatorDetail_additionally());
        violatorDetail.setSignature(protocolData.getProtocol_violator_violatorDetail_signature());
        violator.setViolatorDetail(violatorDetail);


        PersonRequestDTO personRequestDTO = new PersonRequestDTO();
        personRequestDTO.setFirstNameKir(protocolData.getProtocol_violator_person_firstNameKir());
        personRequestDTO.setSecondNameKir(protocolData.getProtocol_violator_person_secondNameKir());
        personRequestDTO.setLastNameKir(protocolData.getProtocol_violator_person_lastNameKir());
        personRequestDTO.setFirstNameLat(protocolData.getProtocol_violator_person_firstNameLat());
        personRequestDTO.setSecondNameLat(protocolData.getProtocol_violator_person_secondNameLat());
        personRequestDTO.setLastNameLat(protocolData.getProtocol_violator_person_lastNameLat());
        personRequestDTO.setBirthDate(strToLocalDate(protocolData.getProtocol_violator_person_birthDate()));

        AddressRequestDTO birthAddress = new AddressRequestDTO();
        birthAddress.setCountry(buildCountryOrNull(protocolData.getProtocol_violator_person_birthAddress_countryId()));
        birthAddress.setRegion(buildRegionOrNull(protocolData.getProtocol_violator_person_birthAddress_regionId()));
        birthAddress.setDistrict(buildDistrictOrNull(protocolData.getProtocol_violator_person_birthAddress_districtId()));
        birthAddress.setAddress(protocolData.getProtocol_violator_person_birthAddress_address());
        personRequestDTO.setBirthAddress(birthAddress);

        personRequestDTO.setCitizenshipType(buildCitizenshipTypeOrNull(protocolData.getProtocol_violator_person_citizenshipTypeId()));
        personRequestDTO.setGender(buildGenderOrNull(protocolData.getProtocol_violator_person_genderId()));
        personRequestDTO.setNationality(buildNationalityOrNull(protocolData.getProtocol_violator_person_nationalityId()));

        violator.setPerson(personRequestDTO);

        PersonDocumentRequestDTO personDocumentRequestDTO = new PersonDocumentRequestDTO();

        String documentSeries = protocolData.getProtocol_violator_personDocument_documentSeries();
        String documentNumber = protocolData.getProtocol_violator_personDocument_documentNumber();
        personDocumentRequestDTO.setNumber(documentNumber.replaceAll("^[0-9]+", ""));
        if (documentSeries != null) {
            personDocumentRequestDTO.setSeries(documentSeries);
        } else {
            String series = documentNumber.replaceAll("[0-9]", "");
            personDocumentRequestDTO.setSeries(series);
        }


        personDocumentRequestDTO.setPersonDocumentType(buildPersonDocumentTypeOrNull(protocolData.getProtocol_violator_personDocument_documentTypeId()));
        personDocumentRequestDTO.setGivenDate(strToLocalDate(protocolData.getProtocol_violator_personDocument_documentGivenDate()));

        AddressRequestDTO givenAddress = new AddressRequestDTO();
        givenAddress.setCountry(buildCountryOrNull(protocolData.getProtocol_violator_personDocument_givenAddress_countryId()));
        givenAddress.setRegion(buildRegionOrNull(protocolData.getProtocol_violator_personDocument_givenAddress_regionId()));
        givenAddress.setDistrict(buildDistrictOrNull(protocolData.getProtocol_violator_personDocument_givenAddress_districtId()));
        givenAddress.setAddress(protocolData.getProtocol_violator_personDocument_givenAddress_address());

        personDocumentRequestDTO.setDocumentGivenAddress(givenAddress);

        violator.setDocument(personDocumentRequestDTO);

        return violator;
    }

    private Country buildCountryOrNull(String id) {
        if (id == null || id.isBlank()) return null;
//        Country country = new Country();
//        country.setId(Long.parseLong(id));
//        return country;
        return countryRepository.findById(Long.parseLong(id)).orElseThrow(
                () -> new EntityByIdNotFound(Country.class, Long.parseLong(id))
        );
    }

    private Region buildRegionOrNull(String id) {
        if (id == null || id.isBlank()) return null;
        return regionRepository.findById(Long.parseLong(id)).orElseThrow(
                () -> new EntityByIdNotFound(Region.class, Long.parseLong(id))
        );
    }

    private District buildDistrictOrNull(String id) {
        if (id == null || id.isBlank()) return null;
//        District district = new District();
//        district.setId(Long.parseLong(id));
//        return district;
        return districtRepository.findById(Long.parseLong(id)).orElseThrow(
                () -> new EntityByIdNotFound(District.class, Long.parseLong(id))
        );
    }

    private Mtp buildMtpOrNull(String id) {
        if (id == null || id.isBlank()) return null;
        Mtp mtp = new Mtp();
        mtp.setId(Long.parseLong(id));
        return mtp;
//        return mtpRepository.findById(Long.parseLong(id)).orElseThrow(
//                () -> new EntityByIdNotFound(Mtp.class, Long.parseLong(id))
//        );
    }

    private Department buildDepartmentOrNull(String id) {
        if (id == null || id.isBlank()) return null;
        Department department = new Department();
        department.setId(Long.parseLong(id));
        return department;
//        return departmentRepository.findById(Long.parseLong(id)).orElseThrow(
//                () -> new EntityByIdNotFound(Department.class, Long.parseLong(id))
//        );
    }

    private Article buildArticleOrNull(String id) {
        if (id == null || id.isBlank()) return null;
//        Article article = new Article();
//        article.setId(Long.parseLong(id));
//        return article;
        return articleRepository.findById(Long.parseLong(id)).orElseThrow(
                () -> new EntityByIdNotFound(Article.class, Long.parseLong(id))
        );
    }

    private ArticlePart buildArticlePartOrNull(String id) {
        if (id == null || id.isBlank()) return null;
//        ArticlePart articlePart = new ArticlePart();
//        articlePart.setId(Long.parseLong(id));
//        return articlePart;
        return articlePartRepository.findById(Long.parseLong(id)).orElseThrow(
                () -> new EntityByIdNotFound(ArticlePart.class, Long.parseLong(id))
        );
    }

    private ArticleViolationType buildArticleViolationTypeOrNull(String id) {
        if (id == null || id.isBlank()) return null;
//        ArticleViolationType articleViolationType = new ArticleViolationType();
//        articleViolationType.setId(Long.parseLong(id));
//        return articleViolationType;
        return articleViolationTypeRepository.findById(Long.parseLong(id)).orElseThrow(
                () -> new EntityByIdNotFound(ArticleViolationType.class, Long.parseLong(id))
        );
    }

    private Occupation buildOccupationOrNull(String id) {
        if (id == null || id.isBlank()) return null;
        Occupation occupation = new Occupation();
        occupation.setId(Long.parseLong(id));
        return occupation;
//        return occupationRepository.findById(Long.parseLong(id)).orElseThrow(
//                () -> new EntityByIdNotFound(Occupation.class, Long.parseLong(id))
//        );
    }

    private PunishmentType buildPunishmentTypeOrNull(String id) {
        if (id == null || id.isBlank()) return null;
        PunishmentType punishmentType = new PunishmentType();
        punishmentType.setId(Long.parseLong(id));
        return punishmentType;
//        return punishmentTypeRepository.findById(Long.parseLong(id)).orElseThrow(
//                () -> new EntityByIdNotFound(PunishmentType.class, Long.parseLong(id))
//        );
    }

    private CitizenshipType buildCitizenshipTypeOrNull(String id) {
        if (id == null || id.isBlank()) return null;
        CitizenshipType citizenshipType = new CitizenshipType();
        citizenshipType.setId(Long.parseLong(id));
        return citizenshipType;
//        return citizenshipTypeRepository.findById(Long.parseLong(id)).orElseThrow(
//                () -> new EntityByIdNotFound(CitizenshipType.class, Long.parseLong(id))
//        );
    }

    private Gender buildGenderOrNull(String id) {
        if (id == null || id.isBlank()) return null;
        Gender gender = new Gender();
        gender.setId(Long.parseLong(id));
        return gender;
//        return genderRepository.findById(Long.parseLong(id)).orElseThrow(
//                () -> new EntityByIdNotFound(Gender.class, Long.parseLong(id))
//        );
    }

    private Nationality buildNationalityOrNull(String id) {
        if (id == null || id.isBlank()) return null;
        Nationality nationality = new Nationality();
        nationality.setId(Long.parseLong(id));
        return nationality;
//        return nationalityRepository.findById(Long.parseLong(id)).orElseThrow(
//                () -> new EntityByIdNotFound(Nationality.class, Long.parseLong(id))
//        );
    }

    private PersonDocumentType buildPersonDocumentTypeOrNull(String id) {
        if (id == null || id.isBlank()) return null;
        PersonDocumentType personDocumentType = new PersonDocumentType();
        personDocumentType.setId(Long.parseLong(id));
        return personDocumentType;
//        return personDocumentTypeRepository.findById(Long.parseLong(id)).orElseThrow(
//                () -> new EntityByIdNotFound(PersonDocumentType.class, Long.parseLong(id))
//        );
    }
}

