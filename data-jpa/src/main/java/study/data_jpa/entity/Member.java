package study.data_jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Member {

    @Id
    @GeneratedValue
    private Long id;
    private String username;

    //jpa는 프록시 방식으로 동작하기 때문에 기본 생성자가 있어야함.
    protected Member() {}

    public Member(String username) {
        this.username = username;
    }

   /* public void changeUsername(String username) {
        this.username = username;
    }*/

}
