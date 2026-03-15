package com.example.rebookauthservice.domain.controller;

import com.example.rebookauthservice.domain.service.PassportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("passports")
@RequiredArgsConstructor
public class PassportController {
    private final PassportService passportService;

    // 패스포트 생성
    @PostMapping
    public String issuePassport(@RequestParam String jwt){
        return passportService.issuePassport(jwt);
    }

}
