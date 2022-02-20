package com.example.travellerproject.model.dto.post;

import com.example.travellerproject.model.dto.user.OwnerOfPostDTO;
import com.example.travellerproject.model.pojo.Image;
import com.example.travellerproject.model.pojo.Post;
import com.example.travellerproject.model.pojo.PostCategory;
import com.example.travellerproject.model.pojo.User;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ResponsePostDTO {

    @NotNull
    private long id;
    @NotNull
    private OwnerOfPostDTO user;

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
    private List<Image> images;
    private int likes;
    //private Set<User> likers;



    public ResponsePostDTO(Post post){
                this.id = post.getId();
                this.user = new OwnerOfPostDTO(post.getUser());
                this.description = post.getDescription();
                this.postCategory = post.getPostCategory();
                this.latitude = post.getLatitude();
                this.longitude = post.getLongitude();
                this.createdAt = post.getCreatedAt();
                this.title = post.getTitle();
                this.images=post.getImages();
                //this.likers = post.getLikers();
            }
}
