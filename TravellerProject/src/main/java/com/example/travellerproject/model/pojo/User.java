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
import java.util.*;

@Entity
@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column
    private String username;

    @Column(name = "date_of_birth")
    private LocalDate birthDate;

    @Column
    private String email;

    @Column
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_admin")
    private boolean isAdmin;

    @Column
    private char gender;

    @OneToMany(mappedBy = "user",cascade = { CascadeType.ALL })
    @JsonManagedReference
    private List<Post> posts ;

    @ManyToMany(mappedBy = "followedUsers",cascade = { CascadeType.ALL })
    private List<User> followers;

    @ManyToMany()
    @JoinTable(
            name ="subscribers",
            joinColumns = {@JoinColumn (name = "subscriber_id")},
            inverseJoinColumns = {@JoinColumn(name="subscribed_for_user_id")}
    )
    private List<User> followedUsers ;

    @OneToMany(mappedBy = "user",cascade = { CascadeType.ALL })
    @JsonBackReference
    private List<Comment> comments ;

    @ManyToMany(mappedBy ="userTagAtPosts",cascade = { CascadeType.ALL } )
    private List<Post> tagsAtPosts;

    @ManyToMany(mappedBy = "likers",cascade = { CascadeType.ALL })
    private Set<Post> likedPosts;

    @ManyToMany(mappedBy = "dislikers",cascade = { CascadeType.ALL })
    private Set<Post> dislikedPosts;

    @ManyToMany(mappedBy = "commentLikers",cascade = { CascadeType.ALL })
    private Set<Comment> likedComments;

    @ManyToMany(mappedBy = "commentDislikers",cascade = { CascadeType.ALL })
    private Set<Comment> dislikedComments;

}
