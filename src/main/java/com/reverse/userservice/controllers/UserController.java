package com.reverse.userservice.controllers;

import com.reverse.userservice.controllers.postobjects.UserEdit;
import com.reverse.userservice.models.Credentials;
import com.reverse.userservice.models.ReverseJWT;
import com.reverse.userservice.models.User;
import com.reverse.userservice.services.UserService;
import com.reverse.userservice.services.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.util.Optional;

@RestController
@RequestMapping(path = "users")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    //@Qualifier("validationServiceImpl")
    @Autowired
    @Setter
    private ValidationService valService;

    @Autowired
    @Setter
    private UserService userService;

    /**
     * createUser*/
    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createUser(@RequestBody User user) {
        logger.debug("Begin createUser");

        userService.createNewUser(user);

        logger.debug("End createUser successfully");
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> checkLoginCredentials(@RequestBody Credentials loginRequest) {

        try {
            ReverseJWT jwt = this.valService.validateCredentials(loginRequest);
            return ResponseEntity.ok().body(jwt);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping(value = "/validate")
    public ResponseEntity validateJwt(@RequestBody ReverseJWT jwt) {

        boolean status = this.valService.validateJwt(jwt);

        if(status) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(401).build();
    }

	@PostMapping(path = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity editUser(@RequestBody UserEdit userEdit) {
        logger.debug("Start editUser");

        ReverseJWT reverseJWT = userEdit.getReverseJWT();
        User user = userEdit.getUser();

        if (!(valService.validateJwt(reverseJWT, user.getId()))) {
            logger.warn("Bad JWT in editUser");
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        if (userService.getUserByID(user.getId()) != null) {
            userService.updateUser(user);
            logger.debug("End editUser successfully");
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            logger.warn("User not found in editUser");
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<User> getUserByID(@PathVariable Long id) {
        System.out.println("Controller is on");
        logger.debug("Start getUserByID");
        User user = userService.getUserByID(id);
        System.out.println(user);

        if (user != null) {
            logger.debug("End getUserByID successfully");
            return ResponseEntity.ok().body(user);
        } else {
            logger.debug("End getUserByID user not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path="/user/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username){
        User user = this.userService.getUserByUsername(username);
        if(user != null) {
            return ResponseEntity.ok().body(user);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
