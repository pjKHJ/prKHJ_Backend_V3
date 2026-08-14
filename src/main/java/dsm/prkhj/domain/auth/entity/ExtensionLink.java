package dsm.prkhj.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "extension_links")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtensionLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true, columnDefinition = "BIGINT UNSIGNED",
            foreignKey = @ForeignKey(name = "fk_ext_links_user"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "link_code_id", nullable = false, columnDefinition = "BIGINT UNSIGNED",
            foreignKey = @ForeignKey(name = "fk_ext_links_code"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private LinkCode linkCode;

    @CreationTimestamp
    @Column(name = "linked_at", nullable = false, updatable = false, columnDefinition = "DATETIME(3)")
    private LocalDateTime linkedAt;

    @Builder
    public ExtensionLink(User user, LinkCode linkCode) {
        this.user = user;
        this.linkCode = linkCode;
    }
}
