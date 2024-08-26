package uz.ciasev.ubdd_service.service.person;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import uz.ciasev.ubdd_service.mvd_core.api.f1.dto.F1Document;
import uz.ciasev.ubdd_service.dto.internal.request.PersonDocumentRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.PersonRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.response.PersonListResponseDTO;
import uz.ciasev.ubdd_service.dto.internal.response.PersonResponseDTO;
import uz.ciasev.ubdd_service.entity.Person;
import uz.ciasev.ubdd_service.exception.notfound.EntityByIdNotFound;
import uz.ciasev.ubdd_service.repository.PersonRepository;
import uz.ciasev.ubdd_service.service.address.AddressService;
import uz.ciasev.ubdd_service.service.main.CitizenshipTypeCalculatingService;

import javax.xml.bind.DatatypeConverter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonServiceImpl implements PersonService {

    private final AddressService addressService;
    private final PersonRepository personRepository;
    private final CitizenshipTypeCalculatingService citizenshipTypeService;

    @Override
    public Optional<Person> getByRealPinpp(String pinpp) {
        return personRepository.findByPinppAndIsRealPinpp(pinpp, true);
    }

    @Override
    public Person findById(Long id) {
        return personRepository
                .findById(id)
                .orElseThrow(() -> new EntityByIdNotFound(Person.class, id));
    }

    @Override
    public PersonListResponseDTO findDetailById(Long id) {
        return new PersonListResponseDTO(findById(id));
    }

    @Override
    public List<PersonListResponseDTO> findAllDetailById(List<Long> ids) {
        return personRepository
                .findAllById(ids)
                .stream()
                .map(PersonListResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Person findOrSave(Person person) {
        Optional<Person> existPerson = findByPinpp(person);
        existPerson.ifPresent(p -> person.setId(p.getId()));
        return saveAddressAndPerson(person);
    }

    @Override
    public Optional<Person> findByPinpp(String pinpp) {
        return personRepository.findByPinpp(pinpp);
    }

    private Optional<Person> findByPinpp(Person person) {
        return personRepository.findByPinppAndIsRealPinpp(person.getPinpp(), person.isRealPinpp());
    }

    private Person saveAddressAndPerson(Person person) {

        log.debug("PERSON SAVE ATTEMPT = FIO:{}, ID:{}, CALL-STACK:{} (QWAS)",
                person.getFIOLat(),
                person.getId(),
                Arrays.stream(Thread.currentThread().getStackTrace())
                        .filter(t -> t.getClassName().contains("uz.ciasev.ubdd_service"))
                        .collect(Collectors.toList()).toString());

        addressService.save(person.getBirthAddress());
        return personRepository.saveAndFlush(person);
    }

    @Override
    public Person buildFromF1(F1Document f1Document) {

        Person person = new Person();

        person.setPinpp(f1Document.getPinpp());
        person.setFirstNameKir(f1Document.getFirstNameKir());
        person.setSecondNameKir(f1Document.getSecondNameKir());
        person.setLastNameKir(f1Document.getLastNameKir());
        person.setFirstNameLat(f1Document.getFirstNameLat());
        person.setSecondNameLat(f1Document.getSecondNameLat());
        person.setLastNameLat(f1Document.getLastNameLat());
        person.setBirthDate(f1Document.getBirthDate());
        person.setBirthAddress(f1Document.getBirthAddress());
        person.setGender(f1Document.getGender());
        person.setNationality(f1Document.getNationality());
        person.setRealPinpp(true);
        person.setCitizenshipType(citizenshipTypeService.calculate(f1Document));

        return person;
    }

    @Override
    public Person buildManual(PersonDocumentRequestDTO docDTO, PersonRequestDTO personDTO) {
        Person person = personDTO.buildPerson();
        person.setRealPinpp(false);
        person.setPinpp(generateFakePinpp(personDTO, docDTO));

        return person;
    }

    @Override
    public PersonResponseDTO convertToDTO(Person person) {
        return new PersonResponseDTO(person, addressService.findDTOById(person.getBirthAddressId()));
    }

    @Override
    public String generateFakePinpp(PersonRequestDTO personDTO, PersonDocumentRequestDTO docDTO) {
        String personUniqueString = buildPersonUniqueString(personDTO, docDTO);
        byte[] hash = DigestUtils.md5Digest(personUniqueString.getBytes());

        return DatatypeConverter.printHexBinary(hash);
    }

    @Override
    public List<Person> findViolatorsPinppByAdmCaseId(Long caseId) {
        return personRepository.findViolatorsPinppByAdmCaseId(caseId);
    }

    private String buildPersonUniqueString(PersonRequestDTO personDTO, PersonDocumentRequestDTO docDTO) {
        return Stream.of(
                personDTO.getFirstNameLat(),
                personDTO.getSecondNameLat(),
                personDTO.getLastNameLat(),
                personDTO.getBirthDate().getYear(),
                personDTO.getBirthDate().getMonthValue(),
                personDTO.getBirthDate().getDayOfMonth(),
                personDTO.getBirthAddress().getCountry().getId(),
                personDTO.getGender().getId(),
                personDTO.getNationality().getId(),
                personDTO.getCitizenshipType().getId(),
                docDTO.getSeries(),
                docDTO.getNumber(),
                docDTO.getGivenDate().getYear(),
                docDTO.getGivenDate().getMonthValue(),
                docDTO.getGivenDate().getDayOfMonth())
                .map(String::valueOf)
                .collect(Collectors.joining(";"))
                .toUpperCase();
    }
}
