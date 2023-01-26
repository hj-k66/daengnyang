package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.*;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserJoinResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserLoginRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.user.UserLoginResponse;
import com.daengnyangffojjak.dailydaengnyang.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserRestController {
    private final UserService userService;

    @PostMapping(value = "/join")       //회원가입
    public ResponseEntity<Response<UserJoinResponse>> join(@RequestBody UserJoinRequest request){
        UserJoinResponse userJoinResponse = userService.join(request);
        return ResponseEntity.created(URI.create("/api/v1/users/"+userJoinResponse.getId()))     //성공 시 상태코드 : 201
                .body(Response.success(userJoinResponse));
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Response<UserLoginResponse>> login(@RequestBody @Valid UserLoginRequest userLoginRequest){
        userService.login(userLoginRequest);
    }

    @GetMapping(value = "/test")
    public Map<String, String> test(){
        return new HashMap<>(){{put("test", "ok");}};
    }
}
