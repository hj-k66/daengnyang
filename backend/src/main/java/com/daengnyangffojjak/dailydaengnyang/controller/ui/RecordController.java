package com.daengnyangffojjak.dailydaengnyang.controller.ui;


import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordWorkRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.repository.RecordRepository;
import com.daengnyangffojjak.dailydaengnyang.service.RecordService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;
    private final RecordRepository recordRepository;

    @GetMapping("/list")
    public String list(Model model, @PageableDefault(size = 2) Pageable pageable,
            @RequestParam(required = false, defaultValue = "") String searchText) {

//        Page<Record> records = postRepository.findByTitleContainingOrBodyContaining(searchText, searchText, pageable);
//        int startPage = Math.max(1, records.getPageable().getPageNumber() - 4);
//        int endPage = Math.min(records.getTotalPages(), records.getPageable().getPageNumber() + 4);
//        model.addAttribute("startPage", startPage);
//        model.addAttribute("endPage", endPage);
//        //records.getTotalElements();
//        model.addAttribute("records", records);
        return "diary/main_list";
    }

//    @GetMapping("/list")
//    public String list(Model model, @PageableDefault(size = 2) Pageable pageable,
//            @RequestParam(required = false, defaultValue = "") String searchText) {
//        // http://localhost:8080/board/list?page=0
//        // http://localhost:8080/board/list?page=0&size=1
//        // Page<Board> boards = boardRepository.findAll(pageable);
//        // http://localhost:8080/board/list?searchText=222
//        Page<Post> posts = postRepository.findByTitleContainingOrBodyContaining(searchText, searchText, pageable);
//        int startPage = Math.max(1, posts.getPageable().getPageNumber() - 4);
//        int endPage = Math.min(posts.getTotalPages(), posts.getPageable().getPageNumber() + 4);
//        model.addAttribute("startPage", startPage);
//        model.addAttribute("endPage", endPage);
//        //posts.getTotalElements();
//        model.addAttribute("posts", posts);
//        return "/posts/list";
//    }

    @GetMapping("/my")
    public String my() {

        return "diary/my_diary";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(new RecordWorkRequest());

        return "diary/add_diary";
    }

    @PostMapping("/add")
    public String submit(Long petId, RecordWorkRequest recordWorkRequest, Principal principal) {

        recordService.createRecord(petId, recordWorkRequest, principal.getName());
        return "redirect:/diary/main_list";
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
