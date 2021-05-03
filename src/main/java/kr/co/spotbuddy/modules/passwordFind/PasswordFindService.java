package kr.co.spotbuddy.modules.passwordFind;

import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.infra.domain.PasswordFind;
import kr.co.spotbuddy.modules.member.MemberRepository;
import kr.co.spotbuddy.modules.passwordFind.dto.PasswordReset;
import kr.co.spotbuddy.modules.passwordFind.dto.PasswordToken;
import kr.co.spotbuddy.modules.passwordFind.dto.PwResetRequest;
import kr.co.spotbuddy.modules.passwordFind.dto.PwResetResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import kr.co.spotbuddy.modules.mail.EmailMessage;
import kr.co.spotbuddy.modules.mail.EmailService;
import kr.co.spotbuddy.modules.response.APIResponse;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordFindService {
    private final MemberRepository memberRepository;
    private final PasswordFindRepository passwordFindRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public PwResetResponse sendPasswordFindEmail(PwResetRequest pwResetRequest) {
        if(!memberRepository.existsByEmail(pwResetRequest.getEmail())) {
            return PwResetResponse.builder().message("해당 정보로 가입된 내역이 없습니다.").build();
        }
        else {
            Member member = memberRepository.findByEmail(pwResetRequest.getEmail());
            if(!pwResetRequest.getName().equals(member.getName())) {
                return PwResetResponse.builder().message("기입한 정보를 다시 한 번 확인해주시기 바랍니다.").build();
            }

            int token = (int) (Math.random() * (99999 - 10000 + 1)) + 10000;
            log.info("token is : " + token);

            sendTokenCheckEmail(pwResetRequest, token);

            savePasswordToken(pwResetRequest, token);

            return PwResetResponse.builder()
                    .message("인증번호가 발송되었습니다.")
                    .build();
        }
    }

    private void sendTokenCheckEmail(PwResetRequest pwResetRequest, int token) {
        Context context = new Context();
        context.setVariable("token", token);

        String message = templateEngine.process("pw-find", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("SPOT BUDDY 비밀번호 찾기 인증 번호 메일입니다. (1시간 유효) ")
                .to(pwResetRequest.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void savePasswordToken(PwResetRequest pwResetRequest, int token) {
        PasswordFind passwordFind = PasswordFind.builder()
                .email(pwResetRequest.getEmail())
                .token(token)
                .sendEmailTime(LocalDateTime.now())
                .build();

        passwordFindRepository.save(passwordFind);
    }


    public APIResponse checkFindToken(PasswordToken passwordToken) {
        APIResponse apiResponse = new APIResponse();
        PasswordFind passwordFind = passwordFindRepository.findTopByEmailOrderByIdDesc(passwordToken.getEmail());
        if(passwordFind.getToken() == passwordToken.getToken()) {
            apiResponse.setMessage("correct");
        }
        else apiResponse.setMessage("incorrect");

        passwordFind.updateConfirmTime();
        passwordFind.mailConfirmed();

        passwordFindRepository.save(passwordFind);

        return apiResponse;
    }

    public void resetPassword(PasswordReset passwordReset) {
        Member member = memberRepository.findByEmail(passwordReset.getEmail());
        PasswordFind passwordFind = passwordFindRepository.findTopByEmailOrderByIdDesc(passwordReset.getEmail());
        if(passwordFind.isConfirmed()) {
            member.resetPassword(passwordEncoder.encode(passwordReset.getPassword()));

            memberRepository.save(member);
        }
    }
}
