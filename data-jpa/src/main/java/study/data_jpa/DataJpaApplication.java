package study.data_jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import study.data_jpa.entity.Member;

@SpringBootApplication
//@EnableJpaRepositories // @SpringBootApplication 이 이 어노테이션의 기능도 수행함
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

}
