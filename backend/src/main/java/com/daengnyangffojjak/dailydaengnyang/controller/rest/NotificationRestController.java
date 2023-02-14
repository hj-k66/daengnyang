package com.daengnyangffojjak.dailydaengnyang.controller.rest;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.Response;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.notification.NotificationDeleteResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.notification.NotificationListResponse;
import com.daengnyangffojjak.dailydaengnyang.domain.dto.notification.NotificationReadResponse;
import com.daengnyangffojjak.dailydaengnyang.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationRestController {

	private final NotificationService notificationService;

	//알림 읽기
	@PutMapping(value = "/{notificationId}")
	public ResponseEntity<Response<NotificationReadResponse>> readNotification(@PathVariable Long notificationId, @AuthenticationPrincipal UserDetails user) {
		NotificationReadResponse notificationReadResponse= notificationService.readNotification(notificationId,user.getUsername());
		return ResponseEntity.ok()
				.body(Response.success(notificationReadResponse));

	}

	//알림 삭제
	@DeleteMapping(value = "/{notificationId}")
	public ResponseEntity<Response<NotificationDeleteResponse>> deleteSchedule(
			@PathVariable Long notificationId, @AuthenticationPrincipal UserDetails user) {
		NotificationDeleteResponse notificationDeleteResponse = notificationService.delete(
				notificationId,
				user.getUsername());
		return ResponseEntity.ok().body(Response.success(notificationDeleteResponse));
	}

	//알람 전체 조회
	//무한 스크롤
	@GetMapping
	public Response<NotificationListResponse> getAllNotificationByLastPage(
			@RequestParam Long lastNotificationId, @RequestParam int size,
			@AuthenticationPrincipal UserDetails user) {
		NotificationListResponse notificationListResponse = notificationService.getAllNotification(
				lastNotificationId, size, user.getUsername());
		return Response.success(notificationListResponse);
	}

	@PostMapping
	public ResponseEntity register(@RequestBody String token,
			@AuthenticationPrincipal UserDetails user) {
		notificationService.registerFCMToken(user.getUsername(), token);
		return ResponseEntity.ok().build();
	}


}
