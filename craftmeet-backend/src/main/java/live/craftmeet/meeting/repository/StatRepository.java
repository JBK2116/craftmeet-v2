package live.craftmeet.meeting.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import live.craftmeet.meeting.model.stat.Stat;

@Repository
public interface StatRepository extends JpaRepository<Stat, UUID> {
    Optional<Stat> findByMeetingId(UUID meetingId);
}
