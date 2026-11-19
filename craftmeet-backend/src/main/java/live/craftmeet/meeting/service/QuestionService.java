package live.craftmeet.meeting.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import live.craftmeet.meeting.dto.question.*;
import live.craftmeet.meeting.model.question.*;
import live.craftmeet.meeting.repository.QuestionRepository;

/**
 * Persists questions for a meeting by mapping each {@link QuestionRequest}
 * subtype to its corresponding {@code Question} entity and saving it.
 */
@Service
public class QuestionService {

	private final QuestionRepository questionRepository;

	public QuestionService(QuestionRepository questionRepository) {
		this.questionRepository = questionRepository;
	}

	/**
	 * Maps every request to its question entity, saves it, and collects the
	 * resulting responses. Runs in a single transaction so the inserts are
	 * batched and atomic.
	 *
	 * @param questionRequests
	 *                             the questions to create
	 * @param meetingId
	 *                             the meeting the questions belong to
	 * @return the saved questions as responses
	 */
	@Transactional
	public List<QuestionResponse> createAndSaveQuestions(List<QuestionRequest> questionRequests, UUID meetingId) {
		List<QuestionResponse> questionResponses = new ArrayList<>();

		for (QuestionRequest questionRequest : questionRequests) {
			QuestionResponse response = switch (questionRequest) {
				case LongAnswerQuestionRequest r -> createAndSaveLongAnswerQuestion(r, meetingId);
				case MultipleChoiceQuestionRequest r -> createAndSaveMultipleChoiceQuestion(r, meetingId);
				case RankedVotingQuestionRequest r -> createAndSaveRankedVotingQuestion(r, meetingId);
				case RatingScaleQuestionRequest r -> createAndSaveRatingScaleQuestion(r, meetingId);
				case YesNoQuestionRequest r -> createAndSaveYesNoQuestion(r, meetingId);
			};
			questionResponses.add(response);
		}

		return questionResponses;
	}

	@Transactional
	public List<QuestionResponse> rotateAndSaveQuestions(List<QuestionRequest> questionRequests, UUID meetingId) {
		questionRepository.deleteAllByMeetingId(meetingId);
		return createAndSaveQuestions(questionRequests, meetingId);
	}

	public List<QuestionResponse> getQuestions(UUID meetingId) {
		List<Question> questions = questionRepository.findByMeetingIdOrderByPositionAsc(meetingId);
		List<QuestionResponse> questionResponses = new ArrayList<>();
		for (Question question : questions) {
			questionResponses.add(QuestionResponse.from(question));
		}
		return questionResponses;
	}

	/**
	 * Creates and saves a free-text question with a maximum response length.
	 */
	private QuestionResponse createAndSaveLongAnswerQuestion(LongAnswerQuestionRequest questionRequest,
			UUID meetingId) {
		LongAnswerQuestion question = new LongAnswerQuestion();
		question.setMeetingId(meetingId);
		question.setPosition(questionRequest.position());
		question.setPrompt(questionRequest.prompt());
		question.setMaxLength(questionRequest.maxLength());
		question = questionRepository.save(question);
		return QuestionResponse.from(question);
	}

	/**
	 * Creates and saves a multiple-choice question with up to four options.
	 */
	private QuestionResponse createAndSaveMultipleChoiceQuestion(MultipleChoiceQuestionRequest questionRequest,
			UUID meetingId) {
		MultipleChoiceQuestion question = new MultipleChoiceQuestion();
		question.setMeetingId(meetingId);
		question.setPosition(questionRequest.position());
		question.setPrompt(questionRequest.prompt());
		question.setAllowMultiple(questionRequest.allowMultiple());
		question.setOption1(questionRequest.option1());
		question.setOption2(questionRequest.option2());
		question.setOption3(questionRequest.option3());
		question.setOption4(questionRequest.option4());
		question = questionRepository.save(question);
		return QuestionResponse.from(question);
	}

	/**
	 * Creates and saves a ranked-voting question with up to four items.
	 */
	private QuestionResponse createAndSaveRankedVotingQuestion(RankedVotingQuestionRequest questionRequest,
			UUID meetingId) {
		RankedVotingQuestion question = new RankedVotingQuestion();
		question.setMeetingId(meetingId);
		question.setPosition(questionRequest.position());
		question.setPrompt(questionRequest.prompt());
		question.setItem1(questionRequest.item1());
		question.setItem2(questionRequest.item2());
		question.setItem3(questionRequest.item3());
		question.setItem4(questionRequest.item4());
		question = questionRepository.save(question);
		return QuestionResponse.from(question);
	}

	/**
	 * Creates and saves a rating-scale question bounded by a minimum and maximum
	 * value.
	 */
	private QuestionResponse createAndSaveRatingScaleQuestion(RatingScaleQuestionRequest questionRequest,
			UUID meetingId) {
		RatingScaleQuestion question = new RatingScaleQuestion();
		question.setMeetingId(meetingId);
		question.setPosition(questionRequest.position());
		question.setPrompt(questionRequest.prompt());
		question.setMin(questionRequest.min());
		question.setMax(questionRequest.max());
		question = questionRepository.save(question);
		return QuestionResponse.from(question);
	}

	/**
	 * Creates and saves a yes/no question with no extra configuration.
	 */
	private QuestionResponse createAndSaveYesNoQuestion(YesNoQuestionRequest questionRequest, UUID meetingId) {
		YesNoQuestion question = new YesNoQuestion();
		question.setMeetingId(meetingId);
		question.setPosition(questionRequest.position());
		question.setPrompt(questionRequest.prompt());
		question = questionRepository.save(question);
		return QuestionResponse.from(question);
	}

}
