package com.example.travellerproject.services;

import com.example.travellerproject.controllers.SessionValidator;
import com.example.travellerproject.exeptions.BadRequestExeption;
import com.example.travellerproject.exeptions.NotFoundExeption;
import com.example.travellerproject.exeptions.UnauthorizedExeption;
import com.example.travellerproject.model.dto.media.ImageDTO;
import com.example.travellerproject.model.dto.media.VideoDTO;
import com.example.travellerproject.model.pojo.Image;
import com.example.travellerproject.model.pojo.Post;
import com.example.travellerproject.model.pojo.User;
import com.example.travellerproject.model.pojo.Video;
import com.example.travellerproject.repositories.ImageRepositoty;
import com.example.travellerproject.repositories.PostRepository;
import com.example.travellerproject.repositories.UserRepository;
import com.example.travellerproject.repositories.VideoRepository;
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
public class VideoService {
    @Autowired
    private SessionValidator sessionValidator;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private Validator validator;

    @SneakyThrows
    public ResponseEntity<VideoDTO> uploadImg(MultipartFile file, HttpSession session, long postId) {
        long userId = sessionValidator.isUserLogedIn(session);
        User user = validator.validateUserAndGet(userId);
        Post post = validator.validatePostAndGet(postId);
        if(user.getId()!=post.getUser().getId()){
            throw new UnauthorizedExeption("This post isn't yours.So you cannot add video");
        }
        String  extension = FilenameUtils.getExtension(file.getOriginalFilename());
        validator.validateVideoExtention(extension);
        String name = System.nanoTime()+"."+ extension;
        Files.copy(file.getInputStream(), new File("video" + File.separator + name).toPath());
        Video video = new Video();
        video.setFilename(name);
        video.setPost(post);
        if(post.getVideos().size()<1){
            post.getVideos().add(video);
        }
        else{
            throw new BadRequestExeption("Sorry post cannot have more than 1 video.");
        }
        videoRepository.save(video);

        return ResponseEntity.ok(modelMapper.map(video, VideoDTO.class));
    }
}
