package uz.market.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
@Builder
public class Product {
    private String id;
    private String name;
    private Double price;
    private String image;
    private String shopId;
    private Integer quantity; // Добавлено поле количества

    public Product(String id, String name, Double price, String image, String shopId, Integer quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.shopId = shopId;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public String getImage() { return image; }
    public String getShopId() { return shopId; }
    public Integer getQuantity() { return quantity; }
}
