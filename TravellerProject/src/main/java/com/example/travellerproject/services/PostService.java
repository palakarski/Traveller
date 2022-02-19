package com.example.travellerproject.services;
import com.example.travellerproject.exeptions.BadRequestExeption;
import com.example.travellerproject.exeptions.NotFoundExeption;
import com.example.travellerproject.exeptions.UnauthorizedExeption;
import com.example.travellerproject.model.dto.LikeDislikeMessageDTO;
import com.example.travellerproject.model.dto.MessageDTO;
import com.example.travellerproject.model.dto.post.RequestPostDTO;
import com.example.travellerproject.model.dto.post.ResponsePostDTO;
import com.example.travellerproject.model.dto.user.OwnerOfPostDTO;
import com.example.travellerproject.model.pojo.Post;
import com.example.travellerproject.model.pojo.User;
import com.example.travellerproject.repositories.PostCategotyRepository;
import com.example.travellerproject.repositories.PostRepository;
import com.example.travellerproject.repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Log4j2
@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostCategotyRepository postCategotyRepository;
    public ResponsePostDTO createPost(RequestPostDTO requestPostDTO,long userId) {
        long postCategoryId = requestPostDTO.getPostCategory();
        if (requestPostDTO.getLatitude().isBlank() ||
                requestPostDTO.getLongitude().isBlank() ||
                requestPostDTO.getTitle().isBlank()) {
            throw new BadRequestExeption("Wrong declaration of the post");
        }
        Post post = modelMapper.map(requestPostDTO, Post.class);
        post.setCreatedAt(LocalDate.now());
        post.setPostCategory(postCategotyRepository.findById(postCategoryId).orElseThrow(() -> new NotFoundExeption("Post Category not found " + postCategoryId)));
        post.setUser(userRepository.findById(userId).orElseThrow(() -> new NotFoundExeption("User not found with id " + userId)));
        postRepository.save(post);
        ResponsePostDTO responsePostDTO = modelMapper.map(post, ResponsePostDTO.class);
        responsePostDTO.setUser(new OwnerOfPostDTO(userRepository.getById(userId)));
        //TODO location
        return responsePostDTO;
    }

    public ResponsePostDTO getById(long id) {
        Optional<Post> post = postRepository.findById(id);
        if(post.isPresent()){
            return modelMapper.map(post.get(),ResponsePostDTO.class);
        }
        else{
            throw new BadRequestExeption("Post doesnt exist");
        }
    }

    public MessageDTO deletePost(long id) {
        Optional<Post> post = postRepository.findById(id);
        if(post.isPresent()){
            postRepository.delete(post.get());
            return new MessageDTO("Post deleted");
        }
        else{
            throw new BadRequestExeption("Post doesnt exist");
        }
    }

    public ResponsePostDTO editPost(RequestPostDTO requestPostDTO, long id,long userId) {
        Post post = postRepository.findById(id).orElseThrow(()-> new NotFoundExeption("No such a post"));
        if(post.getUser().getId()!=userId){
            throw new BadRequestExeption("You are not owner of this post");
        }
        modelMapper.map(requestPostDTO,post);
        postRepository.save(post);
        return modelMapper.map(post,ResponsePostDTO.class);
    }
    public LikeDislikeMessageDTO likePost(long postId, long userId){
        Post post = getPostById(postId);
        User user = getUserById(userId);
        if (user.getLikedPosts().contains(post)){
            throw new BadRequestExeption("You have already liked this post");
        }
        post.getLikers().add(user);
        postRepository.save(post);
        return new LikeDislikeMessageDTO("You have liked a post",post.getLikers().size());
    }

    public LikeDislikeMessageDTO undoLikePost(long postId, long userId){
        Post post = getPostById(postId);
        User user = getUserById(userId);
        if (!user.getLikedPosts().contains(post)){
            throw new BadRequestExeption("You have to like the post before removing like");
        }
        post.getLikers().remove(user);
        postRepository.save(post);
        return new LikeDislikeMessageDTO("You have undid your like  ",post.getLikers().size());
    }

    public LikeDislikeMessageDTO dislikePost(long postId, long userId){
        Post post = getPostById(postId);
        User user = getUserById(userId);
        if (user.getDislikedPosts().contains(post)){
            throw new BadRequestExeption("You have already disliked this post");
        }
        post.getDislikers().add(user);
        postRepository.save(post);
        return new LikeDislikeMessageDTO("You have disliked a post ",post.getDislikers().size());
    }

    public LikeDislikeMessageDTO undoDislikePost(long postId, long userId){
        Post post = getPostById(postId);
        User user = getUserById(userId);
        if (!user.getDislikedPosts().contains(post)){
            throw new BadRequestExeption("You have to dislike the post before removing dislike");
        }
        post.getDislikers().remove(user);
        postRepository.save(post);
        return new LikeDislikeMessageDTO("You have undid your dislike  ",post.getDislikers().size());
    }


    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundExeption("User not found"));
    }

    private Post getPostById(long postId) {
        return postRepository.findById(postId).orElseThrow(()->new NotFoundExeption("Post not found"));
    }

    public MessageDTO tagUser(long userId, long id, long pId) {
        Post post = postRepository.findById(pId).orElseThrow(() -> new NotFoundExeption("Post not found."));
        User tagedUser = userRepository.findById(id).orElseThrow(() -> new NotFoundExeption("User not found"));
        if(post.getUserTagAtPosts().contains(tagedUser)){
            throw new BadRequestExeption("This user is already tagged in this post");
        }
        post.getUserTagAtPosts().add(tagedUser);
        postRepository.save(post);
        return new MessageDTO("You have tagged " + id);
    }
    public MessageDTO unTagUser(long userId, long id, long pId) {
        Post post = postRepository.findById(pId).orElseThrow(() -> new NotFoundExeption("Post not found."));
        User tagedUser = userRepository.findById(id).orElseThrow(() -> new NotFoundExeption("User not found"));
        if(userId!=post.getUser().getId()){
            throw new UnauthorizedExeption("You can't untag this user,because you aren't the owner of the post");
        }
        if(!post.getUserTagAtPosts().contains(tagedUser)){
            throw new BadRequestExeption("This user isn't tagged in this post");
        }
        post.getUserTagAtPosts().remove(tagedUser);
        postRepository.save(post);
        return new MessageDTO("You have untagged " + id);
    }

    public List<ResponsePostDTO> findPosts(String username) {
        User user = userRepository.findByUsername(username);
        if(user==null){
            throw new NotFoundExeption("User not found");
        }
        if(user.getPosts().isEmpty()){
            throw  new BadRequestExeption("This user don't have posts ");
        }
        List<ResponsePostDTO> posts =  new ArrayList<>();
        for (Post e : user.getPosts()) {
            posts.add(modelMapper.map(e,ResponsePostDTO.class));
        }
        return posts;
    }
}
