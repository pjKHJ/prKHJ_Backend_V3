package dsm.prkhj.global.time;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

// 엔티티는 LocalDateTime(KST)으로 저장, API는 +09:00으로 내보냄
public final class Kst {

    public static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private Kst() {
    }

    public static OffsetDateTime toOffset(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(ZONE).toOffsetDateTime();
    }
}
