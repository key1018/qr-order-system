package com.project.qr_order_system.controller;

import com.project.qr_order_system.dto.common.ApiRequest;
import com.project.qr_order_system.dto.common.ApiResponse;
import com.project.qr_order_system.dto.common.ApiResponseHelper;
import com.project.qr_order_system.dto.user.UserLoginRequestDto;
import com.project.qr_order_system.dto.user.UserLoginResponseDto;
import com.project.qr_order_system.dto.user.UserSignRequestDto;
import com.project.qr_order_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
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
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody ApiRequest<UserSignRequestDto> request){
        userService.signup(request.getData());
        return ApiResponseHelper.success("회원가입이 완료되었습니다");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponseDto>> login(@Valid @RequestBody ApiRequest<UserLoginRequestDto> request){
        UserLoginResponseDto responseDto = userService.login(request.getData());
        return ApiResponseHelper.success(responseDto, "로그인 성공");
    }
}
