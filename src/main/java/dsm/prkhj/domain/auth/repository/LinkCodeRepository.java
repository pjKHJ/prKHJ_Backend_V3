package dsm.prkhj.domain.auth.repository;

import dsm.prkhj.domain.auth.entity.LinkCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkCodeRepository extends JpaRepository<LinkCode, Long> {

    Optional<LinkCode> findByUserId(Long userId);

    boolean existsByCode(String code);

    void deleteByUserId(Long userId);
}
