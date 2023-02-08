package com.daengnyangffojjak.dailydaengnyang.controller.ui;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping
@RequiredArgsConstructor
public class DiaryController {

    @GetMapping("/list")
    public String list() {

        return "diary/main_list";
    }

    @GetMapping("/my")
    public String my() {

        return "diary/my_diary";
    }

    @GetMapping("/add")
    public String add() {
        return "diary/add_diary";
    }

    @GetMapping("/modify")
    public String modify() {
        return "diary/modify_diary";
    }

    @GetMapping("/alarm")
    public String alarm() {
        return "diary/alarm";
    }

}
