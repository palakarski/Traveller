package com.example.travellerproject.controllers;

import com.example.travellerproject.exeptions.BadRequestExeption;
import com.example.travellerproject.model.dto.MessageDTO;
import com.example.travellerproject.model.dto.user.*;
import com.example.travellerproject.model.pojo.User;
import com.example.travellerproject.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
public class UserController {
    private static final String LOGGED ="logged";
    private static final String LOGGED_IN = "logged_in";
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;

        @GetMapping(value = "/users/{id}")
        public ResponseEntity<UserWithOutPassDTO> getById(@PathVariable long id){
            return ResponseEntity.ok(userService.getById(id));
        }

        @GetMapping(value = "/users/find/{username}")
        public ResponseEntity<UserWithOutPassDTO> getByusername(@PathVariable String username){
            return ResponseEntity.ok(userService.getByUserName(username));
        }


        @PostMapping(value = "/register")
        public ResponseEntity<UserWithOutPassDTO> register(@RequestBody UserRegisterDTO user){
            User u = userService.register(user);
            UserWithOutPassDTO userWithOutPassDTO = modelMapper.map(u,UserWithOutPassDTO.class);
            return  ResponseEntity.ok(userWithOutPassDTO);
        }
        @PostMapping(value = "/login")
        public ResponseEntity<UserWithOutPassDTO> login(@RequestBody UserSignInDTO user, HttpSession session){
        String username = user.getUsername();
        String password = user.getPassword();
            if(!session.isNew()&&session.getAttribute(LOGGED)!=null){
                throw new BadRequestExeption("You are already logged in.");
            }
        User u = userService.login(username,password);
        session.setAttribute(LOGGED,u.getId());

        return  ResponseEntity.ok(new UserWithOutPassDTO(u));
        }
        @PostMapping(value ="/logout")
        public MessageDTO logout(HttpSession session){
            if(session.isNew() || session.getAttribute(LOGGED)==null){
                throw new BadRequestExeption("You must be logged first");
            }
            session.setAttribute(LOGGED,null);
            return new MessageDTO("You have logged out");

        }
        @DeleteMapping (value = "/delete")
        public MessageDTO deleteAcc(HttpSession session){
        if(session.isNew()||session.getAttribute(LOGGED)==null){
            throw new BadRequestExeption("You need to be logged first");
        }
        long id = (Long)session.getAttribute(LOGGED);
        userService.deleteAcc(id);
        return new MessageDTO("Account has been deleted");
        }
        @PutMapping(value = "/changepass")
        public MessageDTO changePass(HttpSession session, @RequestBody ChangePasswordDTO changePasswordDTO){
            String newpassword = changePasswordDTO.getNewpassword();
            String confnewpassword = changePasswordDTO.getConfnewpassword();
            String oldpassword = changePasswordDTO.getOldpassword();
            if(session.isNew()||session.getAttribute(LOGGED)==null){
                throw new BadRequestExeption("You need to be logged first");
            }
            long id = (Long)session.getAttribute(LOGGED);
            if(!newpassword.equals(confnewpassword)){
                throw new BadRequestExeption("Passwords need to match");
            }
            if(!newpassword.matches("^.*(?=.{8,})(?=.*\\d)(?=.*[a-zA-Z])|(?=.{8,})(?=.*\\d)(?=.*[!@#$%^&])|(?=.{8,})(?=.*[a-zA-Z])(?=.*[!@#$%^&]).*$")){
                throw new BadRequestExeption("Password must contains at least 8 numbers and 2 charsequences");
            }
            return userService.changePassword(id,oldpassword,newpassword);

        }
        @PutMapping(value = "/forgotten_password")
        public MessageDTO forgottenPass(HttpSession session, @RequestBody ForgottenPassDTO forgottenPassDTO){
            String email = forgottenPassDTO.getEmail();
            String password = forgottenPassDTO.getNewpassword();
            String confpassword = forgottenPassDTO.getConfnewpassword();
            if(!session.isNew()&&session.getAttribute(LOGGED)!=null){
                throw new BadRequestExeption("You are already logged in.");
            }

            return userService.forgottenPassword(session,email,password,confpassword);
        }

//        @PostMapping(value = "/user/{id}/follow")
//        public MessageDTO follow(@PathVariable("id") long id,HttpSession session){
//            if(session.isNew()||session.getAttribute(LOGGED)==null){
//                throw new BadRequestExeption("You need to be logged first");
//            }
//            long userId = (Long)session.getAttribute(LOGGED);
//            if(id==userId){
//                throw new BadRequestExeption("You cant subscribe for your own profile");;
//            }
//            return userService.follow(userId,id);
//
//        }



}
