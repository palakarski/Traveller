package com.example.travellerproject.model.dto.user;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserSignInDTO {

    @NotNull
    private String username;
    @NotNull
    private String password;

}
