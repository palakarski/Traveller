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
import com.example.travellerproject.repositories.PostRepository;
import com.example.travellerproject.repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Transactional
    public ResponsePostDTO createPost(RequestPostDTO requestPostDTO,long userId) {
        long postCategoryId = requestPostDTO.getPostCategory();
        validator.validateTitle(requestPostDTO.getTitle());
        validator.validateLonitudeAndLatitude(requestPostDTO.getLongitude(),requestPostDTO.getLatitude());
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
        validator.validateLonitudeAndLatitude(requestPostDTO.getLongitude(),requestPostDTO.getLatitude());
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
        User tagedUser = validator.validateUserAndGet(tagUserid);
        if(post.getUserTagAtPosts().contains(tagedUser)){
            throw new BadRequestException("This user is already tagged in this post");
        }
        post.getUserTagAtPosts().add(tagedUser);
        postRepository.save(post);
        return new MessageDTO("You have tagged " + tagUserid);
    }

    @Transactional
    public MessageDTO unTagUser(long userId, long tagUserid, long pId) {
        Post post = validator.validatePostAndGet(pId);
        User tagedUser = validator.validateUserAndGet(tagUserid);
        if(userId!=post.getUser().getId()){
            throw new UnauthorizedException("You can't untag this user,because you aren't the owner of the post");
        }
        if(!post.getUserTagAtPosts().contains(tagedUser)){
            throw new BadRequestException("This user isn't tagged in this post");
        }
        post.getUserTagAtPosts().remove(tagedUser);
        postRepository.save(post);
        return new MessageDTO("You have untagged " + tagUserid);
    }


    public List<ResponsePostDTO> findPosts(String username) {
        User user = userRepository.findByUsername(username);
        if(user==null){
            throw new NotFoundException("User with this username "+username+" not found");
        }
        if(user.getPosts().isEmpty()){
            throw  new BadRequestException("This user don't have posts ");
        }
        List<ResponsePostDTO> postsDTO =  new ArrayList<>();
        for (Post e : user.getPosts()) {
            postsDTO.add(modelMapper.map(e,ResponsePostDTO.class));
        }
        return postsDTO;
    }


    public List<ResponsePostDTO> getNewsfeed(long userId) {
        User user = validator.validateUserAndGet(userId);
        if (user.getFollowedUsers().isEmpty()) {
            throw new BadRequestException("You must have at least  one subscription for your newsfeed.");
        }
        //nz dali taka e pravilno da se sortirat
        List<ResponsePostDTO> newsfeed = new ArrayList<>();
        for (User currUser : user.getFollowedUsers()) {
            for (Post post : currUser.getPosts()) {
                newsfeed.add(modelMapper.map(post, ResponsePostDTO.class));
            }
        }
        if (newsfeed.isEmpty()) {
            throw new NotFoundException("There are no post in your newsfeed.");
        }
        newsfeed.sort((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()));
        return newsfeed;
    }

    public List<ResponsePostDTO> getNewsfeedWithFilter(long userId, String filterName){
        if (!filterName.equals("date") && !filterName.equals("category") && !filterName.equals("like")){
            throw new BadRequestException("No such filters");
        }
        List <ResponsePostDTO> sortedNewsfeed = getUnsortedNewsfeed(userId);
        return filterPostsCollection(sortedNewsfeed , filterName);
    }

    public List<ResponsePostDTO> getAllForeignPostsFiltered(long userId, String filterName){
        if (!filterName.equals("date") && !filterName.equals("category") && !filterName.equals("like")){
            throw new BadRequestException("No such filters");
        }
        List <ResponsePostDTO> sortedForeignPosts = getAllForeignPostsUnsorted(userId);
        return filterPostsCollection(sortedForeignPosts,filterName);
    }



    private List<ResponsePostDTO> getUnsortedNewsfeed(long userId){
        User user = userRepository.getById(userId);
        if(user.getFollowedUsers().isEmpty()){
            throw  new BadRequestException("You must have at least  one subscription for your newsfeed.");
        }
        List <ResponsePostDTO> unsortedNewsfeed = new ArrayList<>();
        for (User currUser : user.getFollowedUsers()) {
            for (Post post : currUser.getPosts()) {
                int postLikes = post.getLikers().size();
                ResponsePostDTO currDto = modelMapper.map(post,ResponsePostDTO.class);
                currDto.setLikes(postLikes);
                unsortedNewsfeed.add(currDto);
            }
        }
        return  unsortedNewsfeed;
    }
    private List<ResponsePostDTO> getAllForeignPostsUnsorted(long userId){
        User u  = userRepository.getById(userId);
        String userName = u.getUsername();
        //findAllByUsernameIsNot - raboti sas string
       List<Post> allForeignPosts = postRepository.findPostByUserIsNot(u);
       List<ResponsePostDTO> dtos = new ArrayList<>();
       for(Post post : allForeignPosts){
           int postLikes = post.getLikers().size();
           ResponsePostDTO currDto = modelMapper.map(post,ResponsePostDTO.class);
           currDto.setLikes(postLikes);
           dtos.add(currDto);
       }
       return dtos;
    }

    private List<ResponsePostDTO> filterPostsCollection(List<ResponsePostDTO> collection,String filterName){
        switch (filterName){
            case "date" :{
                collection.sort((post1 , post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()));
            }break;
            case "category" :{
                collection.sort((post1 , post2) -> post2.getPostCategory().getCategoryType().compareTo(post1.getPostCategory().getCategoryType()));
            }break;
            case "like" : {
                collection.sort((post1 , post2) -> post2.getLikes()-post1.getLikes());
            }break;
        }
        return collection;
    }

    public List<CommentResponseDTO> findComments(long postId) {
        Post post = validator.validatePostAndGet(postId);
        List<CommentResponseDTO>  comments= new ArrayList<>();
        for (Comment c: post.getComments()) {
            comments.add(new CommentResponseDTO(c));
        }
        if(comments.isEmpty()){
            throw new BadRequestException("No comments found for this post");
        }
        return comments;
    }

    public List<ResponsePostDTO> getAllForeignPosts(long userId) {
        return getAllForeignPostsUnsorted(userId);
    }

    public List<OwnerOfPostOrCommentDTO> getAllTagedUsers(long userId, long pId) {
        Post post = validator.validatePostAndGet(pId);
        List<OwnerOfPostOrCommentDTO> tagedUsers = new ArrayList<>();
        if(post.getUserTagAtPosts().isEmpty()){
            throw new BadRequestException("No tagged users found.");
        }
        for (User u : post.getUserTagAtPosts()) {
            tagedUsers.add(new OwnerOfPostOrCommentDTO(u));
        }
        return tagedUsers;
    }
}
