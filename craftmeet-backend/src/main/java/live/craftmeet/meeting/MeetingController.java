package live.craftmeet.meeting;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import live.craftmeet.meeting.dto.meeting.MeetingRequest;
import live.craftmeet.meeting.dto.meeting.MeetingResponse;
import live.craftmeet.meeting.dto.stat.StatLifeTimeResponse;
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

	@PutMapping("/meetings/{id}")
	public ResponseEntity<MeetingResponse> updateMeeting(@Valid @RequestBody MeetingRequest meetingRequest,
			@PathVariable("id") UUID id, @RequestAttribute(Constants.USER_ID_ATTRIBUTE) UUID userId) {
		MeetingResponse meetingResponse = meetingService.update(meetingRequest, id, userId);
		return ResponseEntity.ok(meetingResponse);
	}

	@DeleteMapping("/meetings/{id}")
	public ResponseEntity<Void> deleteMeeting(@PathVariable("id") UUID id,
			@RequestAttribute(Constants.USER_ID_ATTRIBUTE) UUID userId) {
		meetingService.deleteMeeting(id, userId);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/meetings")
	public ResponseEntity<Void> deleteMeetings(@RequestAttribute(Constants.USER_ID_ATTRIBUTE) UUID userId) {
		meetingService.deleteMeetings(userId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/meetings/{id}")
	public ResponseEntity<MeetingResponse> getMeeting(@PathVariable("id") UUID id,
			@RequestAttribute(Constants.USER_ID_ATTRIBUTE) UUID userId) {
		MeetingResponse meetingResponse = meetingService.getMeeting(id, userId);
		return ResponseEntity.ok(meetingResponse);
	}

	@GetMapping("/meetings")
	public ResponseEntity<List<MeetingResponse>> getMeetings(
			@RequestAttribute(Constants.USER_ID_ATTRIBUTE) UUID userId) {
		List<MeetingResponse> meetingResponses = meetingService.getMeetings(userId);
		return ResponseEntity.ok(meetingResponses);
	}

	@GetMapping("/stats")
	public ResponseEntity<StatLifeTimeResponse> getStats(@RequestAttribute(Constants.USER_ID_ATTRIBUTE) UUID userId) {
		StatLifeTimeResponse statLifeTimeResponse = meetingService.getStats(userId);
		return ResponseEntity.ok(statLifeTimeResponse);
	}

}
