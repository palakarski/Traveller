package com.example.travellerproject.model.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
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
    @JsonBackReference
    @JoinColumn(name="owner_id")
    private User user;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="post_id")
    private Post post;
    @Column(name = "created_at")
    private LocalDate createdAt;
    @Column
    private String text;

}
