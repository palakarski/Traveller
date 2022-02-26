package com.example.travellerproject.services;
import com.example.travellerproject.exceptions.BadRequestException;
import com.example.travellerproject.exceptions.NotFoundException;
import com.example.travellerproject.exceptions.UnauthorizedException;
import com.example.travellerproject.model.dto.LikeDislikeMessageDTO;
import com.example.travellerproject.model.dto.MessageDTO;
import com.example.travellerproject.model.dto.comment.CommentResponseDTO;
import com.example.travellerproject.model.dto.post.RequestPostDTO;
import com.example.travellerproject.model.dto.post.ResponsePostDTO;
import com.example.travellerproject.model.dto.user.OwnerOfPostOrCommentDTO;
import com.example.travellerproject.model.pojo.Comment;
import com.example.travellerproject.model.pojo.Post;
import com.example.travellerproject.model.pojo.User;
import com.example.travellerproject.repositories.CommentRepository;
import com.example.travellerproject.repositories.PostRepository;
import com.example.travellerproject.repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

@Log4j2
@Service
public class PostService {
    @Autowired
    private Validator validator;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;


    @Transactional
    public ResponsePostDTO createPost(RequestPostDTO requestPostDTO,long userId) {
        long postCategoryId = requestPostDTO.getPostCategory();
        validator.validateTitle(requestPostDTO.getTitle());
        validator.validateLongtitudeAndLatitude(requestPostDTO.getLongitude(),requestPostDTO.getLatitude());
        Post post = modelMapper.map(requestPostDTO, Post.class);
        post.setUser(validator.validateUserAndGet(userId));
        post.setPostCategory(validator.validateCategory(postCategoryId));
        post.setCreatedAt(LocalDate.now());
        postRepository.save(post);
        return new ResponsePostDTO(post);
    }
    
    public ResponsePostDTO getById(long id) {

        Post post = validator.validatePostAndGet(id);
        return new ResponsePostDTO(post);

    }
    
    public MessageDTO deletePost(long id,long userId) {

        Post post = validator.validatePostAndGet(id);
        validator.validateUserAndPostOwnership(post,userId);
        postRepository.delete(post);
        return new MessageDTO("Post deleted");

    }
    
    @Transactional
    public ResponsePostDTO editPost(RequestPostDTO requestPostDTO, long id,long userId) {
        Post post = validator.validatePostAndGet(id);
        validator.validateUserAndPostOwnership(post,userId);
        validator.validateTitle(requestPostDTO.getTitle());
        validator.validateLongtitudeAndLatitude(requestPostDTO.getLongitude(),requestPostDTO.getLatitude());
        validator.validateCategory(requestPostDTO.getPostCategory());
        modelMapper.map(requestPostDTO,post);
        postRepository.save(post);
        return modelMapper.map(post,ResponsePostDTO.class);
    }
    
    @Transactional
    public LikeDislikeMessageDTO likePost(long postId, long userId){
        Post post = validator.validatePostAndGet(postId);
        User user = validator.validateUserAndGet(userId);
        if (user.getLikedPosts().contains(post)){
            throw new BadRequestException("You have already liked this post");
        }
        if (user.getDislikedPosts().contains(post)){
            throw new BadRequestException("You have disliked this post.Please undo it.");
        }
        post.getLikers().add(user);
        postRepository.save(post);
        return new LikeDislikeMessageDTO("You have liked a post",post.getLikers().size());
    }
    
    @Transactional
    public LikeDislikeMessageDTO undoLikePost(long postId, long userId){
        Post post = validator.validatePostAndGet(postId);
        User user = validator.validateUserAndGet(userId);
        if (!user.getLikedPosts().contains(post)){
            throw new BadRequestException("You have to like the post before removing like");
        }

        post.getLikers().remove(user);
        postRepository.save(post);
        return new LikeDislikeMessageDTO("You have undid your like  ",post.getLikers().size());
    }
    
    @Transactional
    public LikeDislikeMessageDTO dislikePost(long postId, long userId){
        Post post = validator.validatePostAndGet(postId);
        User user = validator.validateUserAndGet(userId);
        if (user.getDislikedPosts().contains(post)){
            throw new BadRequestException("You have already disliked this post");
        }
        if (user.getLikedPosts().contains(post)){
            throw new BadRequestException("You have liked this post.Please undo it.");
        }
        post.getDislikers().add(user);
        postRepository.save(post);
        return new LikeDislikeMessageDTO("You have disliked a post ",post.getDislikers().size());
    }
    
    @Transactional
    public LikeDislikeMessageDTO undoDislikePost(long postId, long userId){
        Post post = validator.validatePostAndGet(postId);
        User user = validator.validateUserAndGet(userId);
        if (!user.getDislikedPosts().contains(post)){
            throw new BadRequestException("You have to dislike the post before removing dislike");
        }
        post.getDislikers().remove(user);
        postRepository.save(post);
        return new LikeDislikeMessageDTO("You have undid your dislike  ",post.getDislikers().size());
    }
    
    @Transactional
    public MessageDTO tagUser(long userId, long tagUserid, long pId) {

        Post post = validator.validatePostAndGet(pId);
        User taggedUser = validator.validateUserAndGet(tagUserid);
        if(post.getUserTagAtPosts().contains(taggedUser)){
            throw new BadRequestException("This user is already tagged in this post");
        }
        post.getUserTagAtPosts().add(taggedUser);
        postRepository.save(post);
        return new MessageDTO("You have tagged " + tagUserid);
    }
    
    @Transactional
    public MessageDTO unTagUser(long userId, long tagUserid, long pId) {
        Post post = validator.validatePostAndGet(pId);
        User tagedUser = validator.validateUserAndGet(tagUserid);
        if(userId!=post.getUser().getId()){
            throw new UnauthorizedException("You can't untag this user, because you aren't owner of the post");
        }
        if(!post.getUserTagAtPosts().contains(tagedUser)){
            throw new BadRequestException("This user isn't tagged in this post");
        }
        post.getUserTagAtPosts().remove(tagedUser);
        postRepository.save(post);
        return new MessageDTO("You have untagged " + tagUserid);
    }

    public List<OwnerOfPostOrCommentDTO> getAllTaggedUsers(long userId, long pId) {
        Post post = validator.validatePostAndGet(pId);
        List<OwnerOfPostOrCommentDTO> taggedUsers = new ArrayList<>();
        if(post.getUserTagAtPosts().isEmpty()){
            throw new BadRequestException("No tagged users found.");
        }
        for (User u : post.getUserTagAtPosts()) {
            taggedUsers.add(new OwnerOfPostOrCommentDTO(u));
        }
        return taggedUsers;
    }
   
    public List<ResponsePostDTO> findPostsByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if(user==null){
            throw new NotFoundException("User with username: "+username+" not found");
        }
        if(user.getPosts().isEmpty()){
            throw  new BadRequestException("This user don't have posts ");
        }
        List<ResponsePostDTO> postsDTO =  new ArrayList<>();
        for (Post e : user.getPosts()) {
            postsDTO.add(new ResponsePostDTO(e));
        }
        return postsDTO;
    }


    public Page<ResponsePostDTO> getNewsfeed(Pageable page, long userId) {
        User user = validator.validateUserAndGet(userId);
        if (user.getFollowedUsers().isEmpty()) {
            throw new BadRequestException("You must have at least  one subscription for your newsfeed.");
        }
        Page<Post> posts = postRepository.getNewsFeed(page,userId);
        Page<ResponsePostDTO> responseNewsFeed = posts.map(ResponsePostDTO::new);
        if (posts.isEmpty()) {
            throw new NotFoundException("There are no post in your newsfeed.");
        }
        //todo return sorted newsfeed
        return responseNewsFeed;
    }


    public Page<ResponsePostDTO> getNewsfeedFiltered(Pageable pageable, long userId, String filterName){
        User user = userRepository.getById(userId);
        if (!filterName.equals("date") && !filterName.equals("category") && !filterName.equals("like")){
            throw new BadRequestException("No such filters");
        }
        if(user.getFollowedUsers().isEmpty()){
            throw  new BadRequestException("You must have at least  one subscription for your newsfeed.");
        }
        Page<Post> posts = null;
        switch (filterName){
            case "date" -> posts = postRepository.getNewsFeedSortedByDate(pageable,userId);
            case "category" -> posts = postRepository.getNewsFeedSortedByCategory(pageable,userId);
            case "like" -> posts = postRepository.getNewsFeedSortedByLikes(pageable,userId);
        }
        Page<ResponsePostDTO> sortedNewsfeed = posts.map(ResponsePostDTO::new);
        return sortedNewsfeed;
    }
    ////TODO add pageination to those metodo:

    public Page<ResponsePostDTO> getForeignPosts(Pageable page,long userId) {
        User user = validator.validateUserAndGet(userId);
        Page<Post> postPage = postRepository.getForeignPost(page,userId);
        Page<ResponsePostDTO> postDTOS = postPage.map(ResponsePostDTO::new);
        return postDTOS;
    }

    public Page<ResponsePostDTO> getForeignPostsFiltered(Pageable page,long userId, String filterName){
        if (!filterName.equals("date") && !filterName.equals("category") && !filterName.equals("like")){
            throw new BadRequestException("No such filters");
        }
        Page<Post> posts = null;
        switch (filterName){
            case "date" -> posts = postRepository.getAllForeignPostByDate(page,userId);
            case "category" ->posts = postRepository.getAllForeignPostByCategory(page,userId);
            case "like"->posts = postRepository.getAllForeignPostByLikes(page,userId);
        }
        Page<ResponsePostDTO>postDTOSFiltered= posts.map(ResponsePostDTO::new);
        return postDTOSFiltered;
    }





//    public List<CommentResponseDTO> findCommentsByPosts(long postId) {
//        Post post = validator.validatePostAndGet(postId);
//        List<CommentResponseDTO>  comments= new ArrayList<>();
//        for (Comment c: post.getComments()) {
//            comments.add(new CommentResponseDTO(c));
//        }
//        if(comments.isEmpty()){
//            throw new BadRequestException("No comments found for this post");
//        }
//        return comments;
//    }
//
//    public List<ResponsePostDTO> getAllForeignPosts(long userId) {
//        List<Post> posts = postRepository.getAllForeignPost(userId);
//        List<ResponsePostDTO> postDTOS = new ArrayList<>();
//        for (Post p : posts){
//            postDTOS.add(modelMapper.map(p,ResponsePostDTO.class));
//        }
//        return postDTOS;
//    }


    public Page<CommentResponseDTO> getAllCommentsByPost(Pageable page, long postId) {
        Post post = validator.validatePostAndGet(postId);
        Page<Comment> comments = commentRepository.findAllByPostId(page,postId);
        Page<CommentResponseDTO> commentResponseDTOS = comments.map(CommentResponseDTO::new);
        if(comments.isEmpty()){
            throw new BadRequestException("No comments found for this post");
        }
        return commentResponseDTOS;
    }
}
