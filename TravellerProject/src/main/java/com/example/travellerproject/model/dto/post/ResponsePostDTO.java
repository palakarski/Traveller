package com.example.travellerproject.model.dto.post;

import com.example.travellerproject.model.dto.user.OwnerOfPostOrCommentDTO;
import com.example.travellerproject.model.pojo.*;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ResponsePostDTO {

    @NotNull
    private long id;
    @NotNull
    private OwnerOfPostOrCommentDTO user;

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
    private List<Video> video;
    private List<OwnerOfPostOrCommentDTO> tagsWithUsers;
    private int likes;
    private int dislikes;
    //private Set<User> likers;



    public ResponsePostDTO(Post post){
                this.id = post.getId();
                this.user = new OwnerOfPostOrCommentDTO(post.getUser());
                this.description = post.getDescription();
                this.postCategory = post.getPostCategory();
                this.latitude = post.getLatitude();
                this.longitude = post.getLongitude();
                this.createdAt = post.getCreatedAt();
                this.title = post.getTitle();
                this.images=post.getImages();
                this.video =post.getVideos();
                this.tagsWithUsers = new ArrayList<>();
                for (User user : post.getUserTagAtPosts()) {
                    tagsWithUsers.add(new OwnerOfPostOrCommentDTO(user));
                }
                this.likes=post.getLikers().size();
                this.dislikes=post.getDislikers().size();
            }
}
