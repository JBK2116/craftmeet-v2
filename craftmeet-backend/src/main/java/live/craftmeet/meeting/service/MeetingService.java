package live.craftmeet.meeting.service;

import org.springframework.stereotype.Service;

import live.craftmeet.meeting.repository.MeetingRepository;

@Service
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final QuestionService questionService;

    public MeetingService(MeetingRepository meetingRepository, QuestionService questionService) {
        this.meetingRepository = meetingRepository;
        this.questionService = questionService;
    }
}
