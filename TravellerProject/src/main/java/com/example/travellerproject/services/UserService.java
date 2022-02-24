package com.example.travellerproject.services;

import com.example.travellerproject.exceptions.AuthenticationException;
import com.example.travellerproject.exceptions.BadRequestException;
import com.example.travellerproject.exceptions.NotFoundException;
import com.example.travellerproject.model.dto.MessageDTO;
import com.example.travellerproject.model.dto.user.*;
import com.example.travellerproject.model.pojo.User;
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
                //star nachin
                emailService.sendEmail("stefeanpvivan1998@gmail.com","REGISTRATION", "Welcome to traveller");
                //nov nachin
                //emailService.sendEmail("stefeanpvivan1998@gmail.com","Traveller Registration", "WELCOME");
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
                String newpassword = changePasswordDTO.getNewpassword();
                String confnewpassword = changePasswordDTO.getConfnewpassword();
                String oldpassword = changePasswordDTO.getOldpassword();

                if(!passwordEncoder.matches(oldpassword,u.getPassword())) {
                    throw new AuthenticationException("Oldpassword doesnt match.");
                }

                validator.validPassword(newpassword);
                validator.matchPassAndConfPass(newpassword,confnewpassword);
                u.setPassword(passwordEncoder.encode(newpassword));
                userRepository.save(u);
                return new MessageDTO("Password was changed.");
        }


        @Transactional
        public MessageDTO forgottenPassword(HttpSession session, UserForgottenPassDTO dto) {
                validator.validateUserByEmail(dto.getEmail());

                String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";
                String pwd = RandomStringUtils.random(8, characters);
                User u = userRepository.findByEmail(dto.getEmail());
                u.setPassword(passwordEncoder.encode(pwd));
                userRepository.save(u);
                emailService.sendEmail("stefeanpvivan1998@gmail.com", "passwordChange", "your new pass is : " + pwd);
                    return new MessageDTO("Password was changed you can login now.");
        }

        @Transactional
        public MessageDTO follow(long userId, long subcribedForId) {
                User subcriber = validator.validateUserAndGet(userId);
                User subscribedFor = validator.validateUserAndGet(subcribedForId);
                if(subcriber.getFollowedUsers().contains(subscribedFor)){
                    throw new BadRequestException("Sorry you have already followed this user.");
                }
                subcriber.getFollowedUsers().add(subscribedFor);
                userRepository.save(subcriber);
                return new MessageDTO("You have subscribe for user with id " + subcribedForId);
        }

        @Transactional
        public MessageDTO unfollow(long userId, long subcribedForId) {
                User subcriber = validator.validateUserAndGet(userId);
                User subscribedFor = validator.validateUserAndGet(subcribedForId);
                if(!subcriber.getFollowedUsers().contains(subscribedFor)){
                    throw new BadRequestException("Sorry you dont follow this user.");
                }
                subcriber.getFollowedUsers().remove(subscribedFor);
                userRepository.save(subcriber);
                return new MessageDTO("You have unsubscribe for user with id " + subcribedForId);
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
    }
