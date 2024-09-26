package ru.d4nik.carparts.components.kiselev;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.mock.mockito.SpyBeans;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import ru.d4nik.carparts.components.kiselev.adapters.email.EmailAdapter;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(DatabaseContainerExtension.class)
@SpyBeans({
        @SpyBean(EmailAdapter.class),
})
@MockBeans({
        @MockBean(JavaMailSender.class)
})
@EnableAutoConfiguration
public abstract class IntegrationTest {
    @Autowired
    protected JdbcTemplate jdbcTemplate;


//    @TestConfiguration
//    @ImportAutoConfiguration({LiquibaseAutoConfiguration.class})
//    @AutoConfiguration
//    public static class TestConfig {
//        @Bean
//        public JavaMailSender javaMailSender() {
//            return new TestJavaMailSender();
//        }
//    }

}
