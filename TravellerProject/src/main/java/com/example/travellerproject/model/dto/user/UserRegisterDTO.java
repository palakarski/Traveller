package com.example.travellerproject.model.dto.user;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@Component
public class UserRegisterDTO {
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String username;
    @NotNull
    private LocalDate birthDate;
    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String confpassword;
    @NotNull
    private LocalDateTime createdAt;
    @NotNull
    private boolean isAdmin;
    private char gender;

}
