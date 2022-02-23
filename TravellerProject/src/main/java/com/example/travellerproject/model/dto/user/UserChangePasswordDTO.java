package com.example.travellerproject.model.dto.user;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserChangePasswordDTO {
    @NotNull
    private String oldpassword;
    @NotNull
    private String newpassword;
    @NotNull
    private String confnewpassword;

}
