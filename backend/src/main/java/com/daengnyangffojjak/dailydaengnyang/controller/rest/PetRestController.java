package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetAddRequest;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetResultResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.pet.PetShowResponse;
import com.daengnyangffojjak.dailydaengnyang.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class PetRestController {

    private final PetService petService;

    /** pet 등록하기 **/
    @PostMapping("/{groupId}/pets")
    public ResponseEntity<Response<PetResultResponse>> addPet(@PathVariable Long groupsId,
                                                              @RequestBody PetAddRequest groupAddRequest,
                                                              Authentication authentication) {
        return ResponseEntity.ok().body(Response.success(petService.add(groupsId, groupAddRequest, authentication)));
    }

    /** pet 조회 **/
    @GetMapping("/{groupId}/pets")
    public ResponseEntity<Response<Page<PetShowResponse>>> showAllPets(
            @PathVariable Long groupId,
            @PageableDefault(size = 20)
            @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok().body(Response.success(petService.showAll(groupId, pageable)));
    }

    /** pet 수정 **/
    @PutMapping("/{groupId}/pets/{id}")
    public ResponseEntity<Response<PetResultResponse>> modifyPet(@PathVariable Long groupId,
                                                                 @PathVariable Long id,
                                                                 @RequestBody PetAddRequest petAddRequest,
                                                                 Authentication authentication) {
        return ResponseEntity.ok()
                .body(Response.success(petService.modify(groupId, id, petAddRequest ,authentication)));
    }

    /** pet 삭제 **/
    @DeleteMapping("/{groupId}/pets/{id}")
    public ResponseEntity<Response<PetDeleteResponse>> deletePet(@PathVariable Long groupId,
                                                                 @PathVariable Long id,
                                                                 Authentication authentication) {
        return ResponseEntity.ok().body(Response.success(petService.delete(groupId, id, authentication)));
    }
}

