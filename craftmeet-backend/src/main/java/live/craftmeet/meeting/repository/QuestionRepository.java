package live.craftmeet.meeting.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import live.craftmeet.meeting.model.question.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    List<Question> findByMeetingIdOrderByPositionAsc(UUID meetingId);

    long countByMeetingId(UUID meetingId);
}
