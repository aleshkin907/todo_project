package com.project.todo.controllers;

import com.project.todo.dto.AuthResponseDto;
import com.project.todo.models.Role;
import com.project.todo.models.UserModel;
import com.project.todo.repository.RoleRepository;
import com.project.todo.repository.UserRepository;
import com.project.todo.security.JWTGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

//@RestController
//@RequestMapping("api/auth")
@Controller
public class AuthController {
    private UserRepository userRepository;
    private AuthenticationManager authenticationManager;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JWTGenerator jwtGenerator;

    @Autowired
    public AuthController(UserRepository userRepository, AuthenticationManager authenticationManager,
                          RoleRepository roleRepository, PasswordEncoder passwordEncoder,JWTGenerator jwtGenerator ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }


    @RequestMapping("api/auth/registration")
    public ResponseEntity<String> registration(@RequestBody UserModel usr){
        if(userRepository.existsByUsername(usr.getUsername())){
            return new ResponseEntity<>("username is taken", HttpStatus.BAD_REQUEST);
        }

        UserModel userModel = new UserModel();
        userModel.setUsername(usr.getUsername());
        userModel.setPassword(passwordEncoder.encode(usr.getPassword()));

        Role roles = roleRepository.findByName("USER").get();
        userModel.setRoles(Collections.singletonList(roles));

        userRepository.save(userModel);

        return new ResponseEntity<>("User registered", HttpStatus.OK);
    }

    @PostMapping("api/auth/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody UserModel user){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        return new ResponseEntity<>(new AuthResponseDto(token), HttpStatus.OK);
    }

    @GetMapping("api/admin/users")
    public ResponseEntity<List<UserModel>> getUsers(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size){
        Page<UserModel> users = this.userRepository.findAll(PageRequest.of(page,size));
        return ResponseEntity.ok(users.getContent());
    }

}
