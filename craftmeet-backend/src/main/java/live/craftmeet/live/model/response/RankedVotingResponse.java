package live.craftmeet.live.model.response;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A ranked-voting response assigning each rank position (1-4) an item number.
 * {@code rank1} is the top priority; {@code rank3} and {@code rank4} mirror
 * the optional items and may be {@code null}.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("ranked_voting")
public class RankedVotingResponse extends Response {

	@Column(name = "rank_1", check = @CheckConstraint(name = "ck_response_rank_1", constraint = "rank_1 BETWEEN 1 AND 4"))
	private Integer rank1;

	@Column(name = "rank_2", check = @CheckConstraint(name = "ck_response_rank_2", constraint = "rank_2 BETWEEN 1 AND 4"))
	private Integer rank2;

	@Column(name = "rank_3", check = @CheckConstraint(name = "ck_response_rank_3", constraint = "rank_3 BETWEEN 1 AND 4"))
	private Integer rank3;

	@Column(name = "rank_4", check = @CheckConstraint(name = "ck_response_rank_4", constraint = "rank_4 BETWEEN 1 AND 4"))
	private Integer rank4;
}
