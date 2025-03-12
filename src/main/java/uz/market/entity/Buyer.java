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
public class Buyer extends User {
    private String basketId;
    private List<String> orderIds;

    public Buyer(Long userId, String username, String fullName, String phoneNumber, Role role, boolean isRegistered, double balance) {
        super(userId, username, fullName, phoneNumber, role, isRegistered, balance);
        this.basketId = null;
        this.orderIds = new ArrayList<>();
        this.setRole(Role.BUYER);
    }
}