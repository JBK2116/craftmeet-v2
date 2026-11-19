package live.craftmeet.meeting.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import live.craftmeet.meeting.dto.StatResponse;
import live.craftmeet.meeting.exception.StatNotFoundError;
import live.craftmeet.meeting.model.stat.Stat;
import live.craftmeet.meeting.repository.StatRepository;

@Service
public class StatService {
	private final StatRepository statRepository;

	public StatService(StatRepository statRepository) {
		this.statRepository = statRepository;
	}

	/**
	 * Creates a statistics record for the specified meeting.
	 *
	 * @param meetingId
	 *                      the ID of the meeting for which the statistics record is
	 *                      created
	 * @return the response containing the saved statistics data
	 */
	@Transactional
	public StatResponse create(UUID meetingId) {
		return createAndSave(meetingId);

	}

	/**
	 * Retrieves the statistics record for the specified meeting.
	 *
	 * @param meetingId
	 *                      the ID of the meeting whose statistics record is
	 *                      retrieved
	 * @return the response containing the statistics data for the meeting
	 */
	public StatResponse get(UUID meetingId) {
		Stat stat = statRepository.findByMeetingId(meetingId).orElseThrow(() -> new StatNotFoundError(meetingId));
		return StatResponse.from(stat);
	}

	/**
	 * Creates and saves a statistics record for the specified meeting.
	 *
	 * @param meetingId
	 *                      the ID of the meeting for which the statistics record is
	 *                      created
	 * @return the response containing the saved statistics data
	 */
	private StatResponse createAndSave(UUID meetingId) {
		Stat stat = new Stat();
		stat.setMeetingId(meetingId);
		stat = statRepository.save(stat);
		return StatResponse.from(stat);
	}

}
