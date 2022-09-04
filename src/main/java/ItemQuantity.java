import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemQuantity {
    private Item item;

    private Shop shop;

    private long quantity;
}
