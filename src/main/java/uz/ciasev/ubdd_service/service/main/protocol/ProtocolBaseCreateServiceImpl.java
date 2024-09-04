package uz.ciasev.ubdd_service.service.main.protocol;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ciasev.ubdd_service.entity.dict.District;
import uz.ciasev.ubdd_service.entity.dict.Region;
import uz.ciasev.ubdd_service.entity.dict.article.Article;
import uz.ciasev.ubdd_service.entity.dict.article.ArticlePart;
import uz.ciasev.ubdd_service.entity.dict.person.CitizenshipType;
import uz.ciasev.ubdd_service.entity.dict.person.CitizenshipTypeAlias;
import uz.ciasev.ubdd_service.entity.dict.user.Position;
import uz.ciasev.ubdd_service.entity.dict.user.Rank;
import uz.ciasev.ubdd_service.exception.NotFoundException;
import uz.ciasev.ubdd_service.mvd_core.api.f1.service.F1Service;
import uz.ciasev.ubdd_service.dto.internal.request.protocol.*;
import uz.ciasev.ubdd_service.dto.internal.request.violator.ViolatorCreateRequestDTO;
import uz.ciasev.ubdd_service.entity.Person;
import uz.ciasev.ubdd_service.entity.PersonDocument;
import uz.ciasev.ubdd_service.entity.admcase.AdmCase;
import uz.ciasev.ubdd_service.entity.history.QualificationRegistrationType;
import uz.ciasev.ubdd_service.entity.protocol.Juridic;
import uz.ciasev.ubdd_service.entity.protocol.Protocol;
import uz.ciasev.ubdd_service.entity.protocol.ProtocolArticle;
import uz.ciasev.ubdd_service.entity.user.User;
import uz.ciasev.ubdd_service.entity.violator.Violator;
import uz.ciasev.ubdd_service.entity.violator.ViolatorDetail;
import uz.ciasev.ubdd_service.event.AdmEventService;
import uz.ciasev.ubdd_service.repository.dict.article.ArticlePartRepository;
import uz.ciasev.ubdd_service.service.damage.DamageMainService;
import uz.ciasev.ubdd_service.service.dict.person.CitizenshipTypeDictionaryService;
import uz.ciasev.ubdd_service.service.history.HistoryService;
import uz.ciasev.ubdd_service.service.juridic.JuridicService;
import uz.ciasev.ubdd_service.service.main.ActorService;
import uz.ciasev.ubdd_service.service.main.CitizenshipTypeCalculatingService;
import uz.ciasev.ubdd_service.service.main.PersonDataService;
import uz.ciasev.ubdd_service.service.person.PersonService;
import uz.ciasev.ubdd_service.service.protocol.ProtocolCreateRequest;
import uz.ciasev.ubdd_service.service.protocol.ProtocolService;
import uz.ciasev.ubdd_service.service.protocol.RepeatabilityService;
import uz.ciasev.ubdd_service.service.ubdd_data.UbddDataToProtocolBindService;
import uz.ciasev.ubdd_service.service.ubdd_data.old_structure.UbddOldStructureService;
import uz.ciasev.ubdd_service.service.validation.ProtocolValidationService;
import uz.ciasev.ubdd_service.utils.validator.Inspector;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProtocolBaseCreateServiceImpl implements ProtocolBaseCreateService {

    private final JuridicService juridicService;
    private final ProtocolService protocolService;
    private final RepeatabilityService protocolRepeatabilityService;
    private final DamageMainService damageMainService;
    private final ActorService newActorService;
    private final PersonDataService personDataService;
    private final ProtocolAdditionalService additionalService;
    private final UbddDataToProtocolBindService ubddDataToProtocolBindService;
    private final UbddOldStructureService ubddOldStructureService;
    private final PersonService personService;
    private final CitizenshipTypeDictionaryService citizenshipService;

    @Override
    @Transactional
    public Protocol createProtocol(@Inspector User user,
                                   ProtocolRequestDTO protocolDTO,
                                   Supplier<AdmCase> admCaseSupplier,
                                   Function<ViolatorDetail, String> fabulaBuilder) {


        if (protocolDTO.getVehicleNumber() == null) {
            Optional.ofNullable(protocolDTO.getAdditional())
                    .map(ProtocolRequestAdditionalDTO::getUbdd)
                    .map(ProtocolUbddDataRequestDTO::getVehicleNumber)
                    .ifPresent(protocolDTO::setVehicleNumber);
            Optional.ofNullable(protocolDTO.getAdditional())
                    .map(ProtocolRequestAdditionalDTO::getTransport)
                    .map(ProtocolUbddDataRequestDTO::getVehicleNumber)
                    .ifPresent(protocolDTO::setVehicleNumber);
        }

        ViolatorCreateRequestDTO violatorRequestDTO = protocolDTO.getViolator();
        Pair<Person, ? extends PersonDocument> personWithDocument;

        if (violatorRequestDTO.getPerson().getFirstNameLat() == null
                || violatorRequestDTO.getPerson().getLastNameLat() == null
                || violatorRequestDTO.getDocument().getGivenDate() == null) {
            personWithDocument = personDataService.provideByPinppOrManualDocument(violatorRequestDTO);
        } else {
            Person person = violatorRequestDTO.getPerson().buildPerson();
            person.setPinpp(violatorRequestDTO.getPinpp());
            var savedPerson = personService.findOrSave(person);
            personWithDocument = Pair.of(savedPerson, violatorRequestDTO.getDocument());
        }

        AdmCase admCase = admCaseSupplier.get();

        Pair<Violator, ViolatorDetail> violatorWithDetail = newActorService.createViolatorWithDetail(user, personWithDocument, admCase, violatorRequestDTO, protocolDTO);
        ViolatorDetail violatorDetail = violatorWithDetail.getSecond();

        Optional<Juridic> juridic = Optional.ofNullable(protocolDTO.getJuridic()).map(j -> juridicService.create(user, j));

        List<ProtocolArticle> additionArticles = protocolDTO.getAdditionArticles().stream().map(QualificationArticleRequestDTO::buildProtocolArticle).collect(Collectors.toList());
        ProtocolCreateRequest protocol = buildProtocol(protocolDTO);
        protocol.setJuridic(juridic.orElse(null));

        protocol.setFabula(fabulaBuilder.apply(violatorDetail));

        Optional.ofNullable(protocolDTO.getAdditional())
                .map(ProtocolRequestAdditionalDTO::getUbdd)
                .map(ProtocolUbddDataRequestUbddDTO::getRegNumber)
                .ifPresent(protocol::setTrackNumber);

        Protocol savedProtocol = protocolService.create(user, violatorDetail, protocol, additionArticles);

        saveRelated(user, savedProtocol, violatorWithDetail.getFirst(), protocolDTO);

        return savedProtocol;
    }

    private void saveRelated(User user, Protocol protocol, Violator violator, ProtocolRequestDTO protocolDTO) {

        protocolRepeatabilityService.replace(user, protocol, protocolDTO.getRepeatabilityProtocolsId());

        addGovDamage(user, violator, protocol, protocolDTO);

        Optional.ofNullable(protocolDTO.getAdditional())
                .ifPresent(additional -> additionalService.createProtocolAdditional(user, protocol, additional));

        if (protocolDTO.getUbddDataBind() != null) {
            ubddDataToProtocolBindService.save(user, protocol.getId(), protocolDTO.getUbddDataBind());
        } else {
            ubddOldStructureService.createBindForNewProtocol(user, protocol, protocolDTO.getAdditional());
        }
    }

    private void addGovDamage(User user,
                              Violator violator,
                              Protocol protocol,
                              ProtocolRequestDTO protocolRequestDTO) {

        Long amount = protocolRequestDTO.getGovernmentDamageAmount();
        if (Objects.nonNull(amount)) {
            damageMainService.addGovernmentDamageToProtocol(user, protocol, violator, amount);
        }
    }

    private ProtocolCreateRequest buildProtocol(ProtocolRequestDTO protocolDTO) {

        ProtocolCreateRequest protocol = new ProtocolCreateRequest();

        protocol.setArticle(protocolDTO.getArticlePart().getArticle());

        protocol.setArticlePart(protocolDTO.getArticlePart());

        protocol.setArticleViolationType(protocolDTO.getArticleViolationType());
        protocol.setIsJuridic(protocolDTO.getIsJuridic());
        protocol.setFabula(protocolDTO.getFabula());

        protocol.setRegion(protocolDTO.getRegion());
        protocol.setDistrict(protocolDTO.getDistrict());
        protocol.setMtp(protocolDTO.getMtp());
        protocol.setAddress(protocolDTO.getAddress());

        protocol.setRegistrationTime(protocolDTO.getRegistrationTime());
        protocol.setViolationTime(protocolDTO.getViolationTime());
        protocol.setFamiliarize(protocolDTO.getIsFamiliarize());
        protocol.setAgree(protocolDTO.getIsAgree());
        protocol.setExplanatory(protocolDTO.getExplanatoryText());
        protocol.setInspectorSignature(protocolDTO.getInspectorSignature());

        protocol.setLatitude(protocolDTO.getLatitude());
        protocol.setLongitude(protocolDTO.getLongitude());
        protocol.setAudioUri(protocolDTO.getAudioUri());
        protocol.setVideoUri(protocolDTO.getVideoUri());

        protocol.setIsTablet(protocolDTO.getIsTablet());
        protocol.setExternalId(protocolDTO.getExternalId());
        protocol.setUbddGroup(protocolDTO.getUbddGroup());

        if (protocolDTO.getInspectorRegionId() != null)
            protocol.setInspectorRegion(new Region(protocolDTO.getInspectorRegionId()));
        if (protocolDTO.getInspectorDistrictId() != null)
            protocol.setInspectorDistrict(new District(protocolDTO.getInspectorDistrictId()));

        protocol.setInspectorPosition(new Position(protocolDTO.getInspectorPositionId()));
        protocol.setInspectorRank(new Rank(protocolDTO.getInspectorRankId()));

        protocol.setInspectorFio(protocolDTO.getInspectorFio());
        protocol.setInspectorInfo(protocolDTO.getInspectorInfo());
        protocol.setInspectorWorkCertificate(protocolDTO.getInspectorWorkCertificate());

        protocol.setVehicleNumber(protocolDTO.getVehicleNumber());

        return protocol;
    }
}