package com.daengnyangffojjak.dailydaengnyang.controller.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserUiController {

    @GetMapping("/login")
    public String login() {
        return "users/login";
    }

    @GetMapping("/join")
    public String join() {
        return "users/login";
    }


}
