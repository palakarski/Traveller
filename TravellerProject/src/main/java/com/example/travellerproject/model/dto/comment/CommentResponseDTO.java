package com.example.travellerproject.model.dto.comment;

import com.example.travellerproject.model.dto.post.ResponsePostDTO;
import com.example.travellerproject.model.dto.user.OwnerOfPostOrCommentDTO;
import com.example.travellerproject.model.pojo.Comment;
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
        private OwnerOfPostOrCommentDTO ownerOfPostOrCommentDTO;
        private long postId;
        private int likes;
        private int dislikes;

        public CommentResponseDTO(Comment comment) {
                this.id = comment.getId();
                this.text = comment.getText();
                this.createdAt = comment.getCreatedAt();
                this.ownerOfPostOrCommentDTO = new OwnerOfPostOrCommentDTO(comment.getUser());
                this.postId = comment.getPost().getId();
                this.likes = comment.getCommentLikers().size();
                this.dislikes = comment.getCommentDislikers().size();
        }
}
