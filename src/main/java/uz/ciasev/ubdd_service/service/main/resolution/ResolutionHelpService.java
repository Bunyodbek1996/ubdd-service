package uz.ciasev.ubdd_service.service.main.resolution;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import uz.ciasev.ubdd_service.dto.internal.request.resolution.CompensationRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.resolution.DecisionRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.resolution.ResolutionRequestDTO;
import uz.ciasev.ubdd_service.entity.Inspector;
import uz.ciasev.ubdd_service.entity.Place;
import uz.ciasev.ubdd_service.entity.admcase.AdmCase;
import uz.ciasev.ubdd_service.entity.dict.VictimTypeAlias;
import uz.ciasev.ubdd_service.entity.resolution.*;
import uz.ciasev.ubdd_service.entity.resolution.compensation.Compensation;
import uz.ciasev.ubdd_service.entity.resolution.decision.Decision;
import uz.ciasev.ubdd_service.entity.resolution.evidence_decision.EvidenceDecision;
import uz.ciasev.ubdd_service.entity.resolution.punishment.PenaltyPunishment;
import uz.ciasev.ubdd_service.entity.resolution.punishment.Punishment;
import uz.ciasev.ubdd_service.entity.settings.BankAccount;
import uz.ciasev.ubdd_service.entity.settings.OrganAccountSettings;
import uz.ciasev.ubdd_service.entity.violator.Violator;
import uz.ciasev.ubdd_service.service.admcase.AdmCaseService;
import uz.ciasev.ubdd_service.service.execution.ExecutionCallbackService;
import uz.ciasev.ubdd_service.service.generator.DecisionNumberGeneratorService;
import uz.ciasev.ubdd_service.service.generator.ResolutionNumberGeneratorService;
import uz.ciasev.ubdd_service.service.main.resolution.dto.CreatedDecisionDTO;
import uz.ciasev.ubdd_service.service.main.resolution.dto.CreatedResolutionDTO;
import uz.ciasev.ubdd_service.service.main.resolution.dto.CreatedSingleResolutionDTO;
import uz.ciasev.ubdd_service.service.resolution.*;
import uz.ciasev.ubdd_service.service.resolution.compensation.CompensationService;
import uz.ciasev.ubdd_service.service.resolution.decision.DecisionService;
import uz.ciasev.ubdd_service.service.resolution.evidence_decision.EvidenceDecisionCreateRequest;
import uz.ciasev.ubdd_service.service.resolution.evidence_decision.EvidenceDecisionService;
import uz.ciasev.ubdd_service.service.resolution.punishment.PunishmentService;
import uz.ciasev.ubdd_service.service.status.AdmCaseStatusService;
import uz.ciasev.ubdd_service.service.victim.VictimService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static uz.ciasev.ubdd_service.entity.settings.BankAccount.EMPTY_ACCOUNT;
import static uz.ciasev.ubdd_service.entity.status.AdmStatusAlias.DECISION_MADE;

@Service
@RequiredArgsConstructor
public class ResolutionHelpService {

    private final VictimService victimService;
    private final AdmCaseService admCaseService;
    private final DecisionService decisionService;
    private final ExecutionCallbackService executionService;
    private final ResolutionService resolutionService;
    private final PunishmentService punishmentService;
    private final AdmCaseStatusService statusService;


    public ResolutionCreateRequest buildResolution(ResolutionRequestDTO requestDTO) {
        return requestDTO.buildResolution();
    }

    public Decision buildDecision(Violator violator, DecisionRequestDTO requestDTO, Supplier<OrganAccountSettings> getAccountSettings) {
        Decision decision = requestDTO.buildDecision();
        decision.setViolator(violator);

        Optional<PenaltyPunishment> penaltyOpt = decision.getPenalty();
        penaltyOpt.ifPresent(penaltyPunishment -> penaltyPunishment.setAccount(EMPTY_ACCOUNT));

        decision.getPunishments()
                .stream()
                .map(Punishment::getLicenseRevocation)
                .filter(Objects::nonNull)
                .forEach(licenseRevocation -> {
                    LocalDate mayBeReturnedAfter = decision.getExecutionFromDate()
                            .plusDays(licenseRevocation.getDays())
                            .plusMonths(licenseRevocation.getMonths())
                            .plusYears(licenseRevocation.getYears());

                    licenseRevocation.setMayBeReturnedAfterDate(mayBeReturnedAfter);
                });


        return decision;
    }

    public Compensation buildNotGovCompensation(CompensationRequestDTO compensationRequestDTO) {
        Compensation compensation = compensationRequestDTO.buildCompensation();
        compensation.setVictim(Optional.ofNullable(compensationRequestDTO.getVictimId()).map(victimService::findById).orElse(null));
        compensation.setAccount(EMPTY_ACCOUNT);

        return compensation;
    }

    public Compensation buildCompensation(CompensationRequestDTO compensationRequestDTO, Supplier<OrganAccountSettings> getAccountSettings) {
        if (compensationRequestDTO.getVictimType().getAlias().equals(VictimTypeAlias.GOVERNMENT)) {
            return buildGovCompensation(
                    compensationRequestDTO,
                    getAccountSettings
            );
        } else {
            return buildNotGovCompensation(compensationRequestDTO);
        }
    }

    public Compensation buildGovCompensation(CompensationRequestDTO compensationRequestDTO, Supplier<OrganAccountSettings> getAccountSettings) {
        Compensation compensation = compensationRequestDTO.buildCompensation();
        compensation.setAccount(EMPTY_ACCOUNT);

        return compensation;
    }

    public Decision saveDecision(Resolution resolution, DecisionNumberGeneratorService decisionNumberGeneratorService, Decision decision) {
        Decision savedDecision = decisionService.create(resolution, decisionNumberGeneratorService, decision);
        for (Punishment punishment : decision.getPunishments()) {
            punishmentService.create(savedDecision, punishment);
        }

        return savedDecision;
    }

    public Optional<Compensation> getGovCompensation(List<Compensation> compensations) {
        return compensations
                .stream()
                .filter(c -> c.getVictimType().is(VictimTypeAlias.GOVERNMENT))
                .findFirst();
    }

    public CreatedSingleResolutionDTO resolve(
            AdmCase admCase,
            Inspector inspector,
            Place resolutionPlace,
            ResolutionNumberGeneratorService resolutionNumberGeneratorService,
            DecisionNumberGeneratorService decisionNumberGeneratorService,
            ResolutionCreateRequest resolution,
            Decision decision
    ) {
        CreatedResolutionDTO data = resolve(
                admCase,
                inspector,
                resolutionPlace,
                resolutionNumberGeneratorService,
                decisionNumberGeneratorService,
                resolution,
                List.of(Pair.of(decision, List.of())),
                List.of()
        );

        return new CreatedSingleResolutionDTO(
                data.getResolution(),
                data.getCreatedDecisions()
        );
    }

    public CreatedResolutionDTO resolve(
            AdmCase admCase,
            Inspector inspector,
            Place resolutionPlace,
            ResolutionNumberGeneratorService resolutionNumberGeneratorService,
            DecisionNumberGeneratorService decisionNumberGeneratorService,
            ResolutionCreateRequest resolution,
            List<Pair<Decision, List<Compensation>>> decisions,
            List<EvidenceDecisionCreateRequest> evidenceDecisionRequests
    ) {
        makeCaseResolved(admCase);

        Resolution savedResolution = resolutionService.create(inspector, resolutionPlace, resolutionNumberGeneratorService, admCase, resolution);

        List<CreatedDecisionDTO> savedDecisions = decisions.stream().map(decisionListPair -> {
            Decision decision = decisionListPair.getFirst();

            Decision savedDecision = saveDecision(savedResolution, decisionNumberGeneratorService, decision);

            return new CreatedDecisionDTO(savedDecision, null);
        }).collect(Collectors.toList());

        CreatedResolutionDTO data = new CreatedResolutionDTO(
                savedResolution,
                savedDecisions,
                List.of()
        );

        executionService.autoExecute(savedResolution);

        return data;
    }

    private void makeCaseResolved(AdmCase admCase) {
        statusService.setStatus(admCase, DECISION_MADE);
        admCase.setConsideredTime(LocalDateTime.now());
        admCaseService.update(admCase.getId(), admCase);
    }

}
