package uz.ciasev.ubdd_service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.ciasev.ubdd_service.entity.protocol.Protocol;
import uz.ciasev.ubdd_service.entity.resolution.Resolution;
import uz.ciasev.ubdd_service.entity.resolution.decision.Decision;
import uz.ciasev.ubdd_service.event.subscribers.AdmEventSubscriber;
import uz.ciasev.ubdd_service.exception.event.AdmEventUnexpectedDataTypeError;
import uz.ciasev.ubdd_service.service.kafka.WebhookEventMessage;
import uz.ciasev.ubdd_service.service.kafka.WebhookKafkaProducer;
import uz.ciasev.ubdd_service.service.main.resolution.dto.CreatedResolutionDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdmEventServiceImpl implements AdmEventService {

    private final List<AdmEventSubscriber> subscribers;
    private final WebhookKafkaProducer webhookKafkaProducer;

    @Override
    public void fireEvent(AdmEventType type, Object data) {

        if (AdmEventType.PROTOCOL_CREATE.equals(type)) {

            if (data instanceof Protocol) {
                WebhookEventMessage webhookEventMessage = WebhookEventMessage.builder()
                        .eventType(String.valueOf(type))
                        .protocolId(((Protocol) data).getId())
                        .build();
                webhookKafkaProducer.sendEvent(webhookEventMessage);
            } else {
                throw new AdmEventUnexpectedDataTypeError(AdmEventType.PROTOCOL_CREATE, Protocol.class, data);
            }

        } else if (AdmEventType.ORGAN_RESOLUTION_CREATE.equals(type) || AdmEventType.COURT_RESOLUTION_CREATE.equals(type)) {

            if (data instanceof CreatedResolutionDTO) {
                WebhookEventMessage webhookEventMessage = WebhookEventMessage.builder()
                        .eventType(String.valueOf(type))
                        .admCaseId(((CreatedResolutionDTO) data).getResolution().getAdmCase().getId())
                        .build();
                webhookKafkaProducer.sendEvent(webhookEventMessage);
            } else {
                throw new AdmEventUnexpectedDataTypeError(AdmEventType.COURT_RESOLUTION_CREATE, CreatedResolutionDTO.class, data);
            }

        } else if (AdmEventType.DECISION_STATUS_CHANGE.equals(type)) {

            WebhookEventMessage eventMessage = WebhookEventMessage.builder()
                    .eventType(String.valueOf(type))
                    .decisionId(((Decision) data).getId())
                    .build();
            webhookKafkaProducer.sendEvent(eventMessage);

        } else if (AdmEventType.DECISIONS_CANCEL.equals(type)) {

            if (!(data instanceof List)) {
                throw new AdmEventUnexpectedDataTypeError(AdmEventType.DECISIONS_CANCEL, List.class, data);
            }
            try {
                ((List<Decision>) data).stream()
                        .findFirst()
                        .map(Decision::getResolution)
                        .map(Resolution::getAdmCase).ifPresent(admCase -> {
                            WebhookEventMessage webhookEventMessage = WebhookEventMessage.builder()
                                    .eventType(String.valueOf(AdmEventType.DECISIONS_CANCEL))
                                    .admCaseId(admCase.getId())
                                    .build();
                            webhookKafkaProducer.sendEvent(webhookEventMessage);
                        });
            } catch (Exception e) {
                throw new AdmEventUnexpectedDataTypeError(AdmEventType.DECISIONS_CANCEL, e);
            }

        }


        new Thread(() -> {
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            subscribers.parallelStream().filter(s -> s.canAccept(type, data)).forEach(s -> s.accept(data));
        }).start();
    }
}
