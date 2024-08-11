package com.bunsaned3thinking.mogu.user.service.module;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bunsaned3thinking.mogu.common.util.UpdateUtil;
import com.bunsaned3thinking.mogu.user.controller.dto.request.UpdateUserPasswordRequest;
import com.bunsaned3thinking.mogu.user.controller.dto.request.UpdateUserRequest;
import com.bunsaned3thinking.mogu.user.controller.dto.request.UserRequest;
import com.bunsaned3thinking.mogu.user.controller.dto.response.UserResponse;
import com.bunsaned3thinking.mogu.user.entity.User;
import com.bunsaned3thinking.mogu.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public ResponseEntity<UserResponse> createUser(UserRequest userRequest) {
		User user = userRequest.toEntity();
		user.updatePassword(bCryptPasswordEncoder.encode(user.getPassword()));
		User savedUser = userRepository.save(user);
		return ResponseEntity.status(HttpStatus.CREATED)
			.contentType(MediaType.APPLICATION_JSON)
			.body(UserResponse.from(savedUser));
	}

	@Override
	public ResponseEntity<UserResponse> findUser(String userId) {
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new EntityNotFoundException("[Error] 사용자를 찾을 수 없습니다."));
		return ResponseEntity.status(HttpStatus.OK)
			.contentType(MediaType.APPLICATION_JSON)
			.body(UserResponse.from(user));
	}

	@Override
	public boolean checkUser(String userId) {
		return userRepository.existsByUserId(userId);
	}

	@Override
	public ResponseEntity<UserResponse> updateUser(String userId, UpdateUserRequest updateUserRequest) {
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new EntityNotFoundException("[Error] 사용자를 찾을 수 없습니다."));
		UpdateUserRequest originUser = UpdateUserRequest.from(user);
		UpdateUtil.copyNonNullProperties(updateUserRequest, originUser);
		update(user, originUser);
		User updatedUser = userRepository.save(user);
		return ResponseEntity.status(HttpStatus.OK)
			.contentType(MediaType.APPLICATION_JSON)
			.body(UserResponse.from(updatedUser));
	}

	@Override
	public ResponseEntity<UserResponse> updatePassword(String userId,
		UpdateUserPasswordRequest updateUserPasswordRequest) {
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new EntityNotFoundException("[Error] 사용자를 찾을 수 없습니다."));
		user.updatePassword(bCryptPasswordEncoder.encode(updateUserPasswordRequest.getPassword()));
		User savedUser = userRepository.save(user);
		return ResponseEntity.status(HttpStatus.OK)
			.contentType(MediaType.APPLICATION_JSON)
			.body(UserResponse.from(savedUser));
	}

	@Override
	public ResponseEntity<UserResponse> setBlockUser(String userId, boolean state) {
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new EntityNotFoundException("[Error] 사용자를 찾을 수 없습니다."));
		if (user.getIsBlock() == state) {
			throw new IllegalArgumentException("[Error] 이미 해당 상태의 사용자입니다.");
		}
		user.block(state);
		User savedUser = userRepository.save(user);
		return ResponseEntity.status(HttpStatus.OK)
			.contentType(MediaType.APPLICATION_JSON)
			.body(UserResponse.from(savedUser));
	}

	@Override
	public ResponseEntity<UserResponse> updateProfileImage(String userId, @NonNull String profileImage) {
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new EntityNotFoundException("[Error] 사용자를 찾을 수 없습니다."));
		user.updateProfileImage(profileImage);
		User updatedUser = userRepository.save(user);
		return ResponseEntity.status(HttpStatus.OK)
			.contentType(MediaType.APPLICATION_JSON)
			.body(UserResponse.from(updatedUser));
	}

	private void update(User user, UpdateUserRequest updateUserRequest) {
		String nickname = updateUserRequest.getNickname();
		Double longitude = updateUserRequest.getLongitude();
		Double latitude = updateUserRequest.getLatitude();
		Short distanceMeters = updateUserRequest.getDistanceMeters();
		user.update(nickname, longitude, latitude, distanceMeters);
	}

	@Override
	public ResponseEntity<Void> deleteUser(String userId) {
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new EntityNotFoundException("[Error] 사용자를 찾을 수 없습니다."));
		userRepository.deleteById(user.getUid());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
