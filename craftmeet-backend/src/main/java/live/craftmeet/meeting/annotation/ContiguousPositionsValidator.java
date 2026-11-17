package live.craftmeet.meeting.annotation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import live.craftmeet.meeting.dto.QuestionRequest;

public class ContiguousPositionsValidator
		implements ConstraintValidator<ContiguousPositions, List<QuestionRequest>> {

	private static final int START = 1;

	@Override
	public boolean isValid(List<QuestionRequest> questions, ConstraintValidatorContext ctx) {
		if (questions == null)
			return true;

		Set<Integer> positions = questions.stream()
				.map(QuestionRequest::position)
				.collect(Collectors.toSet());

		return positions.size() == questions.size()
				&& positions.stream().allMatch(p -> p >= START && p < START + questions.size());
	}
}
