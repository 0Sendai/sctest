package ru.sctest.backend.db.model;

import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import lombok.Data;

@Data 
@Entity 
@Table(name = "slide_results", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"assignmentResult", "slide"})
})
public class SlideResult {
    @Id 
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    private AssignmentResult assignmentResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slide_id", nullable = false)
    private Slide slide;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "answer", columnDefinition = "jsonb")
    private Map<String, Object> answer;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @CreationTimestamp
    @Column(name = "answered_at", nullable = false)
    private LocalDateTime answeredAt;
}
