package uz.ciasev.ubdd_service.service.mib;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uz.ciasev.ubdd_service.entity.mib.MibCardMovement;
import uz.ciasev.ubdd_service.entity.mib.MibExecutionCard;
import uz.ciasev.ubdd_service.entity.resolution.decision.Decision;
import uz.ciasev.ubdd_service.entity.status.AdmStatusAlias;
import uz.ciasev.ubdd_service.entity.user.User;
import uz.ciasev.ubdd_service.mvd_core.api.mib.api.types.MibResult;
import uz.ciasev.ubdd_service.repository.mib.MibExecutionCardRepository;
import uz.ciasev.ubdd_service.service.resolution.decision.DecisionActionService;


@Service
@RequiredArgsConstructor
@Slf4j
public class SendToMibServiceImpl implements SendToMibService {

    private final DecisionActionService decisionService;
    private final MibExecutionCardRepository cardRepository;
    private final MibCardMovementService cardMovementService;


    @Override
    public void doSend(MibExecutionCard card, @Nullable User user, MibResult mibResult) {

        cardRepository.save(card);

        sendExecutionCard(user, card, mibResult);

        Decision decision = card.getDecision();
        decisionService.saveStatus(decision, AdmStatusAlias.SEND_TO_MIB);

    }


    private Pair<MibCardMovement, MibResult> sendExecutionCard(User user, MibExecutionCard card, MibResult mibResponse) {
        MibCardMovement move = new MibCardMovement();
        move.setSendTime(mibResponse.getSendTime());
        move.setMibRequestId(mibResponse.getRequestId());
        move.setSendStatusId(1L);
        move.setSendMessage(mibResponse.getMessage());

        cardMovementService.create(user, card, move);

        return Pair.of(move, mibResponse);
    }
}