package ru.sctest.backend.student.feedback.dto;

import java.util.List;
import java.util.Map;

public record FeedbackDto(
    String title,
    ResultDto result,
    List<SlideDto> slides,
    Map<Long, Object> answers,
    Map<Long, String> comments
) {}


