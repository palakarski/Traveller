package com.example.travellerproject.services;

import com.example.travellerproject.exeptions.BadRequestExeption;
import com.example.travellerproject.exeptions.NotFoundExeption;
import com.example.travellerproject.model.dto.MessageDTO;
import com.example.travellerproject.model.dto.post.RequestPostDTO;
import com.example.travellerproject.model.dto.post.ResponsePostDTO;
import com.example.travellerproject.model.dto.user.OwnerOfPostDTO;
import com.example.travellerproject.model.dto.user.UserWithOutPassDTO;
import com.example.travellerproject.model.pojo.Post;
import com.example.travellerproject.model.pojo.PostCategory;
import com.example.travellerproject.model.pojo.User;
import com.example.travellerproject.repositories.PostCategotyRepository;
import com.example.travellerproject.repositories.PostRepository;
import com.example.travellerproject.repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

//    public ResponsePostDTO createPost(RequestPostDTO requestPostDTO,long userIOd){
//        long postCategoryId = requestPostDTO.getPostCategory();
//        if(requestPostDTO.getLatitude().isBlank()||
//        requestPostDTO.getLongitude().isBlank()||
//        requestPostDTO.getCreatedAt()==null||
//        requestPostDTO.getTitle().isBlank()){
//            throw new BadRequestExeption("Wrong declaration of the post");
//        }
//        Post post = modelMapper.map(requestPostDTO,Post.class);
//        post.setPostCategory(postCategotyRepository.getById(postCategoryId));
//        User u = userRepository.findById(userIOd).orElseThrow(() -> new BadRequestExeption("lelelele"));
//        post.setUser(u);
//        postRepository.save(post);
//        ResponsePostDTO responsePostDTO=modelMapper.map(post,ResponsePostDTO.class);
//        responsePostDTO.setUser(new OwnerOfPostDTO(userRepository.getById(userIOd)));
//        //TODO location
//       return responsePostDTO;
//
//    }

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
}
