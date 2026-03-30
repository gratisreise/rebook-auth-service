package com.example.rebookauthservice.domain.service.oauth;


import com.example.rebookauthservice.common.annotation.OAuthServiceType;
import com.example.rebookauthservice.common.enums.OauthProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class OAuthServiceFactory {
  private final Map<OauthProvider, OAuthService> serviceMap = new HashMap<>();

  public OAuthServiceFactory(List<OAuthService> services) {
    for (OAuthService service : services) {
      OAuthServiceType type = service.getClass().getAnnotation(OAuthServiceType.class);
      if (type != null) {
        serviceMap.put(type.value(), service);
      }
    }
  }

  public OAuthService getOAuthService(OauthProvider provider) {
    return serviceMap.get(provider);
  }
}
