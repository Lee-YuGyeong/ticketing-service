package com.yugyeong.ticketing_service.presentation.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    //user, manager, admin 권한만 접근 가능
    @GetMapping("/api/v1/user")
    public String user() {
        return "user";
    }

    //manager, admin 권한만 접근 가능
    @GetMapping("/api/v1/manager")
    public String manager() {
        return "manager";
    }

    //admin 권한만 접근 가능
    @GetMapping("/api/v1/admin")
    public String admin() {
        return "admin";
    }
}
