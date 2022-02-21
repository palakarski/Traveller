package com.example.travellerproject.services;

import com.example.travellerproject.exeptions.AuthenticationExeption;
import com.example.travellerproject.exeptions.BadRequestExeption;
import com.example.travellerproject.exeptions.NotFoundExeption;
import com.example.travellerproject.model.dto.MessageDTO;
import com.example.travellerproject.model.dto.user.ChangePasswordDTO;
import com.example.travellerproject.model.dto.user.UserRegisterDTO;
import com.example.travellerproject.model.dto.user.UserWithOutPassDTO;
import com.example.travellerproject.model.pojo.User;
import com.example.travellerproject.repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        public User login(String username, String password){
//            if(username== null || username.isBlank()){
//             throw new BadRequestExeption("Username is mandatory");
//            }
//
//            if(password == null|| password.isBlank()){
//                throw  new BadRequestExeption("Password is mandatory");
//            }
//            User u = userRepository.findByUsername(username);
//            if(u == null || !passwordEncoder.matches(password,u.getPassword())){
//                throw new UnauthorizedExeption("Wrong credentials");
//            }
//
//            return u;
            validator.validateUsername(username);
            validator.validPassword(password);
            return validator.validateUsernameAndPassword(username,password);

        }

        public User register(UserRegisterDTO dto){
            String username =  dto.getUsername();
            String password = dto.getPassword();
            String confpass = dto.getConfpassword();
            String firstname = dto.getFirstName();
            String lastname = dto.getLastName();
            LocalDate birthDate = dto.getBirthDate();
            LocalDateTime createdAt = dto.getCreatedAt();
            String email = dto.getEmail();
            //TODO register more validation
//            if(username == null || username.isBlank()){
//                throw new BadRequestExeption("Username is mandatory");
//            }
//            if(username.length()<5){
//                throw new BadRequestExeption("Username must be atleast 8 symbols");
//            }
//            if(password == null || password.isBlank()){
//                throw new BadRequestExeption("Password is mandatory");
//            }
//            if(userRepository.findByUsername(username)!=null){
//                throw new BadRequestExeption("Username is already taken.");
//            }
//            if(!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
//                throw new BadRequestExeption("Wrong email.");
//            }
//
//            if(!password.matches("^.*(?=.{8,})(?=.*\\d)(?=.*[a-zA-Z])|(?=.{8,})(?=.*\\d)(?=.*[!@#$%^&])|(?=.{8,})(?=.*[a-zA-Z])(?=.*[!@#$%^&]).*$")){
//                throw new BadRequestExeption("Password must contains at least 8 numbers and 2 charsequences");
//            }
//            if(!password.equals(confpass)){
//                throw new BadRequestExeption("Passwords doesnt match");
//            }
//            if(userRepository.findByEmail(email)!=null){
//                throw new BadRequestExeption("Email is already taken");
//            }
            validator.validateUsername(username);
            validator.checkUsernameUnique(username);
            validator.validPassword(password);
            validator.matchPassAndConfPass(password,confpass);
            validator.validEmail(email);
            validator.validateFirstnameAndLastName(firstname,lastname);
            validator.validateDateOfBirth(birthDate);

            User u = modelMapper.map(dto,User.class);
            u.setPassword(passwordEncoder.encode(password));
            userRepository.save(u);
            sendEmail("palakarski@gmail.com");
            return u;
        }


        public UserWithOutPassDTO getById(long id){
            User user = validator.validateUserAndGet(id);
            return modelMapper.map(user,UserWithOutPassDTO.class);
//            Optional<User> u = userRepository.findById(id);
//            if(u.isPresent()) {
//                UserWithOutPassDTO userWithOutPassDTO = new UserWithOutPassDTO(u.get());
//                return userWithOutPassDTO;
//            }
//            else{
//                throw new NotFoundExeption("User not found");
//            }
        }


        public UserWithOutPassDTO getByUserName(String username){
            User u = userRepository.findByUsername(username);
            if(u!=null){
                UserWithOutPassDTO userWithOutPassDTO = new UserWithOutPassDTO(u);
                return userWithOutPassDTO;
            }
            else{
                throw new NotFoundExeption("User not found");
            }
        }

    public void deleteAcc(long id) {
            User u = userRepository.findById(id).orElseThrow(() ->new NotFoundExeption("User not found with id " + id) );
            userRepository.delete(u);
    }

    public MessageDTO changePassword(long id, ChangePasswordDTO changePasswordDTO) {
        User u = validator.validateUserAndGet(id);
        String newpassword = changePasswordDTO.getNewpassword();
        String confnewpassword = changePasswordDTO.getConfnewpassword();
        String oldpassword = changePasswordDTO.getOldpassword();

        if(!passwordEncoder.matches(oldpassword,u.getPassword())) {
            throw new AuthenticationExeption("Oldpassword doesnt match");
        }
//        if(!newpassword.matches("^.*(?=.{8,})(?=.*\\d)(?=.*[a-zA-Z])|(?=.{8,})(?=.*\\d)(?=.*[!@#$%^&])|(?=.{8,})(?=.*[a-zA-Z])(?=.*[!@#$%^&]).*$")){
//            throw new BadRequestExeption("Password must contains at least 8 numbers and 2 charsequences");
//        }
//        if(!newpassword.equals(confnewpassword)){
//            throw new BadRequestExeption("Passwords need to match");
//        }
        validator.validPassword(newpassword);
        validator.matchPassAndConfPass(newpassword,confnewpassword);
        u.setPassword(passwordEncoder.encode(newpassword));
        userRepository.save(u);
        return new MessageDTO("Password was changed");
    }

    public MessageDTO forgottenPassword(HttpSession session, String email, String password, String repeatedNewPass) {
//            if(userRepository.findByEmail(email)==null){
//                throw new BadRequestExeption("We dont have user with this email");
//            }
//            if(!password.equals(repeatedNewPass)){
//                throw new BadRequestExeption("Password and confirm password doesnt match");
//            }
            validator.validateUserByEmail(email);
            validator.matchPassAndConfPass(password,repeatedNewPass);
            User u = userRepository.findByEmail(email);
            u.setPassword(passwordEncoder.encode(password));
            userRepository.save(u);
            return new MessageDTO("Password was changed you can login now");

    }

        public MessageDTO follow(long userId, long id) {
            User subcriber = userRepository.findById(userId).orElseThrow(()-> new NotFoundExeption("No such a user"));;
            User subscribedFor = userRepository.findById(id).orElseThrow(()-> new NotFoundExeption("No such a user"));
            if(subcriber.getFollowedUsers().contains(subscribedFor)){
                throw new BadRequestExeption("Sorry you have already followed this user.");
            }
            subcriber.getFollowedUsers().add(subscribedFor);
            userRepository.save(subcriber);
            return new MessageDTO("You have subscribe for " + subscribedFor);
        }

    public MessageDTO unfollow(long userId, long id) {
        User subcriber = userRepository.findById(userId).orElseThrow(()-> new NotFoundExeption("No such a user"));
        User subscribedFor = userRepository.findById(id).orElseThrow(()-> new NotFoundExeption("No such a user"));
        if(!subcriber.getFollowedUsers().contains(subscribedFor)){
            throw new BadRequestExeption("Sorry you dont follow this user.");
        }
        subcriber.getFollowedUsers().remove(subscribedFor);
        userRepository.save(subcriber);
        return new MessageDTO("You have unsubscribe for " + subscribedFor);
    }
    private void sendEmail(String recepient){
        Properties properties = new Properties();

        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.auth","true");

        String myAccount = "stefeanpvivan1998@gmail.com";
        String password = "xxxxxxx";

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccount,password);
            }
        });

        Message message = prepareMessage(session,myAccount,recepient);
        try {
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("greshka pri prashtane na email");
        }
    }
    private static Message prepareMessage(Session session,String myAccount,String recepient){
            Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(myAccount));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
            message.setSubject("Traveller registration");
            //tuk se promenq tova koeto shte sadarja emaila
            message.setText("Welcome to Traveller registration successful. evala na tiq momcheta aide i lokaciq sa slojili ;D");
            return message;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
