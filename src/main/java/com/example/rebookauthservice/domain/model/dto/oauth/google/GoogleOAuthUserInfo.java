package com.example.rebookauthservice.domain.model.dto.oauth.google;

import com.example.rebookauthservice.common.enums.Provider;
import com.example.rebookauthservice.domain.model.dto.oauth.OAuthUserInfo;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GoogleOAuthUserInfo implements OAuthUserInfo {
    private final GoogleUserInfo userInfo;

    @Override
    public String getId() {
        return userInfo.id();
    }

    @Override
    public String getName() {
        return Provider.GOOGLE.name() + userInfo.id();
    }

    @Override
    public String getImageUrl() {
        return userInfo.picture();
    }
}
