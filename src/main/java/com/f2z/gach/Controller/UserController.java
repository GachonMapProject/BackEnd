package com.f2z.gach.Controller;

import com.f2z.gach.DTO.User.UserDTO;
import com.f2z.gach.DTO.User.UserGuestDTO;
import com.f2z.gach.DTO.User.UserInfoDTO;
import com.f2z.gach.Entity.EnumType.Properties;
import com.f2z.gach.Entity.User.User;
import com.f2z.gach.Entity.User.UserInfo;
import com.f2z.gach.ExceptionHandler.ApiException;
import com.f2z.gach.Response.ResponseEntity;
import com.f2z.gach.Service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/test")
    public String test() {
        return "static/image/Gach가자_홈페이지용.png";
    }


    //가천대 로그인 요청
    @PostMapping("/login")
    public ResponseEntity<?> loginMember(@RequestBody UserDTO userDto) {
        log.info("login try " + userDto);
        try{
            User user= userService.loginMember(userDto);
            log.info("login success " + userDto);
            // info가 DB에 없으면 202
            // DB에 있으면 200
            return new ResponseEntity<>(true, HttpStatus.OK, "login success", user.getUserId());
//            return new ResponseEntity<>(true, HttpStatus.OK, "login success", userService.ExistUserInfo(user.getUsername());
//            if(userService.checkExistUserInfo(user.getUsername())) {
//                return new ResponseEntity<>(true, HttpStatus.OK, "login success", Collections.singletonList(new UserDTO(user.getUserId())));
//            } else {
//                user.setUserId((long) -1);
//                return new ResponseEntity<>(true, HttpStatus.OK, "login success but no Info", Collections.singletonList(new UserDTO(user.getUserId())));
//            }
        } catch (Exception error) {
            log.info("login error : " + error.getMessage());
            throw new ApiException(false, Properties.BAD_REQUEST.getCode(), Properties.BAD_REQUEST.getMessage(), error);
        }
    }

    // 최초 로그인 회원 정보 기재 요청
    @PostMapping("/{userId}")
    public ResponseEntity<?> saveMemberInfo(@PathVariable("userId") Long userId, @RequestBody UserInfoDTO userInfoDTO) {
        try{
            Long userCode = userService.saveMemberInfo(userId, userInfoDTO);
            return new ResponseEntity<>(true, HttpStatus.OK, "info success", userCode);
        } catch (Exception error) {
            log.info("info error : " + error.getMessage());
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST, error.getMessage(), error);
        }
    }

    // 최초 비회원 로그인 및 정보 기재 요청
    @PostMapping("/guest")
    public ResponseEntity<?> saveGuestInfo(@RequestBody UserGuestDTO userGuestDTO) {
        try{
            Integer guestId = userService.saveGuestInfo(userGuestDTO);
            return new ResponseEntity<>(true, HttpStatus.OK, "info success", guestId);
        } catch (Exception error) {
            log.info("info error : " + error.getMessage());
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST,error.getMessage(), error);
        }
    }

    // 회원 정보 수정 요청
    @PatchMapping("/{userId}")
    public ResponseEntity<?> updateMemberInfo(@PathVariable("userId") Long userId, @RequestBody UserInfoDTO userInfoDTO) {
        try{
            Long userCode = userService.updateMemberInfo(userId, userInfoDTO);
            return new ResponseEntity<>(true, HttpStatus.OK, "update success", userCode);
        } catch (Exception error) {
            log.info("update error : " + error.getMessage());
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST, error.getMessage(), error);

        }
    }

    // 회원 정보 조회 요청

    // 회원 정보 삭제 요청
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteMember(@PathVariable("userId") Long userId) {
        try{
            userService.deleteMember(userId);
            return new ResponseEntity<>(true, HttpStatus.OK, "delete success", userId);
        } catch (Exception error) {
            log.info("delete error : " + error.getMessage());
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST, error.getMessage(), error);

        }
    }



}
