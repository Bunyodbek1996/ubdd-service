package uz.ciasev.ubdd_service.dto.ubdd;

import lombok.Data;
import lombok.NoArgsConstructor;
import uz.ciasev.ubdd_service.entity.invoice.Invoice;
import uz.ciasev.ubdd_service.entity.invoice.InvoiceOwnerTypeAlias;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UbddInvoiceRequest {

    @NotNull(message = "externalId is required")
    private Long externalId;

    private Boolean isDiscount70;

    private Boolean isDiscount50;

    private Long invoiceId;

    @NotNull(message = "invoiceSerial is required")
    private String invoiceSerial;

    @NotNull(message = "invoiceNumber is required")
    private String invoiceNumber;

    @NotNull(message = "invoiceDate is required")
    private LocalDate invoiceDate;

    private LocalDate discount70ForDate;

    private LocalDate discount50ForDate;

    @NotNull(message = "penaltyPunishmentAmount is required")
    private Long penaltyPunishmentAmount;

    private Long penaltyPunishmentId;

    private Long discount70Amount;

    private Long discount50Amount;

    @NotNull(message = "organName is required")
    private String organName;

    @NotNull(message = "bankInn is required")
    private String bankInn;

    @NotNull(message = "bankName is required")
    private String bankName;

    @NotNull(message = "bankCode is required")
    private String bankCode;

    @NotNull(message = "bankAccount is required")
    private String bankAccount;

    @NotNull(message = "payerName is required")
    private String payerName;

    @NotNull(message = "payerAddress is required")
    private String payerAddress;

    @NotNull(message = "payerBirthdate is required")
    private LocalDate payerBirthdate;

    private Integer ownerTypeId = 1;

    public Invoice toEntity() {

        Invoice invoice = new Invoice();

        //invoice.setOwnerTypeAlias(InvoiceOwnerTypeAlias.getInstanceById(this.ownerTypeId));
        invoice.setOwnerTypeAlias(InvoiceOwnerTypeAlias.getInstanceById(1));

        invoice.setOrganName(this.organName);

        invoice.setInvoiceSerial("MAB_" + this.invoiceSerial);

        invoice.setInvoiceId(this.invoiceId);
        invoice.setAmount(this.penaltyPunishmentAmount);

        invoice.setIsDiscount50(this.isDiscount50);
        invoice.setDiscount50Amount(this.discount50Amount);
        invoice.setDiscount50ForDate(this.discount50ForDate);

        invoice.setIsDiscount70(this.isDiscount70);
        invoice.setDiscount70Amount(this.discount70Amount);
        invoice.setDiscount70ForDate(this.discount70ForDate);

        invoice.setInvoiceDate(this.invoiceDate);
        invoice.setBankInn(this.bankInn);
        invoice.setBankName(this.bankName);
        invoice.setBankCode(this.bankCode);
        invoice.setBankAccount(this.bankAccount);

        invoice.setPayerName(this.payerName);
        invoice.setPayerAddress(this.payerAddress);
        invoice.setPayerBirthdate(this.payerBirthdate);

        return invoice;
    }
}
