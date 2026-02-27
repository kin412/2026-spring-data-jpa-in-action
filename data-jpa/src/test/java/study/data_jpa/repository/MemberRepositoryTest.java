package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember(){
        System.out.println("memberRepository = " + memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        //값이 없을수도 있기때문에 get으로 받는건 안좋음
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);

    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("memberA");
        Member member2 = new Member("memberB");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 건
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    //NamedQuery
    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);

    }

    //@Query
    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);

    }

    @Test
    public void findUserNameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findByUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }

    }

    @Test
    public void findMemberDto() {

        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }

    }

    //컬렉션 파라미터 바인딩 - 실무에서 많이씀
    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }

    }

    //반환 타입이 자유로움
    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findMember = memberRepository.findListByUsername("AAA");
        System.out.println("findMember = " + findMember);

        //여기에 값이 없어도 empty 컬렉션을 반환함
        List<Member> findMember2 = memberRepository.findListByUsername("qwewqeasdad");
        System.out.println("findMember2.size() = " + findMember2.size());

        Optional<Member> aaa = memberRepository.findOptionalByUsername("AAA");
        System.out.println("aaa = " + aaa);

        //단건의 경우 없으면 null 반환함.
        Member findMember3 = memberRepository.findMemberByUsername("asdqwe");
        System.out.println("findMember3 = " + findMember3);

    }

    @Test
    public void paging() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));


        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        //slice는 count를 쓰지않음.
        //Slice<Member> page = memberRepository.findByAge(age, pageRequest);
        //list도 pageable이 먹긴함
        //List<Member> pageList = memberRepository.findByAge(age, pageRequest);

        //page로 dto 반환도 가능함.
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        //page형에 totalCount가 있음.
        //long totalCount = memberRepository.totalCount(age);

        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        //System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 40));

        /*
        벌크 연산의 문제
        memberRepository.bulkAgePlus(3);를 하면

        @Modifying
        @Query("update Member m set m.age = m.age+1 where m.age >= :age")
        int bulkAgePlus(@Param("age") int age);

        이벌크가 실행되어서 update를 친다. 근데 이 업데이트는 영속성 컨텍스트와는 관련이 없다.
        저장할때 member5는 41로 저장이 되는데 현재 영속성 컨텍스트에는
        memberRepository.save(new Member("member5", 40));
        이것밖에 없어서 member5는 40이다.
        데이터 정합성이 깨지므로 벌크연산후에는 entityManager를 clear 해줘야한다.
        혹시나 남아있는 반영되지않는 쓰기 지연 저장소의 내용이 있을수 있으므로 flush도 한다.
        entity manager는 같은 트랜잭션이라면 같은객체다.
        근데 스프링데이터jpa는 이걸 안해도 된다.
        jpa 인터페이스에 clear해주는 옵션이 있다.
         */

        //when
        int resultCount = memberRepository.bulkAgePlus(3);
        //em.flush();
        //em.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        //then
        assertThat(resultCount).isEqualTo(5);

    }

    @Test
    public void findMemberLazy(){
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        //지연로딩 N+1 문제
        //List<Member> members = memberRepository.findAll();

        //fetch join을 적용해 N+1문제 해결
        //List<Member> members = memberRepository.findMemberFetchJoin();

        //fetch조인을 @Query에 쓰는 대신에 간편하게 @EntityGraph(attributePaths = {"team"}) 로 해결
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }

    }

    @Test
    public void queryHint(){
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        //Member findMember = memberRepository.findById(member1.getId()).get();
        //findMember.setUsername("member2");

        //em.flush();

        //@QueryHints를 readOnly로 걸어 버려서 스냅샷을 찍어두지 않기때문에 flush해도 반응이 없다.
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();

    }

    @Test
    public void lock(){
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        List<Member> result = memberRepository.findLockByUsername("member1");

    }

    @Test
    public void callCustom(){
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void projections(){
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("member1", 10, teamA);
        Member m2 = new Member("member2", 10, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1");

        for (UsernameOnlyDto usernameOnlyDto : result) {
            System.out.println("usernameOnly = " + usernameOnlyDto);
        }

    }

    @Test
    public void nativeQuery(){
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("member1", 10, teamA);
        Member m2 = new Member("member2", 10, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(1,10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection.getUsername = " + memberProjection.getUsername());
            System.out.println("memberProjection.getTeamName = " + memberProjection.getTeamName());
        }
    }

}