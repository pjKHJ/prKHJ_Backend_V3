package dsm.prkhj;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PrKhjBackendV3Application {

    public static void main(String[] args) {
        // Hibernate 초기화 전에 설정해야 하므로 run() 이전에 둔다
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        SpringApplication.run(PrKhjBackendV3Application.class, args);
    }

}
