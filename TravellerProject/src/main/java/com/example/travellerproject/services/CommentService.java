package com.example.travellerproject.services;

import com.example.travellerproject.exeptions.BadRequestExeption;
import com.example.travellerproject.exeptions.NotFoundExeption;
import com.example.travellerproject.exeptions.UnauthorizedExeption;
import com.example.travellerproject.model.dto.MessageDTO;
import com.example.travellerproject.model.dto.comment.CommentRequestDTO;
import com.example.travellerproject.model.dto.comment.CommentResponseDTO;
import com.example.travellerproject.model.dto.post.ResponsePostDTO;
import com.example.travellerproject.model.dto.user.OwnerOfPostDTO;
import com.example.travellerproject.model.pojo.Comment;
import com.example.travellerproject.model.pojo.Post;
import com.example.travellerproject.model.pojo.User;
import com.example.travellerproject.repositories.CommentRepository;
import com.example.travellerproject.repositories.PostRepository;
import com.example.travellerproject.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;


    public CommentResponseDTO create(long userId, CommentRequestDTO commentRequestDTO, long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundExeption("Post doesnt exist"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundExeption("User doesnt exist"));
        if(commentRequestDTO.getText()==null||commentRequestDTO.getText().isBlank()){
            throw new BadRequestExeption("Cannot post empty comment");
        }
        Comment comment = modelMapper.map(commentRequestDTO,Comment.class);
        comment.setUser(user);
        comment.setPost(post);
        commentRepository.save(comment);
        CommentResponseDTO  commentResponseDTO= modelMapper.map(comment,CommentResponseDTO.class);
        commentResponseDTO.setResponsePostDTO(modelMapper.map(post, ResponsePostDTO.class));
        commentResponseDTO.setOwnerOfPostDTO(modelMapper.map(user, OwnerOfPostDTO.class));
        return commentResponseDTO;
    }


    public MessageDTO deleteComment(long id,long userId) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new NotFoundExeption("Comment not found."));
        if(comment.getUser().getId()!=userId){
            throw new UnauthorizedExeption("This comment isn't your,and you can't delete it.");
        }
        commentRepository.delete(comment);
        return new MessageDTO("Comment was deleted");
    }

    public CommentResponseDTO getById(long id) {
        Comment comment=commentRepository.findById(id).orElseThrow(() -> new NotFoundExeption("Comment not found."));
        CommentResponseDTO commentResponseDTO = modelMapper.map(comment,CommentResponseDTO.class);
        commentResponseDTO.setOwnerOfPostDTO(modelMapper.map(comment.getUser(),OwnerOfPostDTO.class));
        commentResponseDTO.setResponsePostDTO(modelMapper.map(comment.getPost(),ResponsePostDTO.class));
        return commentResponseDTO;
    }

    public CommentResponseDTO editComment(CommentRequestDTO commentRequestDTO,long id,long userId) {
        Comment comment=commentRepository.findById(id).orElseThrow(() -> new NotFoundExeption("Comment not found."));
        if(comment.getUser().getId()!=userId){
            throw new UnauthorizedExeption("You cant edit comment that u didnt post.");
        }
        modelMapper.map(commentRequestDTO,comment);
        commentRepository.save(comment);
        CommentResponseDTO commentResponseDTO = modelMapper.map(comment,CommentResponseDTO.class);
        commentResponseDTO.setOwnerOfPostDTO(modelMapper.map(comment.getUser(),OwnerOfPostDTO.class));
        commentResponseDTO.setResponsePostDTO(modelMapper.map(comment.getPost(),ResponsePostDTO.class));
        return commentResponseDTO;
    }
}
