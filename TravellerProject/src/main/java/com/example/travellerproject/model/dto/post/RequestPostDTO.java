package com.example.travellerproject.model.dto.post;

import com.example.travellerproject.model.pojo.PostCategory;
import com.example.travellerproject.model.pojo.User;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RequestPostDTO {
    @NotNull
    private User user;
    private String description;
    @NotNull
    private PostCategory postCategory;
    @NotNull
    private String latitude;
    @NotNull
    private String longtitude;
    @NotNull
    private LocalDateTime createdAt;
    @NotNull
    private String title;
}
