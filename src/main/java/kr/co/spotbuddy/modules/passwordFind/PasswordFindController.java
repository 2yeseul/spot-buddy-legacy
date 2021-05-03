package kr.co.spotbuddy.modules.passwordFind;

import kr.co.spotbuddy.modules.passwordFind.dto.PasswordReset;
import kr.co.spotbuddy.modules.passwordFind.dto.PasswordToken;
import kr.co.spotbuddy.modules.passwordFind.dto.PwResetRequest;
import kr.co.spotbuddy.modules.passwordFind.dto.PwResetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import kr.co.spotbuddy.modules.response.APIResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/setting/password")
public class PasswordFindController {
    private final PasswordFindService passwordFindService;

    @PostMapping("/send-email")
    public PwResetResponse sendPasswordFindEmail(@RequestBody PwResetRequest pwResetRequest) {
        return passwordFindService.sendPasswordFindEmail(pwResetRequest);
    }

    @PostMapping("/token-check")
    public APIResponse isTokenValid(@RequestBody PasswordToken passwordToken) {
        return passwordFindService.checkFindToken(passwordToken);
    }

    @PostMapping("/reset")
    public void resetMemberPassword(@RequestBody PasswordReset passwordReset) {
        passwordFindService.resetPassword(passwordReset);
    }

}
