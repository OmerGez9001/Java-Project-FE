import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;



@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterLog {
    private Long id;

    private String workerId;

    private String registerId;

    private RegisterAction registerAction;
}
