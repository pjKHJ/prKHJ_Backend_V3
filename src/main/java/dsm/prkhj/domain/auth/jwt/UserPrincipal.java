package dsm.prkhj.domain.auth.jwt;

import dsm.prkhj.domain.auth.entity.Role;

public record UserPrincipal(Long id, String githubLogin, Role role) {
}
