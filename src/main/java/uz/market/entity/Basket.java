package uz.market.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Basket {
    private String id;
    private Long buyerId;
    private Map<String, Integer> productQuantities;
}
