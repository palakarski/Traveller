package com.example.travellerproject.model.dto.user;

import com.example.travellerproject.model.dto.post.ResponsePostDTO;
import com.example.travellerproject.model.dto.post.ResponseWithoutOwnerDTO;
import com.example.travellerproject.model.pojo.Post;
import com.example.travellerproject.model.pojo.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Component
public class UserWithOutPassDTO {
    @NonNull
    private long id;
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
    private LocalDateTime createdAt;
    private char gender;
    private boolean isAdmin;
    @JsonManagedReference
    List<ResponseWithoutOwnerDTO> posts = new ArrayList<>();
    
            public UserWithOutPassDTO(User user){
                this.id = user.getId();
                this.firstName = user.getFirstName();
                this.lastName = user.getLastName();
                this.username = user.getUsername();
                this.birthDate = user.getBirthDate();
                this.email = user.getEmail();
                this.createdAt= user.getCreatedAt();
                this.gender = user.getGender();
                for (Post p : user.getPosts()) {
                    posts.add(new ResponseWithoutOwnerDTO(p));
                }
                this.isAdmin = user.isAdmin();
            }
}
