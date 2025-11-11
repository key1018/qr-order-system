package com.project.qr_order_system.controller;

import com.project.qr_order_system.dto.user.UserSignRequestDto;
import com.project.qr_order_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qrorder/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserSignRequestDto requestDto){
        userService.signup(requestDto);
        return ResponseEntity.ok("회원가입 성공");
    }
}
