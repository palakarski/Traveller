package com.example.travellerproject.model.dto.user;

import com.example.travellerproject.model.pojo.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OwnerOfPostDTO {
    private long id;
    private String username;
    private String firstName;
    private String lastName;

    public  OwnerOfPostDTO(User user){
        this.id = user.getId();
        this.username=user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName =  user.getLastName();

    }
}
