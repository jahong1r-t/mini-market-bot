package uz.market.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.market.entity.enums.Role;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Buyer extends User {
    private List<Order> orders;

    public Buyer(Long userId, String userName, String fullName, String phoneNumber, Role role, Boolean isRegister, Double balance, List<Order> orders) {
        super(userId, userName, fullName, phoneNumber, role, isRegister, balance);
        this.orders = orders;
    }
}