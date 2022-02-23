package com.example.travellerproject.services;

import com.example.travellerproject.exceptions.BadRequestException;
import com.example.travellerproject.exceptions.NotFoundException;
import com.example.travellerproject.exceptions.UnauthorizedException;
import com.example.travellerproject.model.pojo.Comment;
import com.example.travellerproject.model.pojo.Post;
import com.example.travellerproject.model.pojo.PostCategory;
import com.example.travellerproject.model.pojo.User;
import com.example.travellerproject.repositories.CommentRepository;
import com.example.travellerproject.repositories.PostCategotyRepository;
import com.example.travellerproject.repositories.PostRepository;
import com.example.travellerproject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class Validator {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostCategotyRepository categotyRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private CommentRepository commentRepository;


    String lettersEng = "[A-Z][a-z]+";

    public void validateUsername(String username){

        if(username == null || username.isBlank()){
            throw new BadRequestException("Username is mandatory");
        }
        if(username.length()<8){
            throw new BadRequestException("Username must be atleast 8 symbols and lesser than 20 symbols");
        }
        if(!username.matches("^(?=.{8,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$")){
                throw new BadRequestException("Username must be between 8 and 20 characters.Only latin letters and numbers allowed");
        }
    }
    public void checkUsernameUnique(String username){
        User u = userRepository.findByUsername(username);
        if(u!=null){
            throw new BadRequestException("Username is already taken.");
        }
    }
    public void validPassword(String password){
        if(password == null || password.isBlank()){
            throw new BadRequestException("Password is mandatory");
        }
        if(password.length()>30){
            throw new BadRequestException("Password need to be lesser than 30 characters");
        }
        if(!password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")){
            throw new BadRequestException("Password minimum eight characters, at least one letter, one number and one special character:");
            //TODO wrong message
        }

    }
    public void matchPassAndConfPass(String password,String confpassword){
        if(!password.equals(confpassword)){
            throw new BadRequestException("Passwords doesnt match");
        }
    }

    public void validEmail(String email){
        if(userRepository.findByEmail(email)!=null){
            throw new BadRequestException("Email is already taken");
        }
        if(!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new BadRequestException("Wrong email.");
        }

    }

    public void validateFirstnameAndLastName(String firstname,String lastname) {

        if (firstname.length() < 3 || firstname.length() > 20) {
            throw new BadRequestException("First name must be between 3 and 20 characters.");
        }
            if (lastname.length() < 3 || lastname.length() > 20) {
                throw new BadRequestException("Last name must be between 3 and 20 characters.");
            }
        if (!firstname.matches(lettersEng) || !lastname.matches(lettersEng)) {
            throw new BadRequestException("Names must be written in latinic and must start with capital letter.");
        }
        }

    public void validateDateOfBirth(LocalDate dateOfBirth){
        if(dateOfBirth.isBefore(LocalDate.of(1920,1,1))||dateOfBirth.isAfter(LocalDate.of(2016,1,1))){
            throw new UnauthorizedException("Either you are too young or too old");
        }
    }
    public void validateLonitudeAndLatitude(String longitude,String latitude){
        if(longitude.isBlank()||latitude.isBlank()){
            throw new BadRequestException("Empty cordinates");
        }
        //Regex with six digital decimal
        if(!latitude.matches("^(\\+|-)?(?:90(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]{1,6})?))$")){
            throw new BadRequestException("Incorrect latitude.");
        }
        if(!longitude.matches("^(\\+|-)?(?:180(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]{1,6})?))$")){
            throw new BadRequestException("Incorrect longitude.");
        }

    }
    public void validateTitle(String title){
        if(title.isBlank()||title.length()<20){
            throw new BadRequestException("Title must be at least 20 characters");

        }
    }
    public PostCategory validateCategory(long cateId){
        return categotyRepository.findById(cateId).orElseThrow(()-> new NotFoundException("Post Category not found " + cateId));
    }

    public User validateUserAndGet(long userId){
        return userRepository.findById(userId).orElseThrow(()-> new NotFoundException("User not found with id " + userId));
    }
    public Post validatePostAndGet(long postId){
        return postRepository.findById(postId).orElseThrow(()-> new NotFoundException("Post not found with id " + postId));
    }
    public Comment validateCommentAndGet(long comId){
        return commentRepository.findById(comId).orElseThrow(()-> new NotFoundException("Comment not found with id " + comId));
    }
    public void validateUserByEmail(String email){
        if(userRepository.findByEmail(email)==null){
            throw new BadRequestException("We dont have user with this email");
        }
    }

    public User validateUsernameAndPassword(String username, String password) {
        User u = userRepository.findByUsername(username);
        if(u == null || !passwordEncoder.matches(password,u.getPassword())){
            throw new UnauthorizedException("Wrong credentials");
        }
        return u;
    }
    public void validateUserAndPostOwnership(Post post,long userId){
        if(post.getUser().getId()!=userId){
            throw new BadRequestException("You are not owner of this post");
        }
    }

    public void validateImageExtention(String extension) {
        if(!extension.matches("(jpe?g|png|gif|bmp)")){
            throw new BadRequestException("This file is not image");
        }
    }
    public void validateVideoExtention(String extension) {
        if(!extension.matches("(avi|AVI|wmv|WMV|flv|FLV|mpg|MPG|mp4|MP4)")){
            throw new BadRequestException("This file is not video");

        }
    }
}
