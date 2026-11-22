package live.craftmeet.user;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import live.craftmeet.user.dto.UpdateUsernameRequest;
import live.craftmeet.user.dto.UserResponse;
import live.craftmeet.user.service.UserService;
import live.craftmeet.user.utils.Constants;

@RestController
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable UUID id,
			@RequestAttribute(Constants.USER_ID_ATTRIBUTE) UUID userId) {
		userService.deleteUser(id, userId);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id,
			@RequestAttribute(Constants.USER_ID_ATTRIBUTE) UUID userId,
			@Valid @RequestBody UpdateUsernameRequest updateUsernameRequest) {
		UserResponse userResponse = userService.updateUsername(id, userId, updateUsernameRequest.newUsername());
		return ResponseEntity.ok(userResponse);
	}

}
