package com.example.travellerproject.model.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Component
@Getter
@Setter
@NoArgsConstructor
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User user;
    @Column
    private String description;
    @OneToOne
    @JoinColumn(name = "categorry_id")
    private PostCategory postCategory;
    @Column
    private String latitude;
    @Column
    private String longtitude;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column
    private String title;

}
