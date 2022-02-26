package com.example.travellerproject.controllers;
import com.example.travellerproject.exceptions.NotFoundException;
import com.example.travellerproject.model.dto.media.ImageDTO;
import com.example.travellerproject.services.ImageService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;

@RestController
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping(value = "/images/upload/post/{id}")
    public ResponseEntity<ImageDTO> uploadImg(@RequestParam(name = "file") MultipartFile file, @PathVariable long id, HttpSession session){
        return imageService.uploadImg(file,session,id);
    }
    @SneakyThrows
    @GetMapping(value = "/images/{filename}")
    public void downloadById(@PathVariable String filename, HttpServletResponse response){
        File file = new File("images"+File.separator+filename);
        if(!file.exists()){
            throw  new NotFoundException("File does not exist");
        }
        Files.copy(file.toPath(),response.getOutputStream());
    }
}
