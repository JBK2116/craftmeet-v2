package live.craftmeet.live.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import live.craftmeet.live.model.participant.Participant;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, UUID> {
	List<Participant> findAllByMeetingIdOrderByCreatedAtAsc(UUID meetingId);

	boolean existsByMeetingIdAndNameIgnoreCase(UUID meetingId, String name);

	@Query("DELETE FROM Participant p WHERE p.meetingId = :meetingId")
	@Modifying
	void deleteAllByMeetingId(@Param("meetingId") UUID meetingId);

	long countByMeetingId(UUID meetingId);

}
