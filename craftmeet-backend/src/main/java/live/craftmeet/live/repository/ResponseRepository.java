package live.craftmeet.live.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import live.craftmeet.live.model.response.Response;

@Repository
public interface ResponseRepository extends JpaRepository<Response, UUID> {
	List<Response> findAllByQuestionIdOrderByCreatedAtAsc(UUID questionId);

	@Query("DELETE FROM Response r WHERE r.questionId = :questionId")
	@Modifying
	void deleteByQuestionId(@Param("questionId") UUID questionId);

	long countByQuestionId(UUID questionId);
}
