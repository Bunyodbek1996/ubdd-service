package uz.ciasev.ubdd_service.service.main;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import uz.ciasev.ubdd_service.exception.ErrorCode;
import uz.ciasev.ubdd_service.exception.ValidationException;
import uz.ciasev.ubdd_service.mvd_core.api.f1.service.F1Service;
import uz.ciasev.ubdd_service.mvd_core.api.f1.dto.F1Document;
import uz.ciasev.ubdd_service.dto.internal.request.ActorRequest;
import uz.ciasev.ubdd_service.dto.internal.request.PersonDocumentRequestDTO;
import uz.ciasev.ubdd_service.dto.internal.request.PersonRequestDTO;
import uz.ciasev.ubdd_service.entity.Person;
import uz.ciasev.ubdd_service.entity.PersonDocument;
import uz.ciasev.ubdd_service.entity.dict.ExternalSystemAlias;
import uz.ciasev.ubdd_service.service.file.Category;
import uz.ciasev.ubdd_service.service.file.S3FileService;
import uz.ciasev.ubdd_service.service.person.PersonService;

@Service
@RequiredArgsConstructor
public class PersonDataServiceImpl implements PersonDataService {

    private final F1Service f1Service;
    private final S3FileService fileService;
    private final PersonService personService;

    @Override
    public Pair<Person, ? extends PersonDocument> provideByPinppOrManualDocument(ActorRequest actorRequest) {

        Pair<Person, ? extends PersonDocument> rsl;

        if (actorRequest.getPinpp() != null) {
            rsl = provideByPinpp(actorRequest.getPinpp());
            if (actorRequest.getDocument() != null) {
                rsl = Pair.of(rsl.getFirst(), actorRequest.getDocument());
            }
        } else {
            throw new ValidationException(ErrorCode.PINPP_REQUIRED);
        }

        return rsl;
    }

    @Override
    public Pair<Person, F1Document> provideByPinpp(String pinpp) {
        var document = f1Service.findByPinpp(pinpp);
        var dto = personService.buildFromF1(document);
        var person = personService.findOrSave(dto);

        return Pair.of(person, document);
    }

    @Override
    public String getPhotoByPinpp(String pinpp) {
        var document = f1Service.findByPinpp(pinpp);
        return getPhotoByDocument(document);

    }

    private Pair<Person, PersonDocumentRequestDTO> provideByManualDocument(PersonDocumentRequestDTO docDTO, PersonRequestDTO personDTO) {
        var dto = personService.buildManual(docDTO, personDTO);
        var person = personService.findOrSave(dto);

        return Pair.of(person, docDTO);
    }

    @Override
    public String getPhotoByDocument(PersonDocument personDocument) {
        String uri = null;

        if (personDocument.getExternalSystem().filter(ExternalSystemAlias.F1::equals).isPresent()) {
            if (personDocument.getPhotoId() != null) {
                byte[] photo = f1Service.getPhotoById(personDocument.getPhotoId().toString(), personDocument.getPhotoType());
                if (photo != null) {
                    String name = personDocument.getExternalId().orElse("");
                    uri = fileService.save(Category.DOCUMENTS, name + ".jpg", photo);
                }
            }
        }
        return uri;
    }
}
