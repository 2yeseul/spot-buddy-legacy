package kr.co.spotbuddy.modules.deleteAccount;

import kr.co.spotbuddy.infra.domain.DeleteAccount;
import kr.co.spotbuddy.infra.domain.Member;
import kr.co.spotbuddy.modules.deleteAccount.dto.DeleteForm;
import kr.co.spotbuddy.modules.member.MemberType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeleteAccountService {
    private final DeleteAccountRepository deleteAccountRepository;
    private final MemberType memberType;

    @Transactional
    public void saveDeleteAccount(Object object, DeleteForm deleteForm) {
        Member member = memberType.getMemberType(object);

        String etc = "empty";
        if(deleteForm.getEtc()!=null) etc = deleteForm.getEtc();

        DeleteAccount deleteAccount = DeleteAccount.builder()
                .email(member.getEmail())
                .reasonIndex(deleteForm.getReasonIndex())
                .etc(etc)
                .deleteTime(LocalDateTime.now())
                .build();

        deleteAccountRepository.save(deleteAccount);
    }
}
