package kr.co.spotbuddy.modules.member;

import kr.co.spotbuddy.modules.member.dto.ModifyForm;
import kr.co.spotbuddy.modules.member.dto.PasswordForm;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settings")
public class SettingController {

    private final MemberService memberService;

    @PostMapping("/password")
    public String updatePassword(@CurrentUser Object object, @RequestBody PasswordForm passwordForm) {
        memberService.updateNewPassword(object, passwordForm);
        return "success";
    }

    @PostMapping("/modify-info")
    public void modifyMemberInfo(@CurrentUser Object object, @RequestBody ModifyForm modifyForm) {
        memberService.modifyMemberInfo(object, modifyForm);
    }

}
