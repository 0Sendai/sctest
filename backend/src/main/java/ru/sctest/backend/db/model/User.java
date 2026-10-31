package ru.sctest.backend.db.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;

@Data 
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, 
        generator = "user_seq_gen")
    @SequenceGenerator(
        name = "user_seq_gen",
        sequenceName = "users_id_seq",
        allocationSize = 1
    )
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String password;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column
    private UserRole role;

    @Column(name="created_at")
    @CreationTimestamp 
    private LocalDateTime createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    private List<Assignment> assignments;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "student")
    private List<AssignmentResult> assignmentResults;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "student")
    private List<SlideComment> slideComments;

    public User() {}

    public User(Long id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = UserRole.valueOf(role);
    }

    @Override 
    public String toString() {
        return String.format("\nname: %s\npass: %s\nrole: %s\n created: %t", 
            name, password, role, createdAt
        );
    }
}
