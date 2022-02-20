package com.example.travellerproject.model.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "post_images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "post_id")
    private Post post;
    @Column(name = "file")
    private String filename;
}