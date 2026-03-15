package com.example.rebookauthservice.domain.model.dto.oauth.naver;

import com.example.rebookauthservice.common.enums.Provider;
import com.example.rebookauthservice.domain.model.dto.oauth.OAuthUserInfo;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NaverOAuthUserInfo implements OAuthUserInfo {
    private final NaverUserInfo userInfo;

    @Override
    public String getId() {
        return userInfo.response().id();
    }

    @Override
    public String getName() {
        return Provider.NAVER.name() + userInfo.response().id();
    }

    @Override
    public String getImageUrl() {
        return userInfo.response().profileImage();
    }
}
