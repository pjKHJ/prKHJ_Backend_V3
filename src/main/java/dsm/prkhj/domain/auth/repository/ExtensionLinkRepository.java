package dsm.prkhj.domain.auth.repository;

import dsm.prkhj.domain.auth.entity.ExtensionLink;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtensionLinkRepository extends JpaRepository<ExtensionLink, Long> {

    Optional<ExtensionLink> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
