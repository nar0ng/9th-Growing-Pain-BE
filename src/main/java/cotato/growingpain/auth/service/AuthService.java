package cotato.growingpain.auth.service;

import cotato.growingpain.auth.dto.request.JoinRequest;
import cotato.growingpain.member.domain.entity.Member;
import cotato.growingpain.member.repository.MemberRepository;
import cotato.growingpain.security.jwt.Token;
import cotato.growingpain.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final ValidateService validateService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public Token createLoginInfo(JoinRequest request) {

        validateService.checkPasswordPattern(request.password());
        validateService.checkDuplicateEmail(request.email());
        validateService.checkDuplicateNickName(request.name());

        log.info("[회원 가입 서비스]: {}", request.email());

        Member newMember = Member.builder()
                .password(bCryptPasswordEncoder.encode(request.password()))
                .email(request.email())
                .name(request.name())
                .field(request.field())
                .belong(request.belong())
                .build();
        memberRepository.save(newMember);

        // 회원가입 성공 후 토큰 생성 및 반환
        return tokenProvider.createToken(request.email());
    }
}