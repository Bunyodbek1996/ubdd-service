package uz.ciasev.ubdd_service.mvd_core.api.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingPaymentDTO {

    @NotNull(message = "id field required")
    private Long id;

    private Long admCaseId;

    @NotNull(message = "createdByEmi field required")
    private Boolean createdByEmi;

    private Long externalId;

    private String invoiceSerial;

    @NotNull(message = "bid field required")
    private String bid;

    @NotNull(message = "amount field required")
    private Double amount;

    @NotNull(message = "docNumber field required")
    private String docNumber;

    @NotNull(message = "paidAt field required")
    private LocalDateTime paidAt;

    @Valid
    @NotNull(message = "payerInfo field required")
    private BillingPayerInfoDTO payerInfo;

    @Valid
    @NotNull(message = "payeeInfo field required")
    private BillingPayeeInfoDTO payeeInfo;

}
