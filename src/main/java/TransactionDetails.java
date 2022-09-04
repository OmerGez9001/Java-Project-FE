import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TransactionDetails {
    private String customerId;
    private Long shopId;
    private List<ItemTransactionRequest> items;

}