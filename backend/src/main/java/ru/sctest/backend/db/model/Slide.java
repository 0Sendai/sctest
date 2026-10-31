package ru.sctest.backend.db.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data 
@Entity 
@Table(name = "slides", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"assignment", "position"})
})
public class Slide {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, 
        generator = "slide_seq_gen")
    @SequenceGenerator(
        name = "slide_seq_gen",
        sequenceName = "slides_id_seq",
        allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @Column(name = "type")
    private String type;

    @Column(name = "position", nullable = false)
    private Integer position;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp 
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "currentSlide")
    private List<AssignmentResult> assignmentResults;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "slide")
    private List<SlideResult> slideResults;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "slide")
    private List<SlideComment> slideComments;
}
