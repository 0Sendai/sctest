package ru.sctest.backend.db.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;

@Data 
@Entity 
@Table(name = "slide_comments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"assignment", "student", "slide"})
})
public class SlideComment {
    @Id 
    @GeneratedValue(strategy = GenerationType.SEQUENCE, 
        generator = "slide_comment_seq_gen")
    @SequenceGenerator(
        name = "slide_comment_seq_gen",
        sequenceName = "slide_comments_id_seq",
        allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_result_id", nullable = false)
    private AssignmentResult assignmentResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slide_id", nullable = false)
    private Slide slide;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "comment")
    private String comment;
}
