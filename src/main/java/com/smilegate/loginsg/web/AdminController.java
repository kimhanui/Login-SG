package com.smilegate.loginsg.web;

import com.smilegate.loginsg.service.AdminService;
import com.smilegate.loginsg.web.dto.AdminUpdateUserDto;
import com.smilegate.loginsg.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/userlist")
    public ResponseEntity<List<UserDto>> getUserList(){
        return ResponseEntity.ok(adminService.getUserList());
    }

    @PostMapping("/updateuser")
    public ResponseEntity<UserDto> updateUserInfo(@Valid @RequestBody AdminUpdateUserDto dto) {
        return ResponseEntity.ok(adminService.updateUserInfo(dto));
    }
}
