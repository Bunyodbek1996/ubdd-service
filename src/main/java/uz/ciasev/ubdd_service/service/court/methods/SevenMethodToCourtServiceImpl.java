package uz.ciasev.ubdd_service.service.court.methods;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uz.ciasev.ubdd_service.mvd_core.api.court.service.token.CourtTokenService;
import uz.ciasev.ubdd_service.mvd_core.api.court.dto.CourtRequestDTO;
import uz.ciasev.ubdd_service.mvd_core.api.court.dto.CourtResponseDTO;
import uz.ciasev.ubdd_service.mvd_core.api.court.dto.seventh.CourtDecisionFileRequestDTO;
import uz.ciasev.ubdd_service.entity.resolution.Resolution;
import uz.ciasev.ubdd_service.entity.user.User;
import uz.ciasev.ubdd_service.exception.*;
import uz.ciasev.ubdd_service.service.resolution.ResolutionService;

@Slf4j
@Service
public class SevenMethodToCourtServiceImpl implements SevenMethodToCourtService {

    private final String courtBaseUrl;
    private final RestTemplate restTemplate;
    private final CourtTokenService courtTokenService;
    private final ResolutionService resolutionService;

    @Autowired
    public SevenMethodToCourtServiceImpl(@Value("${court-api.base-url}")String courtBaseUrl,
                                         @Qualifier("courtRestTemplate") RestTemplate restTemplate,
                                         CourtTokenService courtTokenService,
                                         ResolutionService resolutionService) {
        this.courtBaseUrl = courtBaseUrl;
        this.restTemplate = restTemplate;
        this.courtTokenService = courtTokenService;
        this.resolutionService = resolutionService;
    }

    @Override
    @Transactional
    public void updateDecisionFile(User user, Long id) {
        var resolution = resolutionService.getById(id);

        if (!resolution.getOrgan().isCourt()) {
            throw new ValidationException(ErrorCode.NOT_COURT_RESOLUTION);
        }

        if (resolution.getFileId() == null) {
            throw new ValidationException(ErrorCode.COURT_RESOLUTION_FILE_ID_NOT_PRESENT);
        }

        var headers = courtTokenService.getHeadersForCourt();

        var requestBody = buildDecisionFile(resolution);
        var httpEntity = new HttpEntity<>(requestBody, headers);

        var uriBuilder = UriComponentsBuilder
                .fromHttpUrl(courtBaseUrl + "/adm/api/mvd-case/file/review/")
                .path(String.valueOf(resolution.getFileId()));

        try {
            var responseEntity = restTemplate.exchange(
                            uriBuilder.toUriString(),
                            HttpMethod.POST,
                            httpEntity,
                            CourtResponseDTO.class);

            var response = responseEntity.getBody();

            if (response != null && response.getResult() != null) {
                var result = response.getResult();

                if (!result.isSuccessfully()) {
                    log.error("COURT 7 ERROR : {} FRO RESOLUTION ID {}", result.getResultDescription(), id);
                    throw new CourtDecisionFileException(result.getResultDescription());
                }
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("COURT SERVICE : {}", e.getLocalizedMessage());
            throw new CourtServiceUnavailableException(e.getMessage());
        }
    }

    private CourtRequestDTO<CourtDecisionFileRequestDTO> buildDecisionFile(Resolution resolution) {
        var courtDecisionFile = new CourtDecisionFileRequestDTO();
        courtDecisionFile.setCaseId(resolution.getAdmCaseId());
        courtDecisionFile.setClaimId(resolution.getClaimId());
        courtDecisionFile.setFileId(resolution.getFileId());
        courtDecisionFile.setUri(resolution.getCourtDecisionUri());
        courtDecisionFile.setReview(true);

        var courtRequestDTO = new CourtRequestDTO<CourtDecisionFileRequestDTO>();
        courtRequestDTO.setSendDocumentRequest(courtDecisionFile);

        return courtRequestDTO;
    }
}
