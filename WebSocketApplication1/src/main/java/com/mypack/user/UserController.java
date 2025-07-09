package com.mypack.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class UserController {

    @Autowired
    public UserService userService;
    @Autowired
    public UserRepository userRepository;

    @MessageMapping("/user.addUser")
    @SendTo("/topic/online-users")
    public User addUser(@Payload User user) {
        userService.saveUser(user);
        return user;
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/topic/online-users")
    public User disconnectUser(@Payload User user) {
        userService.disconnectedUser(user.getNickName());
        return user;
    }


    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userRepository.findAllByStatus(Status.ONLINE);
        return ResponseEntity.ok(users);
    }
}

