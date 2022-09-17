import lombok.Data;



@Data
public class RegisterLog {
    private Long id;

    private String workerId;

    private String registerId;

    private RegisterType registerType;

    private RegisterAction registerAction;
}
