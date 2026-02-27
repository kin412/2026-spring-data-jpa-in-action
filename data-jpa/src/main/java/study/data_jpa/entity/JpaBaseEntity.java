package study.data_jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

    @PrePersist // persist 하기전 발생하는 이벤트
    public void prePersist() {

        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updateDate = now;

    }

    @PreUpdate // 업데이트 하기전 발생하는 이벤트
    public void preUpdate() {
        updateDate = LocalDateTime.now();
    }

}
