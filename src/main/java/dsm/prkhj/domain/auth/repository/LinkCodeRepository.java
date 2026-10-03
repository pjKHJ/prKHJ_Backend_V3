package dsm.prkhj.domain.auth.repository;

import dsm.prkhj.domain.auth.entity.LinkCode;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface LinkCodeRepository extends JpaRepository<LinkCode, Long> {

    Optional<LinkCode> findByUserId(Long userId);

    boolean existsByCode(String code);

    void deleteByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    // 잠금(비관적락) -> @Transactional 필수
    // FOR UPDATE
    @Query("select c from LinkCode c join fetch c.user where c.code = :code") // 조인한 users 잠깁
    Optional<LinkCode> findByCodeForUpdate(String code);
}
