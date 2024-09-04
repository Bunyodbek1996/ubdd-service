package uz.ciasev.ubdd_service.entity.temp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "gai_export_temporary")
@NoArgsConstructor
public class GaiExportTemporary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "EX_ID", nullable = false)
    private String exId;

    @Column(name = "is_success")
    private Boolean isSuccess;

    @Column(name = "error", columnDefinition = "TEXT")
    private String error;

    public GaiExportTemporary(String exId) {
        this.exId = exId;
    }

    public void attachResult(boolean isSuccess, String error) {
        this.isSuccess = isSuccess;
        this.error = error;
    }
}
