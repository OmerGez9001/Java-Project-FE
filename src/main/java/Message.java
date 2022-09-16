import lombok.Data;

@Data
public class Message {
    private String content;
    private Long shopId;
    private String sender;
}