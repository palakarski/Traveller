package com.example.travellerproject.services;

import com.example.travellerproject.exceptions.AuthenticationException;
import com.example.travellerproject.exceptions.BadRequestException;
import com.example.travellerproject.exceptions.NotFoundException;
import com.example.travellerproject.exceptions.UnauthorizedException;
import com.example.travellerproject.model.dto.MessageDTO;
import com.example.travellerproject.model.dto.user.*;
import com.example.travellerproject.model.pojo.PostCategory;
import com.example.travellerproject.model.pojo.User;
import com.example.travellerproject.repositories.PostCategoryRepository;
import com.example.travellerproject.repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Properties;

@Log4j2
@Service
public class UserService {
    @Autowired
    private Validator validator;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PostCategoryRepository postCategoryRepository;

        public User login(String username, String password){

                validator.validateUsername(username);
                validator.validPassword(password);
                return validator.validateUsernameAndPassword(username,password);

        }

        public User register(UserRegisterDTO dto){
            validator.validateUsername(dto.getUsername());
            validator.checkUsernameUnique(dto.getUsername());
            validator.validPassword(dto.getPassword());
            validator.matchPassAndConfPass(dto.getPassword(),dto.getConfpassword());
            validator.validEmail(dto.getEmail());
            validator.validateFirstnameAndLastName(dto.getFirstName(),dto.getLastName());
            validator.validateDateOfBirth(dto.getBirthDate());

            User u = modelMapper.map(dto,User.class);
            u.setPassword(passwordEncoder.encode(dto.getPassword()));
            u.setCreatedAt(LocalDateTime.now());
            userRepository.save(u);
            String recipient = "stefeanpvivan1998@gmail.com";
            String subject = "Registration ";
            String msg = "Your registration was successful. Welcome to Traveller";
            Thread thread = new Thread(() -> emailService.sendEmailNew(recipient, subject, msg));
            thread.start();
            return u;
        }


        public UserWithOutPassDTO getById(long id){
                User user = validator.validateUserAndGet(id);
                return modelMapper.map(user,UserWithOutPassDTO.class);

        }

        public UserWithOutPassDTO getByUserName(String username){
                User u = userRepository.findByUsername(username);
                if(u!=null){
                    UserWithOutPassDTO userWithOutPassDTO = new UserWithOutPassDTO(u);
                    return userWithOutPassDTO;
                }
                else{
                    throw new NotFoundException("User not found");
                }
        }

        public void deleteAcc(long id) {
                User u = userRepository.findById(id).orElseThrow(() ->new NotFoundException("User not found with id " + id) );
                userRepository.delete(u);
        }

        @Transactional
        public MessageDTO changePassword(long id, UserChangePasswordDTO changePasswordDTO) {
                User u = validator.validateUserAndGet(id);
                String newPassword = changePasswordDTO.getNewpassword();
                String newPasswordConfirmed = changePasswordDTO.getConfnewpassword();
                String oldPassword = changePasswordDTO.getOldpassword();

                if(!passwordEncoder.matches(oldPassword,u.getPassword())) {
                    throw new AuthenticationException("Old password doesnt match.");
                }

                validator.validPassword(newPassword);
                validator.matchPassAndConfPass(newPassword,newPasswordConfirmed);
                u.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(u);
                return new MessageDTO("Password was changed.");
        }


        @Transactional
        public MessageDTO forgottenPassword(UserForgottenPassDTO dto) {
            String email = dto.getEmail();
            validator.validateUserByEmail(email);
            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@$!%*#?&";
            String pwd = RandomStringUtils.random(8, characters)+"1!aA";
            User u = userRepository.findByEmail(email);
            u.setPassword(passwordEncoder.encode(pwd));
            userRepository.save(u);
            String recipient = "stefeanpvivan1998@gmail.com";
            String subject = "Password change!";
            String msg = "your new pass is :" + pwd;
            Thread thread = new Thread(() -> emailService.sendEmailNew(recipient, subject, msg));
            thread.start();
            return new MessageDTO("Password was changed. You can login with your new password");
        }

        @Transactional
        public MessageDTO follow(long userId, long subscribedForId) {
                User subscriber = validator.validateUserAndGet(userId);
                User subscribedFor = validator.validateUserAndGet(subscribedForId);
                if(subscriber.getFollowedUsers().contains(subscribedFor)){
                    throw new BadRequestException("Sorry you have already followed this user.");
                }
                subscriber.getFollowedUsers().add(subscribedFor);
                userRepository.save(subscriber);
                return new MessageDTO("You have subscribed for user with id " + subscribedForId);
        }

        @Transactional
        public MessageDTO unfollow(long userId, long subscribedForId) {
                User subscriber = validator.validateUserAndGet(userId);
                User subscribedFor = validator.validateUserAndGet(subscribedForId);
                if(!subscriber.getFollowedUsers().contains(subscribedFor)){
                    throw new BadRequestException("Sorry you dont follow this user.");
                }
                subscriber.getFollowedUsers().remove(subscribedFor);
                userRepository.save(subscriber);
                return new MessageDTO("You have unsubscribe for user with id " + subscribedForId);
        }

        @Transactional
        public UserWithOutPassDTO edit(long userId, EditUserDTO editUserDTO) {
                User u = validator.validateUserAndGet(userId);
                validator.validEmail(editUserDTO.getEmail());
                validator.validateFirstnameAndLastName(editUserDTO.getFirstName(),editUserDTO.getLastName());
                modelMapper.map(editUserDTO,u);
                userRepository.save(u);
                return modelMapper.map(u,UserWithOutPassDTO.class);
        }

        public MessageDTO createCategory(long userId, UserCreatesCategoryDTO categoryDTO){
            User user = userRepository.getById(userId);
            if (!user.isAdmin()){
                throw  new UnauthorizedException("You must be admin in order to create a category of posts");
            }
            if(categoryDTO.getCategoryType().isBlank()){
                throw new BadRequestException("Category must be valid");
            }
            PostCategory postCategory = modelMapper.map(categoryDTO,PostCategory.class);
            postCategoryRepository.save(postCategory);
            return new MessageDTO("New category with name "+categoryDTO.getCategoryType()+" was made");
        }
}
