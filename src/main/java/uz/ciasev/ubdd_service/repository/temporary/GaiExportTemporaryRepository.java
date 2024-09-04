package uz.ciasev.ubdd_service.repository.temporary;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.ciasev.ubdd_service.entity.temp.GaiExportTemporary;


public interface GaiExportTemporaryRepository extends JpaRepository<GaiExportTemporary, Long>{

    GaiExportTemporary findByExId(String exId);

}
