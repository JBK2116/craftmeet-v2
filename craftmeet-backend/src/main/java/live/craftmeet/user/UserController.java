package live.craftmeet.user;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

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

}
