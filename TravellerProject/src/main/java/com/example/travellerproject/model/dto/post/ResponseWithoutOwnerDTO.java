package com.example.travellerproject.model.dto.post;

import com.example.travellerproject.model.pojo.Post;
import com.example.travellerproject.model.pojo.PostCategory;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor

public class ResponseWithoutOwnerDTO {
    @NotNull
    private long id;

    private String description;
    @NotNull
    private PostCategory postCategory;
    @NotNull
    private String latitude;
    @NotNull
    private String longitude;
    @NotNull
    private LocalDate createdAt;
    @NotNull
    private String title;
    private int likes;
    private int dislikes;


    public ResponseWithoutOwnerDTO(Post post){
        this.id = post.getId();
        this.description = post.getDescription();
        this.postCategory = post.getPostCategory();
        this.latitude = post.getLatitude();
        this.longitude = post.getLongitude();
        this.createdAt = post.getCreatedAt();
        this.title = post.getTitle();
        this.likes=post.getLikers().size();
        this.dislikes=post.getDislikers().size();
    }
}
