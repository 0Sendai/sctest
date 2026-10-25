package ru.sctest.backend.db.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import lombok.Getter;


@Entity
@Table(name = "users")
@Getter 
public class User {
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    private List<Assignment> assignments;

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
