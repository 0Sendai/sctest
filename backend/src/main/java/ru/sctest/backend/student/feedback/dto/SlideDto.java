package ru.sctest.backend.student.feedback.dto;

import java.util.HashMap;
import java.util.Map;

import ru.sctest.backend.db.model.Slide;

public record SlideDto(Long id, String type, Map<String, Object> content) {
    public static SlideDto from(Slide slide) {
        Map<String, Object> content = slide.getContent();
        if (content == null)
            return null;
        Map<String, Object> editedContent = new HashMap<>(content);
        editedContent.remove("correctVariantIndex");
        Long id = slide.getId();
        String type = slide.getType();
        if (id == null || type == null)
            return null;

        return new SlideDto(id, type, editedContent);
    }
}
