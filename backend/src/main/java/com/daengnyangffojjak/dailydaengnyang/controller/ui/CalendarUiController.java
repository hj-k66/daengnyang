package com.daengnyangffojjak.dailydaengnyang.controller.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class CalendarUiController {

    @GetMapping("/calendar/groups/{groupId}")
    public String calendar(@PathVariable Integer groupId) {
        return "calendar/calendar";
    }

}
