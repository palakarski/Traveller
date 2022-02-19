package com.example.travellerproject.model.dto.comment;

import com.example.travellerproject.model.dto.post.ResponsePostDTO;
import com.example.travellerproject.model.dto.user.OwnerOfPostDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
public class CommentResponseDTO {
        private long id;
        private String text;
        private LocalDate createdAt;
        private OwnerOfPostDTO ownerOfPostDTO;
        private ResponsePostDTO responsePostDTO;

}
