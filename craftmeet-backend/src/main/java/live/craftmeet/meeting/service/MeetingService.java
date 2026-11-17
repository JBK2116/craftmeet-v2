package live.craftmeet.meeting.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import live.craftmeet.meeting.dto.MeetingRequest;
import live.craftmeet.meeting.dto.MeetingResponse;
import live.craftmeet.meeting.dto.QuestionResponse;
import live.craftmeet.meeting.exception.RoomCodeGenerationError;
import live.craftmeet.meeting.model.meeting.Meeting;
import live.craftmeet.meeting.model.meeting.MeetingExport;
import live.craftmeet.meeting.model.meeting.MeetingSession;
import live.craftmeet.meeting.model.meeting.MeetingSettings;
import live.craftmeet.meeting.repository.MeetingRepository;

@Service
public class MeetingService {
	private static final int MAX_ROOM_CODE_ATTEMPTS = 10;

	private final MeetingRepository meetingRepository;
	private final QuestionService questionService;

	public MeetingService(MeetingRepository meetingRepository, QuestionService questionService) {
		this.meetingRepository = meetingRepository;
		this.questionService = questionService;
	}

	@Transactional
	public MeetingResponse create(MeetingRequest meetingRequest, UUID userId) {
		Meeting meeting = createAndSaveMeeting(meetingRequest, userId);
		List<QuestionResponse> questionResponseList = questionService.createAndSaveQuestions(meetingRequest.questions(),
				meeting.getId());
		return MeetingResponse.from(meeting, questionResponseList);
	}

	/**
	 * Creates a meeting from the supplied request and saves it to the repository.
	 * The meeting is assigned the given user ID, a generated room code, and the
	 * duration and participant cap from the request.
	 *
	 * @param meetingRequest
	 *                           the request containing meeting details and settings
	 * @param userId
	 *                           the ID of the user creating the meeting
	 * @return the saved meeting
	 */
	private Meeting createAndSaveMeeting(MeetingRequest meetingRequest, UUID userId) {
		Meeting meeting = new Meeting();
		meeting.setUserId(userId);
		meeting.setTitle(meetingRequest.title());
		meeting.setDescription(meetingRequest.description());
		meeting.setRoomCode(generateRoomCode());
		meeting.setSettings(new MeetingSettings(meetingRequest.duration(), meetingRequest.participantCap()));
		meeting.setSession(new MeetingSession());
		meeting.setExport(new MeetingExport());
		return meetingRepository.save(meeting);
	}

	/**
	 * Generates an eight-digit room code that is not currently assigned to a
	 * meeting.
	 * Random candidate codes are checked against the meeting repository for up to
	 * the maximum number of attempts.
	 *
	 * @return a unique eight-digit room code not already assigned to a meeting
	 * @throws RoomCodeGenerationError
	 *                                     if a unique room code cannot be generated
	 *                                     within the allowed attempts
	 */
	private String generateRoomCode() {
		int counter = 0;
		while (counter < MAX_ROOM_CODE_ATTEMPTS) {
			int randomNum = ThreadLocalRandom.current().nextInt(100_000_000);
			String roomCode = String.format("%08d", randomNum);
			if (!meetingRepository.existsByRoomCode(roomCode)) {
				return roomCode;
			}
			counter++;
		}
		throw new RoomCodeGenerationError();
	}
}
