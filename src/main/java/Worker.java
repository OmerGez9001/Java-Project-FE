import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Worker {
    private String workerId;
    private String id;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String accountNumber;
    private Job job;

    private Shop shop;
}
