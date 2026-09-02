package dsm.prkhj;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PrKhjBackendV3Application {

    // @CreationTimestamp가 찍는 issued_at/linked_at이 KST 벽시계여야
    // 응답의 +09:00 오프정셋과 실제 시각이 일치한다
    @PostConstruct
    public void setDefaultTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(PrKhjBackendV3Application.class, args);
    }

}
