package live.craftmeet.meeting.service;

import org.springframework.stereotype.Service;

import live.craftmeet.meeting.repository.QuestionRepository;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

}
