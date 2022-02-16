package com.example.travellerproject.services;

import com.example.travellerproject.exeptions.BadRequestExeption;
import com.example.travellerproject.model.dto.MessageDTO;
import com.example.travellerproject.model.dto.post.RequestPostDTO;
import com.example.travellerproject.model.dto.post.ResponsePostDTO;
import com.example.travellerproject.model.pojo.Post;
import com.example.travellerproject.repositories.PostRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ModelMapper modelMapper;

    public ResponsePostDTO createPost(RequestPostDTO requestPostDTO){
        postRepository.save(modelMapper.map(requestPostDTO, Post.class));
        //TODO location
        return modelMapper.map(requestPostDTO,ResponsePostDTO.class);
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
        Post post = postRepository.getById(id);
        if(post.getUser().getId()!=userId){
            throw new BadRequestExeption("You are not owner of this post");
        }
        modelMapper.map(requestPostDTO,post);
        postRepository.save(post);
        return modelMapper.map(post,ResponsePostDTO.class);


    }
}
