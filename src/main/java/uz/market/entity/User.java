package uz.market.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.market.entity.enums.Role;

@AllArgsConstructor
@NoArgsConstructor
@Data

public abstract class User {
    private Long userId;
    private String userName;
    private String fullName;
    private String phoneNumber;
    private Role role;
    private Boolean isRegister;
    private Double balance;
}
