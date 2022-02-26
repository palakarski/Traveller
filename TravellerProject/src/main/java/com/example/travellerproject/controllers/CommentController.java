package com.example.travellerproject.controllers;
import com.example.travellerproject.model.dto.LikeDislikeMessageDTO;
import com.example.travellerproject.model.dto.MessageDTO;
import com.example.travellerproject.model.dto.comment.CommentRequestDTO;
import com.example.travellerproject.model.dto.comment.CommentResponseDTO;
import com.example.travellerproject.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

@RestController
public class CommentController {


    @Autowired
    private CommentService commentService;
    @Autowired
    private SessionValidator sessionValidator;


    @PostMapping(value = "/comments/create/post/{id}")
    public ResponseEntity<CommentResponseDTO> create(@RequestBody CommentRequestDTO commentRequestDTO, HttpSession session, @PathVariable long id){
    long userId = sessionValidator.isUserLoggedIn(session);
    return ResponseEntity.ok(commentService.create(userId,commentRequestDTO,id));
    }

    @DeleteMapping(value = "/comments/delete/{id}")
    public MessageDTO deleteComment(@PathVariable long id, HttpSession session){
        long userId = sessionValidator.isUserLoggedIn(session);
        return commentService.deleteComment(id,userId);
    }
    @GetMapping (value = "/comments/{id}")
    public ResponseEntity<CommentResponseDTO> getCommentById(@PathVariable long id, HttpSession session){
        sessionValidator.isUserLogged(session);
        return ResponseEntity.ok(commentService.getById(id));
    }
    @PutMapping(value = "/comments/edit/{id}")
    public ResponseEntity<CommentResponseDTO> editComment(@RequestBody CommentRequestDTO commentRequestDTO,@PathVariable long id, HttpSession session){
        long userId=sessionValidator.isUserLoggedIn(session);
        return ResponseEntity.ok(commentService.editComment(commentRequestDTO,id,userId));
    }

    @PostMapping(value = "/comments/{id}/like")
    public LikeDislikeMessageDTO likePost(@PathVariable long id, HttpSession session){
        long userId = sessionValidator.isUserLoggedIn(session);
        return commentService.likeComment(id,userId);
    }

    @PostMapping(value = "/comments/{id}/undolike")
    public LikeDislikeMessageDTO undoLikePost(@PathVariable long id, HttpSession session){
        long userId = sessionValidator.isUserLoggedIn(session);
        return commentService.undoLikeComment(id,userId);
    }

    @PostMapping(value = "/comments/{id}/dislike")
    public LikeDislikeMessageDTO dislikeComment(@PathVariable long id, HttpSession session){
        long userId = sessionValidator.isUserLoggedIn(session);
        return commentService.dislikeComment(id,userId);
    }

    @PostMapping(value = "/comments/{id}/undoDislike")
    public LikeDislikeMessageDTO undoDislikeComment(@PathVariable long id, HttpSession session){
        long userId = sessionValidator.isUserLoggedIn(session);
        return commentService.undoDislikeComment(id,userId);
    }
}
