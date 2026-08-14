package dsm.prkhj.domain.auth.repository;

import dsm.prkhj.domain.auth.entity.LinkCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkCodeRepository extends JpaRepository<LinkCode, Long> {
}
