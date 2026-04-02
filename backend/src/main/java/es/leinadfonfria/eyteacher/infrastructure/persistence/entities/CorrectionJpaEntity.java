package es.leinadfonfria.eyteacher.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

/**
 * JPA entity for correction persistence.
 */
@Entity
@Table(name = "corrections")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrectionJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private UserJpaEntity teacher;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solution_id", nullable = false, unique = true)
    private SolutionJpaEntity solution;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
