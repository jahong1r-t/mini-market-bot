package uz.market.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Product {
    private String id;
    private String name;
    private double price;
    private String productImg;
    private String shopId;
    private int quantity;
}
