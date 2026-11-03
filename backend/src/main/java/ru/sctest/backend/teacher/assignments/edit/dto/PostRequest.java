package ru.sctest.backend.teacher.assignments.edit.dto;

import java.util.List;
import java.util.UUID;

public record PostRequest(
    UUID assignmentId,
    String title,
    List<PostRequestSlideDto> slides,
    List<Long> studentIdsJson
) {}
