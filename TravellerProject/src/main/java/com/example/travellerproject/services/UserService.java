package com.example.travellerproject.services;

import com.example.travellerproject.exeptions.AuthenticationExeption;
import com.example.travellerproject.exeptions.BadRequestExeption;
import com.example.travellerproject.exeptions.NotFoundExeption;
import com.example.travellerproject.exeptions.UnauthorizedExeption;
import com.example.travellerproject.model.dto.MessageDTO;
import com.example.travellerproject.model.dto.user.UserRegisterDTO;
import com.example.travellerproject.model.dto.user.UserWithOutPassDTO;
import com.example.travellerproject.model.pojo.User;
import com.example.travellerproject.repositories.UserRepository;
import org.apache.tomcat.websocket.AuthenticationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;

        public User login(String username, String password){
        if(username== null || username.isBlank()){
         throw new BadRequestExeption("Username is mandatory");
        }
        if(password == null|| password.isBlank()){
            throw  new BadRequestExeption("Password is mandatory");
        }
        User u = userRepository.findByUsername(username);
        if(u == null && !passwordEncoder.matches(password,u.getPassword())){
            throw new UnauthorizedExeption("Wrong credentials");
        }
        return u;
    }

        public User register(UserRegisterDTO dto){
            String username =  dto.getUsername();
            String password = dto.getPassword();
            String confpass = dto.getConfpassword();
            String email = dto.getEmail();
            if(username == null || username.isBlank()){
                throw new BadRequestExeption("Username is mandatory");
            }
            if(username.length()<5){
                throw new BadRequestExeption("Username must be atleast 8 symbols");
            }
            if(password == null || password.isBlank()){
                throw new BadRequestExeption("Password is mandatory");
            }
            if(userRepository.findByUsername(username)!=null){
                throw new BadRequestExeption("Username is already taken.");
            }
            //TODO
            if(!password.matches("^.*(?=.{8,})(?=.*\\d)(?=.*[a-zA-Z])|(?=.{8,})(?=.*\\d)(?=.*[!@#$%^&])|(?=.{8,})(?=.*[a-zA-Z])(?=.*[!@#$%^&]).*$")){
                throw new BadRequestExeption("Password must contains at least 8 numbers and 2 charsequences");
            }
            if(!password.equals(confpass)){
                throw new BadRequestExeption("Passwords doesnt match");
            }
            if(userRepository.findByEmail(email)!=null){
                throw new BadRequestExeption("Email is already taken");
            }

            User u = modelMapper.map(dto,User.class);
            u.setPassword(passwordEncoder.encode(password));
            userRepository.save(u);
            return u;
        }

        ///
        public UserWithOutPassDTO getById(long id){
            Optional<User> u = userRepository.findById(id);
            if(u.isPresent()) {
                UserWithOutPassDTO userWithOutPassDTO = new UserWithOutPassDTO(u.get());
                return userWithOutPassDTO;
            }
            else{
                throw new NotFoundExeption("User not found");
            }
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
            User u = userRepository.getById(id);
            userRepository.delete(u);
    }

    public MessageDTO changePassword(long id, String oldpassword, String newpassword) {
        User u = userRepository.getById(id);
        if(!passwordEncoder.matches(oldpassword,u.getPassword())) {
            throw new AuthenticationExeption("Oldpassword doesnt match");
        }
        u.setPassword(passwordEncoder.encode(newpassword));
        userRepository.save(u);
        return new MessageDTO("Password was changed");
    }

    public MessageDTO forgottenPassword(HttpSession session, String email, String password, String repeatedNewPass) {
            if(userRepository.findByEmail(email)==null){
                throw new BadRequestExeption("We dont have user with this email");
            }
            if(!password.equals(repeatedNewPass)){
                throw new BadRequestExeption("Password and confirm password doesnt match");
            }
            User u = userRepository.findByEmail(email);
            u.setPassword(passwordEncoder.encode(password));
            userRepository.save(u);
            return new MessageDTO("Password was changed you can login now");

    }

//    public MessageDTO follow(long userId, long id) {
//            User subcriber = userRepository.getById(userId);
//            User subscribedFor = userRepository.getById(id);
//
//    }
}
