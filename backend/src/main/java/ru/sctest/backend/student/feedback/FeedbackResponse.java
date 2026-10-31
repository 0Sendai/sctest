package ru.sctest.backend.student.feedback;

import ru.sctest.backend.student.feedback.dto.FeedbackDto;

public sealed interface FeedbackResponse 
    permits FeedbackResponse.Success, FeedbackResponse.Fail 
{
    record Success(FeedbackDto data) implements FeedbackResponse {}
    record Fail(String error) implements FeedbackResponse {}
}
