package uz.market.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Order {
    private String orderId;
    private String buyerId;
    private String shopId;
    private Map<String, Integer> productQuantities; // productId -> quantity
    private double totalAmount;
    private LocalDateTime timestamp;
}
