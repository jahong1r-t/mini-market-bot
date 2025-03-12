package uz.market.entity;

import lombok.*;
import uz.market.entity.enums.Role;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Seller extends User {
    private List<String> shopIds;
    private List<String> orderIds;

    public Seller(Long userId, String username, String fullName, String phoneNumber, Role role, boolean isRegistered, double balance) {
        super(userId, username, fullName, phoneNumber, role, isRegistered, balance);
        this.shopIds = new ArrayList<>();
        this.orderIds = new ArrayList<>();
        this.setRole(Role.SELLER);
    }
}
