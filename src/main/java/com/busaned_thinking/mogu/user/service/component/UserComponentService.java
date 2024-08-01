package com.busaned_thinking.mogu.user.service.component;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.busaned_thinking.mogu.user.controller.dto.request.UpdateUserRequest;
import com.busaned_thinking.mogu.user.controller.dto.request.UserRequest;
import com.busaned_thinking.mogu.user.controller.dto.response.UserResponse;

public interface UserComponentService {
	ResponseEntity<UserResponse> createUser(UserRequest userRequest);

	ResponseEntity<UserResponse> findUser(String userId);

	ResponseEntity<UserResponse> updateUser(String userId, UpdateUserRequest updateUserRequest);

	ResponseEntity<UserResponse> updateProfileImage(String userId, MultipartFile multipartFile);

	ResponseEntity<Void> deleteUser(String userId);
}