package com.daengnyangffojjak.dailydaengnyang.controller.ui;


import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordWorkRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Pet;
import com.daengnyangffojjak.dailydaengnyang.domain.entity.Record;
import com.daengnyangffojjak.dailydaengnyang.repository.RecordRepository;
import com.daengnyangffojjak.dailydaengnyang.service.RecordService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;
    private final RecordRepository recordRepository;

    @GetMapping("/list")
    public String list(Model model, @PageableDefault(size = 3) Pageable pageable,
            @RequestParam(required = false, defaultValue = "") String searchText) {
        Page<Record> records = recordRepository.findByTitleContainingOrBodyContaining(searchText, searchText, pageable);
        int startPage = Math.max(1, records.getPageable().getPageNumber() - 5);
        int endPage = Math.min(records.getTotalPages(), records.getPageable().getPageNumber() + 5);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("records", records);

        Page<RecordResponse> recordList = recordService.getAllRecords(pageable);
        model.addAttribute("list", recordList);
        return "record/main_list";
    }


    @GetMapping("/pets/{petId}/records/{recordId}")
    public String my(@PathVariable Long petId, @PathVariable Long recordId,  RecordResponse recordResponse, Model model
    , Authentication authentication) {
        RecordResponse response = recordService.getOneRecord(petId, recordId, authentication.getName());
//        model.addAttribute("petId", petId);
//        model.addAttribute("recordId", recordId);
        model.addAttribute("record", response);

        return "record/my_record";
    }

    @GetMapping("/view/add")
    public String add(Model model, Record record) {
        model.addAttribute(new RecordWorkRequest());
//        model.addAttribute("petId", pet.getId());
//        model.addAttribute("petId",record.getPet().getId());

        return "record/add_record";
    }

    @ResponseBody
    @PostMapping("/add")
    public String add(Long petId, RecordWorkRequest recordWorkRequest, Authentication authentication) {
        recordService.createRecord(petId, recordWorkRequest, authentication.getName());
        return "redirect:/record/main_list";
    }
    @GetMapping("/modify")
    public String modify() {
        return "record/modify_record";
    }

    @GetMapping("/alarm")
    public String alarm() {
        return "record/alarm";
    }

}
