package live.craftmeet.meeting;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import live.craftmeet.meeting.dto.MeetingRequest;
import live.craftmeet.meeting.dto.MeetingResponse;
import live.craftmeet.meeting.service.MeetingService;
import live.craftmeet.user.utils.Constants;

@RestController
public class MeetingController {

	private final MeetingService meetingService;

	public MeetingController(MeetingService meetingService) {
		this.meetingService = meetingService;
	}

	@PostMapping("/meetings")
	public ResponseEntity<MeetingResponse> createMeeting(
			@Valid @RequestBody MeetingRequest meetingRequest,
			@RequestAttribute(Constants.USER_ID_ATTRIBUTE) UUID userId) {
		MeetingResponse meetingResponse = meetingService.create(meetingRequest, userId);
		return ResponseEntity.status(HttpStatus.CREATED).body(meetingResponse);
	}
}
