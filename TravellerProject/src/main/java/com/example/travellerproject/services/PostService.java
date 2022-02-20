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
    private PostCategotyRepository postCategotyRepository;

    public ResponsePostDTO createPost(RequestPostDTO requestPostDTO,long userId) {
        long postCategoryId = requestPostDTO.getPostCategory();

        validator.validateTitle(requestPostDTO.getTitle());
        validator.validateLonitudeAndLatitude(requestPostDTO.getLongitude(),requestPostDTO.getLatitude());
        Post post = modelMapper.map(requestPostDTO, Post.class);
        post.setUser(validator.validateUserAndGet(userId));
        post.setPostCategory(validator.validateCategory(postCategoryId));
        post.setCreatedAt(LocalDate.now());
//        if (requestPostDTO.getLatitude().isBlank() ||
//                requestPostDTO.getLongitude().isBlank() ||
//                requestPostDTO.getTitle().isBlank()) {
//            throw new BadRequestExeption("Wrong declaration of the post");
//        }
//
//
//        post.setPostCategory(postCategotyRepository.findById(postCategoryId).orElseThrow(() -> new NotFoundExeption("Post Category not found " + postCategoryId)));
//        post.setUser(userRepository.findById(userId).orElseThrow(() -> new NotFoundExeption("User not found with id " + userId)));
        postRepository.save(post);
        ResponsePostDTO responsePostDTO = modelMapper.map(post, ResponsePostDTO.class);
        responsePostDTO.setUser(new OwnerOfPostDTO(userRepository.getById(userId)));
        //TODO location
        return responsePostDTO;
    }

    public ResponsePostDTO getById(long id) {
        Post post = validator.validatePostAndGet(id);
        return modelMapper.map(post,ResponsePostDTO.class);
//        Optional<Post> post = postRepository.findById(id);
//        if(post.isPresent()){
//            return modelMapper.map(post.get(),ResponsePostDTO.class);
//        }
//        else{
//            throw new BadRequestExeption("Post doesnt exist");
//        }
    }

    public MessageDTO deletePost(long id) {
        Post post = validator.validatePostAndGet(id);
        postRepository.delete(post);
        return new MessageDTO("Post deleted");
//        Optional<Post> post = postRepository.findById(id);
//        if(post.isPresent()){
//            postRepository.delete(post.get());
//            return new MessageDTO("Post deleted");
//        }
//        else{
//            throw new BadRequestExeption("Post doesnt exist");
//        }
    }

    public ResponsePostDTO editPost(RequestPostDTO requestPostDTO, long id,long userId) {
        Post post = validator.validatePostAndGet(id);
        validator.validateUserAndPostOwnership(post,userId);
//        Post post = postRepository.findById(id).orElseThrow(()-> new NotFoundExeption("No such a post"));
//        if(post.getUser().getId()!=userId){
//            throw new BadRequestExeption("You are not owner of this post");
//        }
        modelMapper.map(requestPostDTO,post);
        postRepository.save(post);
        return modelMapper.map(post,ResponsePostDTO.class);
    }
    public LikeDislikeMessageDTO likePost(long postId, long userId){
        Post post = validator.validatePostAndGet(postId);
        User user = validator.validateUserAndGet(userId);
//        Post post = getPostById(postId);
//        User user = getUserById(userId);
        if (user.getLikedPosts().contains(post)){
            throw new BadRequestExeption("You have already liked this post");
        }
        post.getLikers().add(user);
        postRepository.save(post);
        return new LikeDislikeMessageDTO("You have liked a post",post.getLikers().size());
    }

    public LikeDislikeMessageDTO undoLikePost(long postId, long userId){
        Post post = validator.validatePostAndGet(postId);
        User user = validator.validateUserAndGet(userId);
//        Post post = getPostById(postId);
//        User user = getUserById(userId);
        if (!user.getLikedPosts().contains(post)){
            throw new BadRequestExeption("You have to like the post before removing like");
        }
        post.getLikers().remove(user);
        postRepository.save(post);
        return new LikeDislikeMessageDTO("You have undid your like  ",post.getLikers().size());
    }

    public LikeDislikeMessageDTO dislikePost(long postId, long userId){
        Post post = validator.validatePostAndGet(postId);
        User user = validator.validateUserAndGet(userId);
//        Post post = getPostById(postId);
//        User user = getUserById(userId);
        if (user.getDislikedPosts().contains(post)){
            throw new BadRequestExeption("You have already disliked this post");
        }
        post.getDislikers().add(user);
        postRepository.save(post);
        return new LikeDislikeMessageDTO("You have disliked a post ",post.getDislikers().size());
    }

    public LikeDislikeMessageDTO undoDislikePost(long postId, long userId){
        Post post = validator.validatePostAndGet(postId);
        User user = validator.validateUserAndGet(userId);
//        Post post = getPostById(postId);
//        User user = getUserById(userId);
        if (!user.getDislikedPosts().contains(post)){
            throw new BadRequestExeption("You have to dislike the post before removing dislike");
        }
        post.getDislikers().remove(user);
        postRepository.save(post);
        return new LikeDislikeMessageDTO("You have undid your dislike  ",post.getDislikers().size());
    }

//
//    private User getUserById(long userId) {
//        return userRepository.findById(userId).orElseThrow(() -> new NotFoundExeption("User not found"));
//    }
//
//    private Post getPostById(long postId) {
//        return postRepository.findById(postId).orElseThrow(()->new NotFoundExeption("Post not found"));
//    }

    public MessageDTO tagUser(long userId, long tagUserid, long pId) {

        Post post = validator.validatePostAndGet(pId);
        User tagedUser = validator.validateUserAndGet(tagUserid);
//        Post post = postRepository.findById(pId).orElseThrow(() -> new NotFoundExeption("Post not found."));
//        User tagedUser = userRepository.findById(tagUserid).orElseThrow(() -> new NotFoundExeption("User not found"));
        if(post.getUserTagAtPosts().contains(tagedUser)){
            throw new BadRequestExeption("This user is already tagged in this post");
        }
        post.getUserTagAtPosts().add(tagedUser);
        postRepository.save(post);
        return new MessageDTO("You have tagged " + tagUserid);
    }
    public MessageDTO unTagUser(long userId, long tagUserid, long pId) {
        Post post = validator.validatePostAndGet(pId);
        User tagedUser = validator.validateUserAndGet(tagUserid);
//        Post post = postRepository.findById(pId).orElseThrow(() -> new NotFoundExeption("Post not found."));
//        User tagedUser = userRepository.findById(tagUserid).orElseThrow(() -> new NotFoundExeption("User not found"));
        if(userId!=post.getUser().getId()){
            throw new UnauthorizedExeption("You can't untag this user,because you aren't the owner of the post");
        }
        if(!post.getUserTagAtPosts().contains(tagedUser)){
            throw new BadRequestExeption("This user isn't tagged in this post");
        }
        post.getUserTagAtPosts().remove(tagedUser);
        postRepository.save(post);
        return new MessageDTO("You have untagged " + tagUserid);
    }

    public List<ResponsePostDTO> findPosts(String username) {
        User user = userRepository.findByUsername(username);
        if(user==null){
            throw new NotFoundExeption("User not found");
        }
        if(user.getPosts().isEmpty()){
            throw  new BadRequestExeption("This user don't have posts ");
        }
        List<ResponsePostDTO> postsDTO =  new ArrayList<>();
        for (Post e : user.getPosts()) {
            postsDTO.add(modelMapper.map(e,ResponsePostDTO.class));
        }
        return postsDTO;
    }


    public List<ResponsePostDTO> getNewsfeed(long userId) {
        User user = validator.validateUserAndGet(userId);
//        User user = userRepository.getById(userId);
        if (user.getFollowedUsers().isEmpty()) {
            throw new BadRequestExeption("You must have at least  one subscription for your newsfeed.");
        }
        //nz dali taka e pravilno da se sortirat
        List<ResponsePostDTO> newsfeed = new ArrayList<>();
        for (User currUser : user.getFollowedUsers()) {
            for (Post post : currUser.getPosts()) {
                newsfeed.add(modelMapper.map(post, ResponsePostDTO.class));
            }
        }
        if (newsfeed.isEmpty()) {
            throw new NotFoundExeption("There are no post in your newsfeed.");
        }
        newsfeed.sort((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()));
        return newsfeed;
    }

    public List<ResponsePostDTO> getNewsfeedWithFilter(long userId, String filterName){
        if (!filterName.equals("date") && !filterName.equals("category") && !filterName.equals("like")){
            throw new BadRequestExeption("No such filters");
        }
        List <ResponsePostDTO> sortedNewsfeed = getUnsortedNewsfeed(userId);
        return filterPostsCollection(sortedNewsfeed , filterName);
    }

    public List<ResponsePostDTO> getAllForeignPosts(long userId, String filterName){
        if (!filterName.equals("date") && !filterName.equals("category") && !filterName.equals("like")){
            throw new BadRequestExeption("No such filters");
        }
        List <ResponsePostDTO> sortedForeignPosts = getAllForeignPostsUnsorted(userId);
        return filterPostsCollection(sortedForeignPosts,filterName);
    }



    private List<ResponsePostDTO> getUnsortedNewsfeed(long userId){
        User user = userRepository.getById(userId);
        if(user.getFollowedUsers().isEmpty()){
            throw  new BadRequestExeption("You must have at least  one subscription for your newsfeed.");
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


    /*
    private List<ResponsePostDTO> getUnsortedNewsfeed(long userId){
        User user = userRepository.getById(userId);
        if(user.getFollowedUsers().isEmpty()){
            throw  new BadRequestExeption("You must have at least  one subscription for your newsfeed.");
        }
        List <ResponsePostDTO> unsortedNewsfeed = new ArrayList<>();
        for (User currUser : user.getFollowedUsers()) {
            for (Post post : currUser.getPosts()) {
                unsortedNewsfeed.add(modelMapper.map(post, ResponsePostDTO.class));
            }
        }
        return  unsortedNewsfeed;
    }

     */
}
