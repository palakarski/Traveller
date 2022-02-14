package com.example.travellerproject.model.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;

@Component
@Getter
@Setter
@NoArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name="owner_id")
    private User user;
    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column
    private String text;

}
