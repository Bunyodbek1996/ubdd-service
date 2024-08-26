package uz.ciasev.ubdd_service.dto.internal.request.violator;

import lombok.Getter;
import lombok.Setter;
import uz.ciasev.ubdd_service.dto.internal.request.ActorRequest;
import uz.ciasev.ubdd_service.dto.internal.request.AddressRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.PersonDocumentRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.PersonRequestDTO;
import uz.ciasev.ubdd_service.exception.ErrorCode;
import uz.ciasev.ubdd_service.utils.validator.UzbAddress;
import uz.ciasev.ubdd_service.utils.validator.ValidAddress;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ViolatorCreateRequestDTO extends ViolatorRequestDTO implements ActorRequest {

    private String pinpp;

    @Valid
    @NotNull(message = "person is required")
    private PersonRequestDTO person;

    @Valid
    @NotNull(message = "document is required")
    private PersonDocumentRequestDTO document;

    @Valid
    @NotNull(message = ErrorCode.VIOLATOR_DETAIL_REQUIRED)
    private ViolatorDetailRequestDTO violatorDetail;

    @Valid
    @NotNull(message = ErrorCode.ACTUAL_ADDRESS_REQUIRED)
    @ValidAddress(message = ErrorCode.ACTUAL_ADDRESS_INVALID)
    @UzbAddress(message = ErrorCode.VIOLATOR_ACTUAL_ADDRESS_MAST_CONTENT_REGION_AND_DISTRICT)
    private AddressRequestDTO actualAddress;

    @Valid
    @NotNull(message = ErrorCode.POST_ADDRESS_REQUIRED)
    @ValidAddress(message = ErrorCode.POST_ADDRESS_INVALID)
    @UzbAddress(message = ErrorCode.VIOLATOR_POST_ADDRESS_MAST_CONTENT_REGION_AND_DISTRICT)
    private AddressRequestDTO postAddress;
}
