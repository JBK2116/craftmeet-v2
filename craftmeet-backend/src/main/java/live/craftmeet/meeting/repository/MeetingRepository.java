package live.craftmeet.meeting.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import live.craftmeet.meeting.model.meeting.Meeting;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, UUID> {

	Optional<Meeting> findByRoomCode(String roomCode);

	Optional<Meeting> findByIdAndUserId(UUID id, UUID userId);

	@Modifying
	@Query("DELETE FROM Meeting m WHERE m.userId = :userId")
	void deleteAllByUserId(@Param("userId") UUID userId);

	int deleteByIdAndUserId(UUID id, UUID userId);

	List<Meeting> findByUserId(UUID userId);

	@Query("SELECT m FROM Meeting m WHERE m.session.startTime IS NOT NULL AND m.session.endTime IS NULL AND m.userId = :userId")
	Optional<Meeting> findByLiveMeetingAndUserId(@Param("userId") UUID userId);

	boolean existsByRoomCode(String roomCode);
}
