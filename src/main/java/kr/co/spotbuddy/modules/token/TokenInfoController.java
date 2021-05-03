package kr.co.spotbuddy.modules.token;

import kr.co.spotbuddy.modules.token.dto.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
@Slf4j
public class TokenInfoController {

    private final TokenInfoService tokenInfoService;

    @PostMapping("/save")
    public void getIosToken(@RequestBody Token token, @RequestHeader(value = "User-Agent") String header) throws Exception {
        log.info("USER AGENT IS : " + header);
        tokenInfoService.iosTokenProcess(token);
    }
}
