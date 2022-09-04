import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data

public class Jwt {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
}
