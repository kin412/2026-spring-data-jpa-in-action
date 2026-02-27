package study.data_jpa.repository;


import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.hibernate.annotations.Parameter;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3HelloBy();

    //namedquery
    //@Query(name = "Member.findByUsername") // 이게 없어도 잘동작함.
    //jpareposiotry에 명시되어있는 엔티티명.함수명 으로 생성함.
    List<Member> findByUsername(@Param("username") String username);


    //@Query는 쿼리가 살짝 복잡할 경우 실무에 많이 씀
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findByUsernameList();

    @Query("select new study.data_jpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    //컬렉션 파라미터 바인딩. 컬렉션도 in을 통해 파라미터로 받을수 있음. 실무에서 많이씀
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    //스프링 데이터 jpa는 반환 타입이 자유로움
    List<Member> findListByUsername(String username); //컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 optional

    //Pageable
    //count의 경우 조회쿼리를 꼭 따라가지 않아도 갯수가 나올수 있기때문에 최적화를 위해 따로짤수도 있다.
    /*@Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")*/
    Page<Member> findByAge(int age, Pageable pageable);
    //Slice<Member> findByAge(int age, Pageable pageable);
    //list도 pageable이 먹긴함
    //List<Member> findByAgeList(int age, Pageable pageable);
    //상위 3개 이런식으로도 가능
    //Page<Member> findTop3ByAge(int age, Pageable pageable);

    //벌크성 수정쿼리는 영속성 컨텍스트와의 문제가 있다.
    //clear를 직접하지않아도 clearAutomatically 옵션을 true로 넣어주면 clear 해준다.
    @Modifying(clearAutomatically = true)// 벌크 시 jpa의 executeQuery()
    @Query("update Member m set m.age = m.age+1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    //n+1 문제를 fetch join으로 해결
    //근데 fetch 조인을 하려면 @Query를 꼭 써워야하는가?
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    //그래서 간단히 쓸수 있는게 @EntityGraph
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    List<UsernameOnlyDto> findProjectionsByUsername(@Param("username") String username);

    @Query(value = " select * from member where username= ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    @Query(value = "select m.member_id as id, m.username, t.name as teamName " +
            "from member m left join team t",
            countQuery = "select count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);

}
