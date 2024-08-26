package uz.ciasev.ubdd_service.dto.internal.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.ciasev.ubdd_service.entity.Person;
import uz.ciasev.ubdd_service.entity.dict.person.CitizenshipType;
import uz.ciasev.ubdd_service.entity.dict.person.Gender;
import uz.ciasev.ubdd_service.entity.dict.person.Nationality;
import uz.ciasev.ubdd_service.exception.ErrorCode;
import uz.ciasev.ubdd_service.utils.validator.ActiveOnly;
import uz.ciasev.ubdd_service.utils.validator.ValidAddress;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonRequestDTO {

    @Size(max = 50, message = ErrorCode.MAX_FIRST_NAME_KIR_LENGTH)
    @NotBlank(message = ErrorCode.FIRST_NAME_KIR_REQUIRED)
    private String firstNameKir;

    @Size(max = 50, message = ErrorCode.MAX_SECOND_NAME_KIR_LENGTH)
    private String secondNameKir;

    @Size(max = 50, message = ErrorCode.MAX_LAST_NAME_KIR_LENGTH)
    @NotBlank(message = ErrorCode.LAST_NAME_KIR_REQUIRED)
    private String lastNameKir;

    @Size(max = 50, message = ErrorCode.MAX_FIRST_NAME_LAT_LENGTH)
    @NotBlank(message = ErrorCode.FIRST_NAME_LAT_REQUIRED)
    private String firstNameLat;

    @Size(max = 50, message = ErrorCode.MAX_SECOND_NAME_LAT_LENGTH)
    private String secondNameLat;

    @Size(max = 50, message = ErrorCode.MAX_LAST_NAME_LAT_LENGTH)
    @NotBlank(message = ErrorCode.LAST_NAME_LAT_REQUIRED)
    private String lastNameLat;

    @NotNull(message = ErrorCode.BIRTH_DATE_REQUIRED)
    private LocalDate birthDate;

    @Valid
    @NotNull(message = ErrorCode.BIRTH_ADDRESS_REQUIRED)
    @ValidAddress(message = ErrorCode.BIRTH_ADDRESS_INVALID)
    private AddressRequestDTO birthAddress;

    @NotNull(message = ErrorCode.CITIZENSHIP_TYPE_REQUIRED)
    @ActiveOnly(message = ErrorCode.CITIZENSHIP_TYPE_DEACTIVATED)
    @JsonProperty(value = "citizenshipTypeId")
    private CitizenshipType citizenshipType;

    @NotNull(message = ErrorCode.GENDER_REQUIRED)
    @ActiveOnly(message = ErrorCode.GENDER_DEACTIVATED)
    @JsonProperty(value = "genderId")
    private Gender gender;

    @NotNull(message = ErrorCode.NATIONALITY_REQUIRED)
    @ActiveOnly(message = ErrorCode.NATIONALITY_DEACTIVATED)
    @JsonProperty(value = "nationalityId")
    private Nationality nationality;

    public Person buildPerson() {
        Person person = new Person();

        person.setFirstNameKir(this.firstNameKir);
        person.setSecondNameKir(this.secondNameKir);
        person.setLastNameKir(this.lastNameKir);
        person.setFirstNameLat(this.firstNameLat);
        person.setSecondNameLat(this.secondNameLat);
        person.setLastNameLat(this.lastNameLat);
        person.setBirthDate(this.birthDate);
        person.setBirthAddress(this.birthAddress.buildAddress());
        person.setCitizenshipType(this.citizenshipType);
        person.setGender(this.gender);
        person.setNationality(this.nationality);
        person.setRealPinpp(true);

        return person;
    }
}

