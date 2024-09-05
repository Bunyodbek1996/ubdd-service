package uz.ciasev.ubdd_service.service.invoice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.ciasev.ubdd_service.mvd_core.api.billing.dto.BillingPayeeInfoDTO;
import uz.ciasev.ubdd_service.mvd_core.api.billing.dto.BillingPayerInfoDTO;
import uz.ciasev.ubdd_service.mvd_core.api.billing.dto.BillingPaymentDTO;
import uz.ciasev.ubdd_service.entity.invoice.Invoice;
import uz.ciasev.ubdd_service.entity.invoice.Payment;
import uz.ciasev.ubdd_service.entity.invoice.PaymentDataProjection;
import uz.ciasev.ubdd_service.repository.invoice.PaymentRepository;
import uz.ciasev.ubdd_service.utils.MoneyFormatter;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public List<Payment> findByInvoiceId(Long invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId);
    }


    @Override
    public Payment save(Invoice invoice, BillingPaymentDTO paymentDTO) {

        Payment payment = new Payment();

        payment.setNumber(String.valueOf(paymentDTO.getId()));
        payment.setPaymentTime(paymentDTO.getPaidAt());

        if (paymentDTO.getBid() == null) {
            payment.setBid("MAB_FAKE_BID_" + System.currentTimeMillis());
        } else {
            payment.setBid(paymentDTO.getBid());
        }

        payment.setBlankDate(paymentDTO.getPaidAt() != null ? paymentDTO.getPaidAt().toLocalDate() : LocalDate.now());
        payment.setBlankNumber(paymentDTO.getDocNumber());

        payment.setAmount(MoneyFormatter.currencyToCoin(paymentDTO.getAmount()));

        BillingPayerInfoDTO payerInfo = paymentDTO.getPayerInfo();

        payment.setFromBankCode(payerInfo.getFromBankCode());
        payment.setFromBankAccount(payerInfo.getFromBankAccount());
        payment.setFromBankName(payerInfo.getFromBankName());
        payment.setFromInn(payerInfo.getFromInn());

        BillingPayeeInfoDTO payeeInfo = paymentDTO.getPayeeInfo();

        payment.setToBankCode(payeeInfo.getToBankCode());
        payment.setToBankAccount(payeeInfo.getToBankAccount());
        payment.setToBankName(payeeInfo.getToBankName());
        payment.setToInn(payeeInfo.getToInn());

        payment.setInvoice(invoice);

        return paymentRepository.saveAndFlush(payment);
    }


    @Override
    public Optional<Payment> findById(Long paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public Optional<Payment> getLastPaymentForInvoices(Collection<Long> invoicesId) {
        return paymentRepository
                .findTopByInvoiceIdInOrderByPaymentTimeDesc(invoicesId);
    }

    @Override
    public List<PaymentDataProjection> findPaymentByInvoices(Collection<Long> invoicesId) {
        return paymentRepository
                .findPaymentDataProjectionByInvoices(invoicesId);
    }

    @Override
    public Optional<Long> getTotalAmountForInvoices(Collection<Long> invoicesId) {
        return paymentRepository.sumAmountByInvoicesId(invoicesId);
    }

    @Override
    public Payment update(Payment payment, BillingPaymentDTO paymentDTO) {
        payment.setAmount(MoneyFormatter.currencyToCoin(paymentDTO.getAmount()));
        payment.setIsUpdated(true);
        return paymentRepository.saveAndFlush(payment);
    }
}
