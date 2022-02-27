package com.example.travellerproject.services;

import com.example.travellerproject.exceptions.BadRequestException;
import com.example.travellerproject.exceptions.UnauthorizedException;
import com.example.travellerproject.model.dto.LikeDislikeMessageDTO;
import com.example.travellerproject.model.dto.MessageDTO;
import com.example.travellerproject.model.dto.comment.CommentRequestDTO;
import com.example.travellerproject.model.dto.comment.CommentResponseDTO;
import com.example.travellerproject.model.dto.post.ResponsePostDTO;
import com.example.travellerproject.model.dto.user.OwnerOfPostOrCommentDTO;
import com.example.travellerproject.model.pojo.Comment;
import com.example.travellerproject.model.pojo.Post;
import com.example.travellerproject.model.pojo.User;
import com.example.travellerproject.repositories.CommentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CommentService {


    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private Validator validator;

    @Transactional
    public CommentResponseDTO create(long userId, CommentRequestDTO commentRequestDTO, long postId) {
        Post post = validator.validatePostAndGet(postId);
        User user = validator.validateUserAndGet(userId);
        if(commentRequestDTO.getText()==null||commentRequestDTO.getText().isBlank()){
            throw new BadRequestException("Cannot post empty comment");
        }
        Comment comment = new Comment();
        comment.setText(commentRequestDTO.getText());
        comment.setUser(user);
        comment.setPost(post);
        comment.setCreatedAt(LocalDate.now());
        commentRepository.save(comment);
        return new CommentResponseDTO(comment);
    }

    @Transactional
    public MessageDTO deleteComment(long commentId,long userId) {
        Comment comment = validator.validateCommentAndGet(commentId);
        User user = validator.validateUserAndGet(userId);
        if(!comment.getUser().equals(user) && !user.isAdmin()){
            throw new UnauthorizedException("This comment isn't yours, and you can't delete it.");
        }
        commentRepository.delete(comment);
        return new MessageDTO("Comment was deleted");
    }

    public CommentResponseDTO getById(long commentId) {
        Comment comment = validator.validateCommentAndGet(commentId);
        return new CommentResponseDTO(comment);
    }

    @Transactional
    public CommentResponseDTO editComment(CommentRequestDTO commentRequestDTO,long commentId,long userId) {
        Comment comment = validator.validateCommentAndGet(commentId);
        User user = validator.validateUserAndGet(userId);
        if(comment.getUser().equals(user) || !user.isAdmin()){
            throw new UnauthorizedException("You cannot edit comment that u did not post.");
        }
        modelMapper.map(commentRequestDTO,comment);
        commentRepository.save(comment);

        return new CommentResponseDTO(comment);
    }

    @Transactional
    public LikeDislikeMessageDTO likeComment(long commentId, long userId){
        Comment comment = validator.validateCommentAndGet(commentId);
        User user = validator.validateUserAndGet(userId);
        if (user.getLikedComments().contains(comment)){
            throw new BadRequestException("You have already liked this comment");
        }
        if (user.getDislikedComments().contains(comment)){
            throw new BadRequestException("You have disliked this comment. Please undo it.");
        }
        comment.getCommentLikers().add(user);

        commentRepository.save(comment);
        return new LikeDislikeMessageDTO("You have liked a comment",comment.getCommentLikers().size());
    }

    @Transactional
    public LikeDislikeMessageDTO undoLikeComment(long commentId, long userId){
        Comment comment = validator.validateCommentAndGet(commentId);
        User user = validator.validateUserAndGet(userId);
        if (!user.getLikedComments().contains(comment)){
            throw new BadRequestException("You have to like the comment before removing like");
        }
        comment.getCommentLikers().remove(user);
        commentRepository.save(comment);
        return new LikeDislikeMessageDTO("You have undid your like ",comment.getCommentLikers().size());
    }

    @Transactional
    public LikeDislikeMessageDTO dislikeComment(long commentId, long userId){
        Comment comment = validator.validateCommentAndGet(commentId);
        User user = validator.validateUserAndGet(userId);
        if (user.getDislikedComments().contains(comment)){
            throw new BadRequestException("You have already disliked this comment");
        }
        if (user.getLikedComments().contains(comment)){
            throw new BadRequestException("You have liked this comment.Please undo it.");
        }
        comment.getCommentDislikers().add(user);
        commentRepository.save(comment);
        return new LikeDislikeMessageDTO("You have disliked a comment",comment.getCommentDislikers().size());
    }

    @Transactional
    public LikeDislikeMessageDTO undoDislikeComment(long commentId, long userId){
        Comment comment = validator.validateCommentAndGet(commentId);
        User user = validator.validateUserAndGet(userId);
        if (!user.getDislikedComments().contains(comment)){
            throw new BadRequestException("You have to dislike the comment before removing dislike");
        }
        comment.getCommentDislikers().remove(user);
        commentRepository.save(comment);
        return new LikeDislikeMessageDTO("You have undid your dislike",comment.getCommentDislikers().size());
    }

}
