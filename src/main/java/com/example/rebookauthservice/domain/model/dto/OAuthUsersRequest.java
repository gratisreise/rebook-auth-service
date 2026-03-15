package com.example.rebookauthservice.domain.model.dto;

import com.example.rebookauthservice.domain.model.dto.oauth.OAuthUserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OAuthUsersRequest {

    private String nickname;
    private String profileImage;

    public static OAuthUsersRequest from(OAuthUserInfo userInfo){
        return new OAuthUsersRequest(userInfo.getName(), userInfo.getImageUrl());
    }
}
