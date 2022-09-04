import lombok.Data;

@Data
public class Worker {
    private String username;
    private String password;
    private String workerId;
    private String id;
    private String fullName;
    private String phoneNumber;
    private String accountNumber;
    private Job job;

    private Shop shop;
}
