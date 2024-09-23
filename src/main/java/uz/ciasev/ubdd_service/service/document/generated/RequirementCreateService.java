package uz.ciasev.ubdd_service.service.document.generated;

import uz.ciasev.ubdd_service.dto.internal.ProtocolGroupByPersonDTO;
import uz.ciasev.ubdd_service.dto.internal.RequirementPrintDTO;
import uz.ciasev.ubdd_service.dto.internal.ViolatorRequirementDTO;
import uz.ciasev.ubdd_service.dto.internal.WantedVehicleDTO;
import uz.ciasev.ubdd_service.dto.internal.ubdd_data.UbddProtocolRequirementDTO;
import uz.ciasev.ubdd_service.entity.user.User;
import uz.ciasev.ubdd_service.service.pdf.PdfFile;

import java.util.List;

public interface RequirementCreateService {

    PdfFile create(User user, RequirementPrintDTO requestDTO);


    ViolatorRequirementDTO groupProtocolsByViolator(String pinpp);

    List<UbddProtocolRequirementDTO> groupUbddProtocolsByViolator(String pinpp);

    List<UbddProtocolRequirementDTO> groupUbddProtocolsByVehicle(String number);

    List<WantedVehicleDTO> groupWantedVehicleBy(String number);
}
