package com.example.travellerproject.controllers;
import com.example.travellerproject.model.dto.LikeDislikeMessageDTO;
import com.example.travellerproject.model.dto.MessageDTO;
import com.example.travellerproject.model.dto.comment.CommentResponseDTO;
import com.example.travellerproject.model.dto.post.RequestPostDTO;
import com.example.travellerproject.model.dto.post.ResponsePostDTO;
import com.example.travellerproject.model.dto.user.OwnerOfPostOrCommentDTO;
import com.example.travellerproject.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.List;


@RestController
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private SessionValidator sessionValidator;


    @PostMapping(value = "posts/create")
    public ResponseEntity<ResponsePostDTO> createPost(@RequestBody RequestPostDTO requestPostDTO, HttpSession session) {
        long userId = sessionValidator.isUserLoggedIn(session);
        return ResponseEntity.ok(postService.createPost(requestPostDTO, userId));
    }

    @DeleteMapping(value = "/posts/delete/{postId}")
    public MessageDTO deletePost(@PathVariable long postId, HttpSession session) {
        long userId =  sessionValidator.isUserLoggedIn(session);
        return postService.deletePost(postId,userId);
    }

    @GetMapping(value = "/posts/{id}")
    public ResponseEntity<ResponsePostDTO> getPostById(@PathVariable long id, HttpSession session) {
        sessionValidator.isUserLogged(session);
        return ResponseEntity.ok(postService.getById(id));
    }

    @PutMapping(value = "/posts/{id}/edit")
    public ResponseEntity<ResponsePostDTO> editPost(@RequestBody RequestPostDTO requestPostDTO, @PathVariable long id, HttpSession session) {
        long userId = sessionValidator.isUserLoggedIn(session);
        return ResponseEntity.ok(postService.editPost(requestPostDTO, id, userId));
    }


    @PostMapping(value = "/posts/{pId}/tag/{tagedUId}")
    public MessageDTO tagUser(@PathVariable long pId, @PathVariable long tagedUId, HttpSession session) {
        long userId = sessionValidator.isUserLoggedIn(session);
        return postService.tagUser(userId, tagedUId, pId);
    }

    @PostMapping(value = "/posts/{pId}/untag/{id}")
    public MessageDTO unTagUser(@PathVariable long pId, @PathVariable long id, HttpSession session) {
        long userId = sessionValidator.isUserLoggedIn(session);
        return postService.unTagUser(userId, id, pId);
    }

    @GetMapping(value = "/posts/{pId}/tags")
    public List<OwnerOfPostOrCommentDTO> getAllTagedUsers(@PathVariable long pId, HttpSession session) {
        long userId = sessionValidator.isUserLoggedIn(session);
        return postService.getAllTaggedUsers(userId, pId);
    }


    @GetMapping(value = "posts/search/{username}")
    public List<ResponsePostDTO> findAllPostOfUser(@PathVariable String username, HttpSession session) {
        sessionValidator.isUserLogged(session);
        return postService.findPostsByUsername(username);
    }

    @GetMapping(value = "/posts/{postId}/comments")
    public Page<CommentResponseDTO> getAllComments(Pageable page, @PathVariable long postId, HttpSession session){
        sessionValidator.isUserLogged(session);
        return postService.getAllCommentsByPost(page,postId);

    }

    @PostMapping(value = "/posts/{id}/like")
    public LikeDislikeMessageDTO likePost(@PathVariable long id, HttpSession session) {
        long userId = sessionValidator.isUserLoggedIn(session);
        return postService.likePost(id, userId);
    }

    @PostMapping(value = "/posts/{id}/undoLike")
    public LikeDislikeMessageDTO undoLikePost(@PathVariable long id, HttpSession session) {
        long userId = sessionValidator.isUserLoggedIn(session);
        return postService.undoLikePost(id, userId);
    }

    @PostMapping(value = "/posts/{id}/dislike")
    public LikeDislikeMessageDTO dislikePost(@PathVariable long id, HttpSession session) {
        long userId = sessionValidator.isUserLoggedIn(session);
        return postService.dislikePost(id, userId);
    }

    @PostMapping(value = "/posts/{id}/undoDislike")
    public LikeDislikeMessageDTO undoDislikePost(@PathVariable long id, HttpSession session) {
        long userId = sessionValidator.isUserLoggedIn(session);
        return postService.undoDislikePost(id, userId);
    }

    @GetMapping(value = "/posts/allForeignPosts")
    public Page<ResponsePostDTO> getForeignPosts(Pageable page,HttpSession session) {
        long userId = sessionValidator.isUserLoggedIn(session);
        return postService.getForeignPosts(page,userId);
    }

    @GetMapping(value = "/posts/allForeignPosts/{filterName}")
    public Page<ResponsePostDTO> getForeignPostsFiltered(Pageable page,@PathVariable String filterName, HttpSession session) {
        long userId = sessionValidator.isUserLoggedIn(session);
        return postService.getForeignPostsFiltered(page,userId, filterName);
    }

    @GetMapping(value = "/posts/newsfeed")
    public Page<ResponsePostDTO> getNewsfeed (Pageable page ,HttpSession session){
        long userId = sessionValidator.isUserLoggedIn(session);
        return postService.getNewsfeed(page,userId);
    }

    @GetMapping(value = "/posts/newsfeed/{filterName}")
    public Page<ResponsePostDTO> getNewsfeedFiltered(Pageable page, @PathVariable String filterName, HttpSession session){
        long userId = sessionValidator.isUserLoggedIn(session);
        return postService.getNewsfeedFiltered(page,userId, filterName);
    }
}

