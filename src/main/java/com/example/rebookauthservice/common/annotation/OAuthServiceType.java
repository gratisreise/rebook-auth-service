package com.example.rebookauthservice.common.annotation;


import com.example.rebookauthservice.common.enums.OauthProvider;
import com.example.rebookauthservice.common.enums.Provider;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface OAuthServiceType {
    OauthProvider value();
}
