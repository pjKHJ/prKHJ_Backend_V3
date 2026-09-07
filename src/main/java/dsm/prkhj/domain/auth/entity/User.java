package dsm.prkhj.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_github_user_id", columnNames = "github_user_id"),
        @UniqueConstraint(name = "uk_users_github_login", columnNames = "github_login")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Column(name = "github_user_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long githubUserId;

    @Column(name = "github_login", nullable = false, length = 39)
    private String githubLogin;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "github_access_token", columnDefinition = "VARBINARY(512)")
    private byte[] githubAccessToken;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME(3)")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(3)")
    private LocalDateTime updatedAt;

    @Builder
    public User(Long githubUserId, String githubLogin, String avatarUrl) {
        this.githubUserId = githubUserId;
        this.githubLogin = githubLogin;
        this.avatarUrl = avatarUrl;
        this.role = Role.USER;
    }

    // 평문이 이 엔티티에 들어오면 안 됨
    public void updateGithubAccessToken(byte[] githubAccessToken) {
        this.githubAccessToken = githubAccessToken;
    }

    // github_login과 avatar_url은 GitHub에서 바뀔 수 있으므로 로그인할 때마다 갱신
    public void syncGithubProfile(String githubLogin, String avatarUrl) {
        this.githubLogin = githubLogin;
        this.avatarUrl = avatarUrl;
    }
}
