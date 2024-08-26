package uz.ciasev.ubdd_service.dto.internal.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.ciasev.ubdd_service.entity.Address;
import uz.ciasev.ubdd_service.entity.AddressReferenceBook;
import uz.ciasev.ubdd_service.entity.dict.Country;
import uz.ciasev.ubdd_service.entity.dict.District;
import uz.ciasev.ubdd_service.entity.dict.Region;
import uz.ciasev.ubdd_service.exception.ErrorCode;
import uz.ciasev.ubdd_service.utils.validator.ActiveOnly;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequestDTO implements GeographyRequest {

    @NotNull(message = ErrorCode.ADDRESS_COUNTRY_REQUIRED)
    @JsonProperty(value = "countryId")
    private Country country;

    @JsonProperty(value = "regionId")
    private Region region;

    @JsonProperty(value = "districtId")
    private District district;

    @Size(max = 200, message = ErrorCode.MAX_ADDRESS_LENGTH)
    private String address;

    public Address buildAddress() {
        return new Address(country, region, district, address);
    }

    public Address apply(Address addressEn) {
        addressEn.set(country, region, district, address);
        return addressEn;
    }

    public AddressRequestDTO(AddressReferenceBook book) {
        this.country = book.getCountry();
        this.region = book.getRegion();
        this.district = book.getDistrict();
        this.address = book.getAddress();
    }
}
