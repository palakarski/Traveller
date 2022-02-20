package com.example.travellerproject.services;

import com.example.travellerproject.controllers.SessionValidator;
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
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private VideoRepository videoRepository;


    @SneakyThrows
    public ResponseEntity<VideoDTO> uploadImg(MultipartFile file, HttpSession session, long postId) {
        long userId = sessionValidator.isUserLogedIn(session);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundExeption("User not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundExeption("Post not found"));
        if(user.getId()!=post.getUser().getId()){
            throw new UnauthorizedExeption("This post isn't yours.So you cannnot add video");
        }
        String extention = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = System.nanoTime()+"."+extention;
        Files.copy(file.getInputStream(), new File("video" + File.separator + name).toPath());
        Video video = new Video();
        video.setFilename(name);
        video.setPost(post);
        videoRepository.save(video);
        post.getVideos().add(video);
        return ResponseEntity.ok(modelMapper.map(video, VideoDTO.class));
    }
}
