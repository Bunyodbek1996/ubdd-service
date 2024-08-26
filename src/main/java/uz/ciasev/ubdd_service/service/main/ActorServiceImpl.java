package uz.ciasev.ubdd_service.service.main;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ciasev.ubdd_service.dto.internal.request.ActorRebindRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.participant.ParticipantProtocolRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.protocol.ProtocolRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.victim.VictimProtocolRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.violator.ViolatorCreateRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.response.PersonListResponseDTO;
import uz.ciasev.ubdd_service.entity.*;
import uz.ciasev.ubdd_service.entity.admcase.AdmCase;
import uz.ciasev.ubdd_service.entity.dict.person.AgeCategory;
import uz.ciasev.ubdd_service.entity.participant.Participant;
import uz.ciasev.ubdd_service.entity.participant.ParticipantDetail;
import uz.ciasev.ubdd_service.entity.protocol.Protocol;
import uz.ciasev.ubdd_service.entity.user.User;
import uz.ciasev.ubdd_service.entity.victim.Victim;
import uz.ciasev.ubdd_service.entity.victim.VictimDetail;
import uz.ciasev.ubdd_service.entity.violator.Violator;
import uz.ciasev.ubdd_service.entity.violator.ViolatorDetail;
import uz.ciasev.ubdd_service.exception.ErrorCode;
import uz.ciasev.ubdd_service.exception.ValidationException;
import uz.ciasev.ubdd_service.repository.participant.ParticipantDetailRepository;
import uz.ciasev.ubdd_service.repository.victim.VictimDetailRepository;
import uz.ciasev.ubdd_service.repository.violator.ViolatorDetailRepository;
import uz.ciasev.ubdd_service.repository.violator.ViolatorRepository;
import uz.ciasev.ubdd_service.service.AdmCaseChangeReasonService;
import uz.ciasev.ubdd_service.service.history.HistoryService;
import uz.ciasev.ubdd_service.service.LastEmploymentInfoService;
import uz.ciasev.ubdd_service.service.address.AddressService;
import uz.ciasev.ubdd_service.service.admcase.AdmCaseAccessService;
import uz.ciasev.ubdd_service.service.admcase.AdmCaseService;
import uz.ciasev.ubdd_service.service.aop.signature.DigitalSignatureCheck;
import uz.ciasev.ubdd_service.entity.signature.SignatureEvent;
import uz.ciasev.ubdd_service.service.aop.violator.ViolatorAspectWorkerService;
import uz.ciasev.ubdd_service.service.dict.ExternalSystemDictionaryService;
import uz.ciasev.ubdd_service.service.dict.person.AgeCategoryDictionaryService;
import uz.ciasev.ubdd_service.service.participant.ParticipantService;
import uz.ciasev.ubdd_service.service.person.PersonService;
import uz.ciasev.ubdd_service.service.protocol.ProtocolService;
import uz.ciasev.ubdd_service.service.validation.ProtocolValidationService;
import uz.ciasev.ubdd_service.service.victim.VictimService;
import uz.ciasev.ubdd_service.service.violator.ViolatorService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static uz.ciasev.ubdd_service.entity.action.ActionAlias.REBIND_VIOLATOR;
@Service
@RequiredArgsConstructor
public class ActorServiceImpl implements ActorService {

    private final VictimService victimService;
    private final ParticipantService participantService;
    private final ViolatorService violatorService;
    private final VictimDetailRepository victimDetailRepository;
    private final ParticipantDetailRepository participantDetailRepository;
    private final ViolatorDetailRepository violatorDetailRepository;
    private final AddressService addressService;
    private final ExternalSystemDictionaryService externalSystemService;
    private final AgeCategoryDictionaryService ageCategoryService;
    private final PersonDataService personDataService;
    private final PersonService personService;
    private final AdmCaseService admCaseService;
    private final ProtocolService protocolService;
    private final PersonDataService admPersonService;
    private final AdmCaseAccessService admCaseAccessService;
    private final ProtocolValidationService protocolValidationService;
    private final AdmCaseChangeReasonService admCaseChangeReasonService;
    private final ViolatorRepository violatorRepository;
    private final HistoryService historyService;
    private final LastEmploymentInfoService lastEmploymentInfoService;

    @Override
    public Pair<Violator, ViolatorDetail> createViolatorWithDetail(User user,
                                                                   Pair<Person, ? extends PersonDocument> personWithDocument,
                                                                   AdmCase admCase,
                                                                   ViolatorCreateRequestDTO violatorRequestDTO,
                                                                   ProtocolRequestDTO protocol) {

        PersonDocument document = personWithDocument.getSecond();
        Person person = personWithDocument.getFirst();

        String photoUri = null;

        Violator violator = violatorRequestDTO.buildViolator();

        violator.setAdmCase(admCase);
        violator.setPerson(person);

        Address postAddress = addressService.save(violatorRequestDTO.getPostAddress().buildAddress());
        violator.setPostAddress(postAddress);

        Address actualAddress = addressService.save(violatorRequestDTO.getActualAddress().buildAddress());
        violator.setActualAddress(actualAddress);

        violator.setPhotoUri(photoUri);

        Violator savedViolator = violatorService.getOrSave(violator);

        ViolatorDetail violatorDetail = violatorRequestDTO.getViolatorDetail().buildDetail();
        violatorDetail.setUser(user);
        violatorDetail.setViolator(violator);
        setDocumentData(person, document, violatorDetail, photoUri, protocol);

        ViolatorDetail savedViolatorDetail = violatorDetailRepository.saveAndFlush(violatorDetail);

        return Pair.of(savedViolator, savedViolatorDetail);
    }

    @Override
    public Pair<Victim, VictimDetail> createVictimWithDetail(User user,
                                                             Pair<Person, ? extends PersonDocument> personWithDocument,
                                                             AdmCase admCase,
                                                             Protocol protocol,
                                                             VictimProtocolRequestDTO victimRequestDTO) {

        PersonDocument document = personWithDocument.getSecond();
        Person person = personWithDocument.getFirst();

        String photoUri = personDataService.getPhotoByDocument(document);
        Address postAddress = addressService.save(victimRequestDTO.getPostAddress().buildAddress());
        Address actualAddress = addressService.save(victimRequestDTO.getActualAddress().buildAddress());

        Victim victim = Victim.builder()
                .person(person)
                .admCase(admCase)
                .actualAddress(actualAddress)
                .postAddress(postAddress)
                .photoUri(photoUri)
                .build();

        victimRequestDTO.applyTo(victim);

        Victim savedVictim = victimService.getOrSave(victim);

        VictimDetail victimDetail = victimRequestDTO.getVictimDetail().buildDetail();
        victimDetail.setUser(user);
        victimDetail.setVictim(savedVictim);
        victimDetail.setProtocol(protocol);

        setDocumentData(person, document, victimDetail, photoUri, protocol);
        lastEmploymentInfoService.addLastEmploymentInfoToOwner(
                user, victimDetail, victimRequestDTO.getVictimDetail().getLastEmploymentInfoDTO()
        );

        try {
            VictimDetail savedVictimDetail = victimDetailRepository.save(victimDetail);
            victimDetailRepository.flush();
            return Pair.of(savedVictim, savedVictimDetail);
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException(ErrorCode.VICTIM_ALREADY_IN_PROTOCOL);
        }
    }

    @Override
    public Pair<Participant, ParticipantDetail> createParticipantWithDetail(User user,
                                                                            Pair<Person, ? extends PersonDocument> personWithDocument,
                                                                            AdmCase admCase,
                                                                            Protocol protocol,
                                                                            ParticipantProtocolRequestDTO participantRequestDTO) {

        PersonDocument document = personWithDocument.getSecond();
        Person person = personWithDocument.getFirst();

        Address actualAddress = addressService.save(participantRequestDTO.getActualAddress().buildAddress());
        String photoUri = personDataService.getPhotoByDocument(document);

        Participant participant = participantRequestDTO.buildParticipant();
        participant.setPerson(person);
        participant.setAdmCase(admCase);
        participant.setParticipantType(participantRequestDTO.getParticipantType());
        participant.setPhotoUri(photoUri);
        participant.setActualAddress(actualAddress);

        Participant savedParticipant = participantService.getOrSave(participant);

        ParticipantDetail participantDetail = participantRequestDTO.getParticipantDetail().buildDetail();
        participantDetail.setUser(user);
        participantDetail.setParticipant(savedParticipant);
        participantDetail.setProtocol(protocol);

        setDocumentData(person, document, participantDetail, photoUri, protocol);

        ParticipantDetail savedParticipantDetail = participantDetailRepository.save(participantDetail);

        return Pair.of(savedParticipant, savedParticipantDetail);
    }

    @Override
    public void deleteVictimDetail(VictimDetail victimDetail) {

        Victim victim = victimDetail.getVictim();
        victimDetailRepository.delete(victimDetail);
        deleteIfNotHasDetails(victim);
    }

    @Override
    public void deleteParticipantDetail(ParticipantDetail participantDetail) {

        Participant participant = participantDetail.getParticipant();
        participantDetailRepository.delete(participantDetail);
        deleteIfNotHasDetails(participant);
    }

    @Override
    public void deleteIfNotHasDetails(Participant participant) {

        if (!participantDetailRepository.existsByParticipantId(participant.getId())) {
            participantService.delete(participant);
        }
    }


    @Override
    public void deleteIfNotHasDetails(Victim victim) {

        if (!victimDetailRepository.existsByVictimId(victim.getId())) {
            victimService.delete(victim);
        }
    }


    @Transactional
    @DigitalSignatureCheck(event = SignatureEvent.VIOLATOR_REBIND)
    public Violator rebindViolator(User user, Long violatorId, ActorRebindRequestDTO requestDTO) {

        Violator violator = violatorService.getById(violatorId);
        AdmCase admCase = admCaseService.getById(violator.getAdmCaseId());
        admCaseAccessService.checkConsiderActionWithAdmCase(user, REBIND_VIOLATOR, admCase);
        if (violatorService.findByAdmCaseId(admCase.getId()).size() != 1) {
            throw new ValidationException(ErrorCode.ADM_CASE_NOT_SINGLE);
        }

        List<Protocol> protocols = protocolService.findAllByViolatorId(violatorId);
        Pair<Person, ? extends PersonDocument> personWithDocument = admPersonService.provideByPinppOrManualDocument(requestDTO);
        PersonDocument doc = personWithDocument.getSecond();
        Person violatorPerson = personWithDocument.getFirst();

        validate(admCase, violatorPerson, protocols);

        String photoUri = admPersonService.getPhotoByDocument(doc);

        admCaseChangeReasonService.create(user, admCase, violator, requestDTO.getChangeReason());
        historyService.registerViolatorRebind(user, protocols, violatorPerson, doc);

        for (Protocol protocol : protocols) {
            ViolatorDetail violatorDetail = protocol.getViolatorDetail();
            setDocumentData(violatorPerson, doc, violatorDetail, photoUri, protocol);
            violatorDetailRepository.saveAndFlush(violatorDetail);
        }

        violator.setPerson(violatorPerson);
        violator.setPhotoUri(photoUri);

        violator.setInn(null);

        return violatorRepository.saveAndFlush(violator);
    }

    private void validate(AdmCase admCase, Person violatorPerson, List<Protocol> protocols) {

        protocols.stream()
                .forEach(p -> protocolValidationService.validateViolatorByProtocol(p, violatorPerson));


        boolean equalWithVictim = victimService.findByAdmCaseId(admCase.getId())
                .stream()
                .filter(v -> v.getPerson().getPinpp().equals(violatorPerson.getPinpp()))
                .findAny()
                .isPresent();

        if (equalWithVictim) {
            throw new ValidationException(ErrorCode.VIOLATOR_AND_VICTIM_EQUAL);
        }

        boolean equalWithParticipant = participantService.findAllByAdmCaseId(admCase.getId())
                .stream()
                .filter(p -> p.getPerson().getPinpp().equals(violatorPerson.getPinpp()))
                .findAny()
                .isPresent();

        if (equalWithParticipant) {
            throw new ValidationException(ErrorCode.VIOLATOR_AND_PARTICIPANTS_EQUAL);
        }
    }

    @Override
    public boolean isAllVictimsRelatedWithViolator(List<Long> victimsId, Violator violator) {

        if (victimsId == null || victimsId.isEmpty())
            return true;

        return victimService
                .findByViolatorId(violator)
                .stream()
                .map(Victim::getId)
                .collect(Collectors.toSet())
                .containsAll(victimsId);
    }

    @Override
    public boolean isAllVictimsRelatedWithAdmCase(List<Long> victimsId, Long admCaseId) {

        if (victimsId == null || victimsId.isEmpty())
            return true;

        return victimService
                .findByAdmCaseId(admCaseId)
                .stream()
                .map(Victim::getId)
                .collect(Collectors.toSet())
                .containsAll(victimsId);
    }

    private void setDocumentData(Person person, PersonDocument document, ActorDetails violatorDetail, String photoUri,  AdmProtocol protocol) {

        AgeCategory ageCategory = ageCategoryService.getByBirthdate(true, person.getBirthDate(), protocol.getViolationTime());
        Address givenAddress = addressService.save(document.getGivenAddress());

        violatorDetail.setAgeCategory(ageCategory);
        violatorDetail.setPersonDocumentType(document.getPersonDocumentType());
        violatorDetail.setDocumentSeries(document.getSeries());
        violatorDetail.setDocumentNumber(document.getNumber());
        violatorDetail.setDocumentGivenDate(document.getGivenDate());
        violatorDetail.setDocumentExpireDate(document.getExpireDate());
        violatorDetail.setDocumentGivenAddress(givenAddress);
    }

}
