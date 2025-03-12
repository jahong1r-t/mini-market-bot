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
public class Seller extends User {
    private List<Shop> shops;

    public Seller(Long userId, String userName, String fullName, String phoneNumber, Role role, Boolean isRegister, Double balance, List<Shop> orders) {
        super(userId, userName, fullName, phoneNumber, role, isRegister, balance);
        this.shops = orders;
    }
}
