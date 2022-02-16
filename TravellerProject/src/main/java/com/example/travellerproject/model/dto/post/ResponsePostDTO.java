package com.example.travellerproject.model.dto.post;

import com.example.travellerproject.model.pojo.PostCategory;
import com.example.travellerproject.model.pojo.User;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class ResponsePostDTO {

    private long id;
    private User user;
    private String description;
    private PostCategory postCategory;
    private String latitude;
    private String longtitude;
    private LocalDateTime createdAt;
    private String title;
}
