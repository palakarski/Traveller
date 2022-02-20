package com.example.travellerproject.controllers;
import com.example.travellerproject.exeptions.BadRequestExeption;
import com.example.travellerproject.model.dto.LikeDislikeMessageDTO;
import com.example.travellerproject.model.dto.MessageDTO;
import com.example.travellerproject.model.dto.post.RequestPostDTO;
import com.example.travellerproject.model.dto.post.ResponsePostDTO;
import com.example.travellerproject.model.pojo.Post;
import com.example.travellerproject.repositories.PostRepository;
import com.example.travellerproject.services.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;


@RestController
public class PostController {

    private static final String LOGGED = "logged";
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PostService postService;
    @Autowired
    private SessionValidator sessionValidator;


    @PostMapping(value = "post/create")
    public ResponseEntity<ResponsePostDTO> createPost(@RequestBody RequestPostDTO requestPostDTO, HttpSession session){
        if(session.isNew() || session.getAttribute(LOGGED)==null){
            throw new BadRequestExeption("You need to logged first");
        }
        long id = (Long) session.getAttribute(LOGGED);
        return ResponseEntity.ok(postService.createPost(requestPostDTO,id));
    }


    @DeleteMapping(value = "delete/{id}")
    public MessageDTO deletePost(@PathVariable long id,HttpSession session){
        if(session.isNew() || session.getAttribute(LOGGED)==null){
            throw new BadRequestExeption("You need to logged first");
        }
        return postService.deletePost(id);
    }
    @GetMapping(value = "/post/{id}")
    public ResponseEntity<ResponsePostDTO> getPostById(@PathVariable long id,HttpSession session){
        if(session.isNew() || session.getAttribute(LOGGED)==null){
            throw new BadRequestExeption("You need to logged first");
        }
        return ResponseEntity.ok(postService.getById(id));
    }
    @PostMapping(value ="/post/{id}/edit")
    public ResponseEntity<ResponsePostDTO> editPost(@RequestBody RequestPostDTO requestPostDTO,@PathVariable long id,HttpSession session){
        if(session.isNew() || session.getAttribute(LOGGED)==null){
            throw new BadRequestExeption("You need to logged first");
        }
        long userId = (Long)session.getAttribute(LOGGED);
        return ResponseEntity.ok(postService.editPost(requestPostDTO,id,userId));
    }
    @PostMapping(value = "/post/{pId}/tag/{id}")
    public MessageDTO tagUser(@PathVariable long pId ,@PathVariable long id,HttpSession session){
        if(session.isNew() || session.getAttribute(LOGGED)==null){
            throw new BadRequestExeption("You need to logged first");
        }
        long userId = (Long)session.getAttribute(LOGGED);
        return postService.tagUser(userId,id,pId);
    }

    @PostMapping(value = "/post/{pId}/untag/{id}")
    public MessageDTO unTagUser(@PathVariable long pId ,@PathVariable long id,HttpSession session){
        if(session.isNew() || session.getAttribute(LOGGED)==null){
            throw new BadRequestExeption("You need to logged first");
        }
        long userId = (Long)session.getAttribute(LOGGED);
        return postService.unTagUser(userId,id,pId);
    }

    @GetMapping(value = "post/search/{username}")
    public List<ResponsePostDTO> findAllPostOfUser(@PathVariable String username, HttpSession session){
        sessionValidator.isUserLoged(session);
        return postService.findPosts(username);
    }

    //return type ?
    @PostMapping(value = "/posts/{id}/like")
    public LikeDislikeMessageDTO likePost(@PathVariable long id, HttpSession session){
        long userId = sessionValidator.isUserLogedIn(session);
        return postService.likePost(id,userId);
    }

    @PostMapping(value = "/posts/{id}/undoLike")
    public LikeDislikeMessageDTO undoLikePost(@PathVariable long id, HttpSession session){
        long userId = sessionValidator.isUserLogedIn(session);
        return postService.undoLikePost(id,userId);
    }

    @PostMapping(value = "/posts/{id}/dislike")
    public LikeDislikeMessageDTO dislikePost(@PathVariable long id, HttpSession session){
        long userId = sessionValidator.isUserLogedIn(session);
        return postService.dislikePost(id,userId);
    }

    @PostMapping(value = "/posts/{id}/undoDislike")
    public LikeDislikeMessageDTO undoDislikePost(@PathVariable long id, HttpSession session){
        long userId = sessionValidator.isUserLogedIn(session);
        return postService.undoDislikePost(id,userId);
    }

}
