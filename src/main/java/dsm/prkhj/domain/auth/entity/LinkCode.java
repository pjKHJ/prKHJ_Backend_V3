package dsm.prkhj.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "link_codes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LinkCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true, columnDefinition = "BIGINT UNSIGNED",
            foreignKey = @ForeignKey(name = "fk_link_codes_user"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false, unique = true, columnDefinition = "CHAR(10)")
    private String code;

    @CreationTimestamp
    @Column(name = "issued_at", nullable = false, updatable = false, columnDefinition = "DATETIME(3)")
    private LocalDateTime issuedAt;

    @Builder
    public LinkCode(User user, String code) {
        this.user = user;
        this.code = code;
    }
}
