package study.data_jpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember(@PathVariable("id") Member member) {
        //도메인 클래스 컨버터. 이 작업을 그냥 매개변수에서 다해준다.
        //영속성 컨텍스트에 속하지 않으므로 조회용으로만 써라
        //Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);

        //노출되는 경우 dto로 변환
        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        return map;
    }

    @PostConstruct
    public void init() {
        //memberRepository.save(new Member("userA"));

        for(int i = 0; i < 100; i++){
            memberRepository.save(new Member("user" +i, i));
        }

    }

}
