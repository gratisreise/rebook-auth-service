package com.example.rebookauthservice.domain.model.dto.oauth.kako;

import com.example.rebookauthservice.common.enums.Provider;
import com.example.rebookauthservice.domain.model.dto.oauth.OAuthUserInfo;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KakaoOAuthUserInfo implements OAuthUserInfo {
    private final KakaoUserInfo userInfo;

    @Override
    public String getId() {
        return userInfo.id().toString();
    }

    @Override
    public String getName() {
        return Provider.KAKAO.name() + userInfo.id();
    }

    @Override
    public String getImageUrl() {
        return userInfo.properties().profileImage();
    }
}
