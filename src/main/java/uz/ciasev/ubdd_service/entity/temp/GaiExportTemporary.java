package uz.ciasev.ubdd_service.entity.temp;
import javax.persistence.*;

@Entity
@Table(name = "gai_export_temporary")
public class GaiExportTemporary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "EX_ID", nullable = false)
    private String exId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExId() {
        return exId;
    }

    public void setExId(String exId) {
        this.exId = exId;
    }

    public GaiExportTemporary(String exId) {
        this.exId = exId;
    }
}
