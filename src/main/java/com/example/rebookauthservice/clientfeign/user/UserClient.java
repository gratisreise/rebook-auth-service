package com.example.rebookauthservice.clientfeign.user;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserClient {
    @PostMapping("/internal/users/sign-up")
    String createUser(@RequestBody UsersCreateRequest request);
}