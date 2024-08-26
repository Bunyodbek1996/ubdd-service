package uz.ciasev.ubdd_service.dto.internal.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.ciasev.ubdd_service.entity.Address;
import uz.ciasev.ubdd_service.entity.PersonDocument;
import uz.ciasev.ubdd_service.entity.dict.ExternalSystemAlias;
import uz.ciasev.ubdd_service.entity.dict.person.PersonDocumentType;
import uz.ciasev.ubdd_service.exception.ErrorCode;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonDocumentRequestDTO implements PersonDocument {

    @Size(max = 10, message = ErrorCode.MAX_DOCUMENT_SERIES_LENGTH)
    @NotBlank(message = ErrorCode.DOCUMENT_SERIES_REQUIRED)
    @JsonProperty(value = "documentSeries")
    private String series;

    @Size(max = 32, message = ErrorCode.MAX_DOCUMENT_NUMBER_LENGTH)
    @NotBlank(message = ErrorCode.DOCUMENT_NUMBER_REQUIRED)
    @JsonProperty(value = "documentNumber")
    private String number;

    @NotNull(message = ErrorCode.DOCUMENT_TYPE_REQUIRED)
    @JsonProperty(value = "documentTypeId")
    private PersonDocumentType personDocumentType;

    @Valid
    @NotNull(message = ErrorCode.DOCUMENT_GIVEN_ADDRESS_REQUIRED)
    private AddressRequestDTO documentGivenAddress;

    @NotNull(message = ErrorCode.DOCUMENT_GIVEN_DATE_REQUIRED)
    @JsonProperty(value = "documentGivenDate")
    private LocalDate givenDate;

    @JsonProperty(value = "documentExpireDate")
    private LocalDate expireDate;

    @Override
    public Optional<Address> getManzilAddress() {
        return Optional.empty();
    }

    @Override
    public Address getGivenAddress() {
        return (this.documentGivenAddress != null) ? this.documentGivenAddress.buildAddress() : null;
    }

    @Override
    public Optional<Address> getResidentAddress() {
        return Optional.empty();
    }

    @Override
    public Optional<ExternalSystemAlias> getExternalSystem() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getExternalId() {
        return Optional.empty();
    }

    @Override
    public BigInteger getPhotoId() {
        return null;
    }

    @Override
    public String getPhotoType() {
        return null;
    }
}