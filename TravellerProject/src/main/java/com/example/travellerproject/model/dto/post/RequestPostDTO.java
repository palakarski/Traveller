package com.example.travellerproject.model.dto.post;

import com.example.travellerproject.model.pojo.PostCategory;
import com.example.travellerproject.model.pojo.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RequestPostDTO {

    private String description;
    @NotNull
    private long postCategory;
    @NotNull
    private String latitude;
    @NotNull
    private String longitude;
    @NotNull
    private LocalDate createdAt;
    @NotNull
    private String title;
}
