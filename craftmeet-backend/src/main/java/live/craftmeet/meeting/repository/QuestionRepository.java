package live.craftmeet.meeting.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import live.craftmeet.meeting.model.question.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

	@Modifying
	@Query("DELETE FROM Question q WHERE q.meetingId = :meetingId")
	void deleteAllByMeetingId(@Param("meetingId") UUID meetingId);

	List<Question> findByMeetingIdOrderByPositionAsc(UUID meetingId);

	List<Question> findByMeetingIdInOrderByPositionAsc(List<UUID> meetingIds);

	long countByMeetingId(UUID meetingId);
}
