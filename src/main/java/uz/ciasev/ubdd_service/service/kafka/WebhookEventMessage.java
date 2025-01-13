package uz.ciasev.ubdd_service.service.kafka;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WebhookEventMessage {

    private String eventType;
    private Long protocolId;
    private Long admCaseId;
    private Long decisionId;
    private List<Long> decisionIds;

}
