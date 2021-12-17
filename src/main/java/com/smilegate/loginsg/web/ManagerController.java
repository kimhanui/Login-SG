package com.smilegate.loginsg.web;

import com.smilegate.loginsg.service.ManagerService;
import com.smilegate.loginsg.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class ManagerController {

    private final ManagerService managerService;

    @GetMapping("/userlist")
    public List<UserDto> getUserList(){
        return managerService.getUserList();
    }
}
