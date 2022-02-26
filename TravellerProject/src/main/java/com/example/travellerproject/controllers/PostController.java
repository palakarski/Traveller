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


    @PostMapping(value = "post/create")
    public ResponseEntity<ResponsePostDTO> createPost(@RequestBody RequestPostDTO requestPostDTO, HttpSession session) {
        long userId = sessionValidator.isUserLogedIn(session);
        return ResponseEntity.ok(postService.createPost(requestPostDTO, userId));
    }

    @DeleteMapping(value = "delete/{postId}")
    public MessageDTO deletePost(@PathVariable long postId, HttpSession session) {
        long userId =  sessionValidator.isUserLogedIn(session);
        return postService.deletePost(postId,userId);
    }

    @GetMapping(value = "/post/{id}")
    public ResponseEntity<ResponsePostDTO> getPostById(@PathVariable long id, HttpSession session) {
        sessionValidator.isUserLoged(session);
        return ResponseEntity.ok(postService.getById(id));
    }

    @PutMapping(value = "/post/{id}/edit")
    public ResponseEntity<ResponsePostDTO> editPost(@RequestBody RequestPostDTO requestPostDTO, @PathVariable long id, HttpSession session) {
        long userId = sessionValidator.isUserLogedIn(session);
        return ResponseEntity.ok(postService.editPost(requestPostDTO, id, userId));
    }


    @PostMapping(value = "/post/{pId}/tag/{tagedUId}")
    public MessageDTO tagUser(@PathVariable long pId, @PathVariable long tagedUId, HttpSession session) {
        long userId = sessionValidator.isUserLogedIn(session);
        return postService.tagUser(userId, tagedUId, pId);
    }

    @PostMapping(value = "/post/{pId}/untag/{id}")
    public MessageDTO unTagUser(@PathVariable long pId, @PathVariable long id, HttpSession session) {
        long userId = sessionValidator.isUserLogedIn(session);
        return postService.unTagUser(userId, id, pId);
    }

    @GetMapping(value = "/post/{pId}/tags")
    public List<OwnerOfPostOrCommentDTO> getAllTagedUsers(@PathVariable long pId, HttpSession session) {
        long userId = sessionValidator.isUserLogedIn(session);
        return postService.getAllTagedUsers(userId, pId);
    }

    @GetMapping(value = "post/search/{username}")
    public List<ResponsePostDTO> findAllPostOfUser(@PathVariable String username, HttpSession session) {
        sessionValidator.isUserLoged(session);
        return postService.findPostsByUsername(username);
    }

//    @GetMapping(value = "post/comments/{postId}")
//    public List<CommentResponseDTO> showAllCommentsByPost(@PathVariable long postId, HttpSession session) {
//        sessionValidator.isUserLoged(session);
//        return postService.findCommentsByPosts(postId);
//    }
    //TODO
    @GetMapping(value = "/post/{postId}/comments")
    public Page<CommentResponseDTO> getAllcomments(Pageable page,@PathVariable long postId,HttpSession session){
        sessionValidator.isUserLoged(session);
        return postService.getAllCommnetsByPost(page,postId);
    }

    @PostMapping(value = "/posts/{id}/like")
    public LikeDislikeMessageDTO likePost(@PathVariable long id, HttpSession session) {
        long userId = sessionValidator.isUserLogedIn(session);
        return postService.likePost(id, userId);
    }

    @PostMapping(value = "/posts/{id}/undoLike")
    public LikeDislikeMessageDTO undoLikePost(@PathVariable long id, HttpSession session) {
        long userId = sessionValidator.isUserLogedIn(session);
        return postService.undoLikePost(id, userId);
    }

    @PostMapping(value = "/posts/{id}/dislike")
    public LikeDislikeMessageDTO dislikePost(@PathVariable long id, HttpSession session) {
        long userId = sessionValidator.isUserLogedIn(session);
        return postService.dislikePost(id, userId);
    }

    @PostMapping(value = "/posts/{id}/undoDislike")
    public LikeDislikeMessageDTO undoDislikePost(@PathVariable long id, HttpSession session) {
        long userId = sessionValidator.isUserLogedIn(session);
        return postService.undoDislikePost(id, userId);
    }

//    @GetMapping(value = "/posts/allForeignPosts")
//    public List<ResponsePostDTO> getAllForeignPosts(HttpSession session) {
//        long userId = sessionValidator.isUserLogedIn(session);
//        return postService.getAllForeignPosts(userId);
//    }
    //NEW
    @GetMapping(value = "/posts/allPosts/")
    public Page<ResponsePostDTO> getAllPosts(Pageable pageable,HttpSession session) {
        long userId = sessionValidator.isUserLogedIn(session);
        return postService.getAllPosts(pageable,userId);
    }

    // vsichki postve bez tiq na usera
    @GetMapping(value = "/posts/allForeignPosts/{filterName}")
    public List<ResponsePostDTO> getAllForeignPostsFiltered(@PathVariable String filterName, HttpSession session) {
        long userId = sessionValidator.isUserLogedIn(session);
        return postService.getAllForeignPostsFiltered(userId, filterName);
    }

    @GetMapping(value = "/posts/newsfeed")
    public Page<ResponsePostDTO> getNewsfeed (Pageable page ,HttpSession session){
        long userId = sessionValidator.isUserLogedIn(session);
        return postService.getNewsfeed(page,userId);
    }

    @GetMapping(value = "/posts/newsfeed/{filterName}")
    public Page<ResponsePostDTO> getNewsfeedWithFilter (Pageable page,@PathVariable String filterName, HttpSession session){
        long userId = sessionValidator.isUserLogedIn(session);
        return postService.getNewsfeedWithFilter(page,userId, filterName);
    }
}

