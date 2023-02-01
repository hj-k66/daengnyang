package com.daengnyangffojjak.dailydaengnyang.controller.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view")
@RequiredArgsConstructor
public class CalendarUiController {

    @GetMapping("/calendar/groups/{groupId}")
    public String calendar(@PathVariable Integer groupId) {
        return "calendar/calendar";
    }

}
