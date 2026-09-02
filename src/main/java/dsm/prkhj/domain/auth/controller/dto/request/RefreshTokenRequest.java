package dsm.prkhj.domain.auth.controller.dto.request;

/**
 * A3 리프레시 / A4 로그아웃 둘 다 body가 refreshToken 하나뿐이라 같이 쓴다.
 */
public record RefreshTokenRequest(String refreshToken) {
}
