package com.example.travellerproject.model.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;
@Entity
@Component
@Getter
@Setter
@NoArgsConstructor
@Table(name = "post_videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    @Column(name = "file")
    private String filename;
}
