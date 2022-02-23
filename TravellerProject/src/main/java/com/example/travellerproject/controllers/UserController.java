package com.example.travellerproject.controllers;
import com.example.travellerproject.exceptions.BadRequestException;
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


    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SessionValidator sessionValidator;

        @GetMapping(value = "/users/{id}")
        public ResponseEntity<UserWithOutPassDTO> getById(@PathVariable long id){
            return ResponseEntity.ok(userService.getById(id));
        }

        @GetMapping(value = "/users/find/{username}")
        public ResponseEntity<UserWithOutPassDTO> getByusername(@PathVariable String username){
            return ResponseEntity.ok(userService.getByUserName(username));
        }


        @PostMapping(value = "/register")
        public ResponseEntity<UserWithOutPassDTO> register(@RequestBody UserRegisterDTO user,HttpSession session){
            sessionValidator.isAlreadyLogged(session);
            User u = userService.register(user);
            UserWithOutPassDTO userWithOutPassDTO = modelMapper.map(u,UserWithOutPassDTO.class);
            return  ResponseEntity.ok(userWithOutPassDTO);
        }
        @PostMapping(value = "/login")
        public ResponseEntity<UserWithOutPassDTO> login(@RequestBody UserSignInDTO user, HttpSession session){
        String username = user.getUsername();
        String password = user.getPassword();
        sessionValidator.isAlreadyLogged(session);
        User u = userService.login(username,password);
        sessionValidator.userLogsIn(session,u.getId());

        return  ResponseEntity.ok(new UserWithOutPassDTO(u));
        }
        @PostMapping(value ="/logout")
        public MessageDTO logout(HttpSession session){
            sessionValidator.isUserLogedIn(session);
            sessionValidator.userLogsOut(session);
            return new MessageDTO("You have logged out");

        }

        @PostMapping(value ="/user/edit")
        public ResponseEntity<UserWithOutPassDTO> Edit(@RequestBody EditUserDTO editUserDTO,HttpSession session){
            long userId = sessionValidator.isUserLogedIn(session);
            return  ResponseEntity.ok(userService.edit(userId,editUserDTO));
        }


        @DeleteMapping (value = "/delete")
        public MessageDTO deleteAcc(HttpSession session){
        long id = sessionValidator.isUserLogedIn(session);
        userService.deleteAcc(id);
        return new MessageDTO("Account has been deleted");
        }
        @PutMapping(value = "/changepass")
        public MessageDTO changePass(HttpSession session, @RequestBody UserChangePasswordDTO changePasswordDTO){
            long id = sessionValidator.isUserLogedIn(session);
            return userService.changePassword(id,changePasswordDTO);

        }
        @PutMapping(value = "/forgotten_password")
        public MessageDTO forgottenPass(HttpSession session, @RequestBody UserForgottenPassDTO forgottenPassDTO){

                sessionValidator.isAlreadyLogged(session);

                return userService.forgottenPassword(session, forgottenPassDTO);
        }


        @PostMapping(value = "/user/{id}/follow")
        public MessageDTO follow(@PathVariable long id,HttpSession session){
            long userId = sessionValidator.isUserLogedIn(session);
            if(id==userId){
                throw new BadRequestException("You can't subscribe for your own profile");
            }
            return userService.follow(userId,id);

        }
        @PostMapping(value = "/user/{id}/unfollow")
        public MessageDTO unfollow(@PathVariable("id") long id,HttpSession session){
            long userId = sessionValidator.isUserLogedIn(session);
            if(id==userId){
                throw new BadRequestException("You can't unsubscribe for your own profile");
            }
            return userService.unfollow(userId,id);

        }



}
