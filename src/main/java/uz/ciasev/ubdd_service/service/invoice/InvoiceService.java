package uz.ciasev.ubdd_service.service.invoice;

import uz.ciasev.ubdd_service.dto.internal.response.adm.InvoiceResponseDTO;
import uz.ciasev.ubdd_service.dto.ubdd.UbddInvoiceRequest;
import uz.ciasev.ubdd_service.entity.invoice.Invoice;
import uz.ciasev.ubdd_service.entity.resolution.decision.Decision;
import uz.ciasev.ubdd_service.entity.resolution.punishment.PenaltyPunishment;
import uz.ciasev.ubdd_service.entity.user.User;

import java.util.Optional;

public interface InvoiceService {

    Invoice create(User user, UbddInvoiceRequest request);

    Invoice findById(Long id);

    Decision getInvoiceDecision(Invoice invoice);

    Optional<Invoice> findByPenalty(PenaltyPunishment penalty);

    Optional<Invoice> findPenaltyInvoiceByDecision(Decision decision);

    Invoice getPenaltyInvoiceByDecision(Decision decision);

    Invoice findByAdmCaseId(Long id);

}
