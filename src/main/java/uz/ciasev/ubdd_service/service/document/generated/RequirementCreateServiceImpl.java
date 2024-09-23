package uz.ciasev.ubdd_service.service.document.generated;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ciasev.ubdd_service.dto.internal.*;
import uz.ciasev.ubdd_service.dto.internal.ubdd_data.UbddProtocolRequirementDTO;
import uz.ciasev.ubdd_service.entity.Person;
import uz.ciasev.ubdd_service.entity.document.RequirementGeneration;
import uz.ciasev.ubdd_service.entity.protocol.ProtocolArticlesProjection;
import uz.ciasev.ubdd_service.entity.protocol.ProtocolGroupByPersonProjection;
import uz.ciasev.ubdd_service.entity.protocol.ProtocolRequirementProjection;
import uz.ciasev.ubdd_service.entity.user.User;
import uz.ciasev.ubdd_service.exception.ErrorCode;
import uz.ciasev.ubdd_service.exception.NotFoundException;
import uz.ciasev.ubdd_service.exception.ValidationException;
import uz.ciasev.ubdd_service.repository.invoice.InvoiceRepository;
import uz.ciasev.ubdd_service.repository.protocol.ProtocolRepository;
import uz.ciasev.ubdd_service.repository.wanted_vehicle.WantedVehicleRepository;
import uz.ciasev.ubdd_service.service.pdf.PdfFile;
import uz.ciasev.ubdd_service.service.pdf.PdfService;
import uz.ciasev.ubdd_service.service.person.PersonService;
import uz.ciasev.ubdd_service.utils.FioUtils;
import uz.ciasev.ubdd_service.utils.MoneyFormatter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;


@Service
@RequiredArgsConstructor
public class RequirementCreateServiceImpl implements RequirementCreateService {

    private final RequirementGenerateRegisterService requirementCreationRegistrationService;
    private final PdfService pdfService;
    private final ProtocolRepository protocolRepository;
    private final PersonService personService;
    private final WantedVehicleRepository wantedVehicleRepository;
    private final InvoiceRepository invoiceRepository;

    private final static int PROTOCOL_REQUIREMENT_MAX_SIZE = 1000;
    private final static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy (HH:mm)");

    @Override
    @Transactional
    public PdfFile create(User user, RequirementPrintDTO requestDTO) {
        if (requestDTO.getPerson().getProtocols().size() > PROTOCOL_REQUIREMENT_MAX_SIZE) {
            throw new ValidationException(ErrorCode.REQUIREMENT_SIZE_MORE_THEN_1000);
        }

        RequirementGeneration registration = requirementCreationRegistrationService.createRegistration(user, requestDTO);
        return pdfService.getRequirement(
                registration,
                () -> protocolRepository.findProtocolRequirementInfoByProtocolIds(registration.getProtocols()),
                () -> protocolRepository.getProtocolArticlesByProtocolIds(registration.getProtocols())
        );
    }


    private List<ProtocolGroupByPersonDTO> filterProtocolsByPerson(List<Long> ids) {
        if (ids.size() > PROTOCOL_REQUIREMENT_MAX_SIZE) {
            throw new ValidationException(ErrorCode.REQUIREMENT_SIZE_MORE_THEN_1000);
        }

        List<ProtocolGroupByPersonProjection> rsl = protocolRepository.groupProtocolsByPersonIds(ids);

        Map<Long, List<Pair<Person, String>>> personMap = rsl.stream()
                .collect(groupingBy(
                        row -> row.getPerson().getId(),
                        Collectors.mapping(row -> Pair.of(row.getPerson(), row.getActualAddressText()), toList())
                ));

        Map<Long, List<Long>> protocolsByViolator = rsl.stream()
                .collect(groupingBy(
                        row -> row.getPerson().getId(),
                        Collectors.mapping(row -> row.getProtocolId(), toList())
                ));

        return protocolsByViolator.entrySet().stream()
                .map(e -> {
                    Pair<Person, String> pair = personMap.get(e.getKey()).stream().findFirst().get();
                    return new ProtocolGroupByPersonDTO(pair.getFirst(), pair.getSecond(), e.getValue());
                }).collect(toList());
    }

    @Override
    public ViolatorRequirementDTO groupProtocolsByViolator(String pinpp) {
        return personService.findByPinpp(pinpp).map(person -> {
            List<ProtocolGroupByPersonDTO> protocolGroupByPersonDTOS = filterProtocolsByPerson(List.of(person.getId()));
            ProtocolGroupByPersonDTO personDTO = protocolGroupByPersonDTOS.stream().findFirst().orElseThrow(() -> new NotFoundException("Violator with pinpp: " + pinpp + " not found"));

            ViolatorRequirementDTO violatorRequirementDTO = generateDTO(personDTO);
            List<ProtocolRequirementDTO> protocolList = convertToDTO(() -> protocolRepository.findProtocolRequirementInfoByProtocolIds(personDTO.getProtocols()),
                    () -> protocolRepository.getProtocolArticlesByProtocolIds(personDTO.getProtocols()));
            violatorRequirementDTO.setProtocols(protocolList);
            return violatorRequirementDTO;
        }).orElseThrow(() -> new NotFoundException("Violator with pinpp: " + pinpp + " not found"));
    }

    @Override
    @Transactional
    public List<UbddProtocolRequirementDTO> groupUbddProtocolsByViolator(String pinpp) {
        return groupUbddProtocolsRequirement(pinpp, true);
    }

    @Override
    @Transactional
    public List<UbddProtocolRequirementDTO> groupUbddProtocolsByVehicle(String number) {
        return groupUbddProtocolsRequirement(number, false);
    }

    private List<UbddProtocolRequirementDTO> groupUbddProtocolsRequirement(String param, boolean isPinpp) {
        List<Long> protocolIds;
        if (!isPinpp) {
            protocolIds = protocolRepository.findAllByVehicleNumberAndViolatorPinpp(param, "#absent");
            if (protocolIds.isEmpty()) {
                throw new NotFoundException("Data with vehicle number: " + param + " not found");
            }
        } else {
            protocolIds = protocolRepository.findAllByVehicleNumberAndViolatorPinpp("#absent", param);
            if (protocolIds.isEmpty()) {
                throw new NotFoundException("Data with violator pinpp: " + param + " not found");
            }
        }
        return convertToUbddDTO(() -> protocolRepository.findProtocolRequirementInfoByProtocolIds(protocolIds),
                () -> protocolRepository.getProtocolArticlesByProtocolIds(protocolIds),
                () -> invoiceRepository.getInvoicesAndProtocols(protocolIds));
    }

    @Override
    @Transactional
    public List<WantedVehicleDTO> groupWantedVehicleBy(String number) {
        return Optional.ofNullable(number)
                .map(this.wantedVehicleRepository::findAllWantedCardByVehicle)
                .orElse(List.of()).stream().map(WantedVehicleDTO::new)
                .collect(toList());
    }

    private List<ProtocolRequirementDTO> convertToDTO
            (Supplier<List<ProtocolRequirementProjection>> protocolsSupplier, Supplier<List<ProtocolArticlesProjection>> articlesSupplier) {
        List<ProtocolArticlesProjection> protocolArticles = articlesSupplier.get();

        Counter counter = new Counter();

        return protocolsSupplier.get()
                .stream()
                .map(projection -> {

                    ProtocolRequirementDTO protocolModel = new ProtocolRequirementDTO();

                    protocolModel.setFio(String.format("%s %s %s",
                            projection.getViolatorLastNameLat(),
                            projection.getViolatorFirstNameLat(),
                            projection.getViolatorSecondNameLat()));
                    protocolModel.setBirthDate(formatDate(projection.getViolatorBirthDate()));
                    protocolModel.setOrgan(projection.getOrganName());
                    protocolModel.setPlace(projection.getDistrictName());
                    protocolModel.setNumber(String.format("%s-%s",
                            projection.getProtocolSeries(),
                            projection.getProtocolNumber()
                    ));
                    protocolModel.setConsiderDate(formatDateTime(
                            Optional.ofNullable(projection.getResolutionTime()).orElse(projection.getAdmCaseConsideredTime())
                    ));

                    protocolModel.setRow(counter.nextValue());

                    protocolModel.setRegistrationDate(formatDateTime(projection.getRegistrationTime()));
                    protocolModel.setArticles(
                            protocolArticles.stream()
                                    .filter(pa -> pa.getProtocolId().equals(projection.getProtocolId()))
                                    .map(pa -> new ArticleRequirementDTO(
                                            pa.getArticleId(),
                                            pa.getArticlePartId(),
                                            sj(
                                                    pa.getArticlePartShortName(),
                                                    pa.getArticleViolationTypeShortName(),
                                                    " "
                                            )
                                    )).collect(toList())
                    );
                    protocolModel.setStatus(
                            Optional.ofNullable(projection.getDecisionStatus()).orElse(projection.getAdmCaseStatus())
                    );
                    protocolModel.setIs34(Optional.ofNullable(projection.getDecisionIsArticle34()).orElse(false));

                    protocolModel.setDecisionNumber(sj(
                            projection.getDecisionSeries(),
                            projection.getDecisionNumber(),
                            "-"
                    ));
                    protocolModel.setMainPunishment(sj(
                            projection.getMainPunishmentType(),
                            projection.getMainPunishmentAmount(),
                            ": "
                    ));
                    protocolModel.setAdditionalPunishment(sj(
                            projection.getAdditionalPunishmentType(),
                            projection.getAdditionalPunishmentAmount(),
                            ": "
                    ));

                    protocolModel.setCompensation(sjc(
                            projection.getCompensationAmount(),
                            projection.getCompensationPaidAmount()
                    ));

                    return protocolModel;
                }).collect(toList());
    }

    private List<UbddProtocolRequirementDTO> convertToUbddDTO
            (Supplier<List<ProtocolRequirementProjection>> protocolsSupplier, Supplier<List<ProtocolArticlesProjection>> articlesSupplier) {
        List<ProtocolArticlesProjection> protocolArticles = articlesSupplier.get();

        return protocolsSupplier.get()
                .stream()
                .map(projection -> {
                    UbddProtocolRequirementDTO protocolModel = new UbddProtocolRequirementDTO(projection);
                    protocolModel.setBirthDate(formatDate(projection.getViolatorBirthDate()));
                    protocolModel.setRegistrationDate(formatDateTime(projection.getRegistrationTime()));
                    protocolArticles.stream()
                            .filter(pa -> pa.getProtocolId().equals(projection.getProtocolId()))
                            .forEach(protocolModel::addArticle);
                    return protocolModel;
                }).collect(toList());
    }

    private List<UbddProtocolRequirementDTO> convertToUbddDTO
            (Supplier<List<ProtocolRequirementProjection>> protocolsSupplier,
             Supplier<List<ProtocolArticlesProjection>> articlesSupplier,
             Supplier<List<String[]>> invoicesSupplier) {

        List<ProtocolArticlesProjection> protocolArticles = articlesSupplier.get();
        List<String[]> protocolsInvoices = invoicesSupplier.get();

        return protocolsSupplier.get()
                .stream()
                .map(projection -> {
                    UbddProtocolRequirementDTO protocolModel = new UbddProtocolRequirementDTO(projection);
                    protocolModel.setBirthDate(formatDate(projection.getViolatorBirthDate()));
                    protocolModel.setRegistrationDate(formatDateTime(projection.getRegistrationTime()));
                    protocolArticles.stream()
                            .filter(pa -> pa.getProtocolId().equals(projection.getProtocolId()))
                            .forEach(protocolModel::addArticle);
                    protocolsInvoices.stream()
                            .filter(array -> array[0].equals(String.valueOf(projection.getProtocolId())))
                            .forEach(array -> protocolModel.setInvoice(array[1]));
                    return protocolModel;
                }).collect(toList());
    }

    private ViolatorRequirementDTO generateDTO(ProtocolGroupByPersonDTO personDTO) {
        return ViolatorRequirementDTO.builder()
                .firstname(personDTO.getFirstNameKir())
                .secondName(personDTO.getSecondNameKir())
                .lastName(personDTO.getLastNameKir())
                .nameLat(FioUtils.buildFullFio(
                        personDTO.getLastNameLat(),
                        personDTO.getFirstNameLat(),
                        personDTO.getSecondNameLat()))
                .residentAddress(personDTO.getActualAddress())
                .createdDate(LocalDateTime.now())
                .birhtDate(personDTO.getBirthDate())
                .build();
    }

    private String formatDate(LocalDate date) {
        return (date != null)
                ? date.format(dateFormat)
                : null;
    }

    private String formatDateTime(LocalDateTime localDateTime) {
        return (localDateTime != null)
                ? localDateTime.format(dateTimeFormat)
                : null;
    }

    private String sj(Object str1, Object str2, String delimiter) {
        StringJoiner j = new StringJoiner("");
        Optional.ofNullable(str1).map(Object::toString).ifPresent(j::add);
        Optional.ofNullable(str2).map(s -> (j.length() > 0 ? delimiter : "") + s.toString()).ifPresent(j::add);
        return j.toString();
    }

    private String sjc(Long str1, Long str2) {
        long amount = Optional.ofNullable(str1).orElse(0L);
        long amountPaid = Optional.ofNullable(str2).orElse(0L);
        if (amount == 0L && amountPaid == 0L) {
            return "";
        }
        return String.format("Belgilangan zarar: %s so’m, Undirilgan: %s so’m", MoneyFormatter.coinToString(amount), MoneyFormatter.coinToString(amountPaid));
    }

    private static class Counter {
        private Integer it = 1;

        public Integer nextValue() {
            return it++;
        }
    }
}
