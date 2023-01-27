package com.daengnyangffojjak.dailydaengnyang.controller.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupUiController {

    @PostMapping
    public String groupCreate() {
        return "users/join_group";
    }

}
