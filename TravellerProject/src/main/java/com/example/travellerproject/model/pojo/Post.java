package com.example.travellerproject.model.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "owner_id")
    private User user;

    @Column
    private String description;

    @ManyToOne()
    @JsonBackReference(value = "post-categoty")
    @JoinColumn(name = "category_id")
    private PostCategory postCategory;

    @Column
    private String latitude;

    @Column
    private String longitude;

    @Column(name = "created_at")

    private LocalDate createdAt;
    @Column

    private String title;

    @OneToMany(mappedBy = "post")
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    @JsonManagedReference
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    @JsonManagedReference
    private List<Video> videos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name ="users_tag_at_posts",
            joinColumns = {@JoinColumn (name = "post_id")},
            inverseJoinColumns = {@JoinColumn(name="user_id")}
    )
    private List<User> userTagAtPosts ;

}
