package kr.co.spotbuddy.modules.appVersion;

import kr.co.spotbuddy.infra.domain.AppVersion;
import kr.co.spotbuddy.modules.appVersion.dto.AppVersionRequest;
import kr.co.spotbuddy.modules.appVersion.dto.AppVersionResponse;
import kr.co.spotbuddy.modules.appVersion.dto.AppVersionSave;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppVersionService {

    private final AppVersionRepository appVersionRepository;

    @Transactional
    public void saveAppVersion(AppVersionSave appVersionSave) {
        if(!appVersionRepository.existsByToken(appVersionSave.getToken())) {
            AppVersion appVersion = AppVersion.builder()
                    .token(appVersionSave.getToken())
                    .version(appVersionSave.getVersion())
                    .build();

            appVersionRepository.save(appVersion);
        }
    }

    public AppVersionResponse getAppVersion(AppVersionRequest request) {
        if(appVersionRepository.existsByToken(request.getToken())) {
            String version = appVersionRepository.findByToken(request.getToken()).getVersion();

            return AppVersionResponse.builder().version(version).build();
        }

        return null;
    }

    @Transactional
    public void updateAppVersion(AppVersionSave appVersionSave) {
        AppVersion appVersion = appVersionRepository.findByToken(appVersionSave.getToken());
        appVersion.updateVersion(appVersionSave.getVersion());
        appVersionRepository.save(appVersion);
    }
}
