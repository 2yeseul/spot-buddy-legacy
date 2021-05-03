package kr.co.spotbuddy.modules.appVersion;

import kr.co.spotbuddy.modules.appVersion.dto.AppVersionRequest;
import kr.co.spotbuddy.modules.appVersion.dto.AppVersionResponse;
import kr.co.spotbuddy.modules.appVersion.dto.AppVersionSave;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/version")
public class AppVersionController {
    private final AppVersionService appVersionService;

    @PostMapping("/save")
    public void saveVersion(@RequestBody AppVersionSave versionSave) {
        appVersionService.saveAppVersion(versionSave);
    }

    @PostMapping("/get")
    public AppVersionResponse getAppVersion(@RequestBody AppVersionRequest versionRequest) {
        return appVersionService.getAppVersion(versionRequest);
    }

    @PostMapping("/update")
    public void updateToken(@RequestBody AppVersionSave appVersionSave) {
        appVersionService.updateAppVersion(appVersionSave);
    }

}
