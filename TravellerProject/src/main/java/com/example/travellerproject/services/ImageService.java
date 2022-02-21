package com.example.travellerproject.services;

import com.example.travellerproject.controllers.SessionValidator;
import com.example.travellerproject.exeptions.BadRequestExeption;
import com.example.travellerproject.exeptions.NotFoundExeption;
import com.example.travellerproject.exeptions.UnauthorizedExeption;
import com.example.travellerproject.model.dto.media.ImageDTO;
import com.example.travellerproject.model.pojo.Image;
import com.example.travellerproject.model.pojo.Post;
import com.example.travellerproject.model.pojo.User;
import com.example.travellerproject.repositories.ImageRepositoty;
import com.example.travellerproject.repositories.PostRepository;
import com.example.travellerproject.repositories.UserRepository;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;

@Service
public class ImageService {
    @Autowired
    private SessionValidator sessionValidator;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ImageRepositoty imageRepositoty;
    @Autowired
    private Validator validator;


    @SneakyThrows
    public ResponseEntity<ImageDTO> uploadImg(MultipartFile file, HttpSession session, long postId) {
        long userId = sessionValidator.isUserLogedIn(session);
        User user = validator.validateUserAndGet(userId);
        Post post = validator.validatePostAndGet(postId);
        if(user.getId()!=post.getUser().getId()){
            throw new UnauthorizedExeption("This post isn't yours.So you cannot add images");
        }
        String  extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = System.nanoTime()+"."+ extension;
        Files.copy(file.getInputStream(), new File("images" + File.separator + name).toPath());
        Image image = new Image();
        image.setFilename(name);
        image.setPost(post);
        if(post.getImages().size()<3){
            post.getImages().add(image);
        }else{
            throw new BadRequestExeption("Sorry post cannot have more then 3 photos");
        }
        imageRepositoty.save(image);

        return ResponseEntity.ok(modelMapper.map(image,ImageDTO.class));
    }
}
