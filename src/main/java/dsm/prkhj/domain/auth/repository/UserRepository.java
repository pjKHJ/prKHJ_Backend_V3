package dsm.prkhj.domain.auth.repository;

import dsm.prkhj.domain.auth.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByGithubUserId(Long githubUserId);
}
