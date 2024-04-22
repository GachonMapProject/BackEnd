package com.f2z.gach.Admin;

import com.f2z.gach.EnumType.Authorization;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
@SessionAttributes
public class AdminController {
    private final AdminRepository adminRepository;

    @GetMapping("/main-page")
    public String mainPage(){
        return "main-page";
    }

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("loginDto", new loginDTO());
        return "log-in";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginDto") loginDTO adminDto, BindingResult result,
                        Model model){
        if(result.hasErrors()){
            return "log-in";
        }

        Admin admin = adminRepository.findByAdminId(adminDto.getAdminId());
        if(admin == null){
            model.addAttribute("idMessage", "아이디가 존재하지 않습니다.");
            return "log-in";
        }
        if(!admin.getAdminPassword().equals(adminDto.getAdminPassword())){
            model.addAttribute("message", "비밀번호가 다릅니다.");
            return "log-in";
        }
        if(admin.getAdminAuthorization().equals(Authorization.WAITER)){
            return "waiter";
        } else if (admin.getAdminAuthorization().equals(Authorization.GUEST)) {
            return "dashboard-guest";
        } else{
            return "dashboard";
        }
    }

    @GetMapping("/signup")
    public String signup(Model model){
        model.addAttribute("adminDto", new AdminDTO());
        return "sign-up";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("adminDto") AdminDTO adminDto, BindingResult result, Model model){
        if(result.hasErrors()){
            return "sign-up";
        } else if(!adminDto.getAdminPassword().equals(adminDto.getAdminPasswordCheck())){
            model.addAttribute("passMessage", "비밀번호가 다릅니다.");
            return "sign-up"; //비밀번호가 다른 경우
        }
        Admin admin = adminRepository.findByAdminId(adminDto.getAdminId());

        if(admin != null){
            model.addAttribute("idMessage", "이미 존재하는 아이디입니다.");
            return "sign-up";
        }

        adminDto.setAdminAuthorization(Authorization.WAITER);
        adminRepository.save(adminDto.toEntity());
        model.addAttribute("message", "회원가입이 완료되었습니다. 관리자 승인까지 잠시만 기다려주세요.");
        model.addAttribute("loginDto", new loginDTO());
        return "log-in";
    }

//    @PostMapping("/login")
//    public String login(@ModelAttribute @Valid Admin admin,
//                        BindingResult result,
//                        Model model){
//        if(result.hasErrors()){
//            model.addAttribute("errors", result.getAllErrors());
//            return "admin_login";
//        }
//        else {
//            admin.setAdminAuthorization(Authorization.valueOf("WAITER"));
//            adminRepository.save(admin);
//            model.addAttribute("number",adminRepository.getAdminSize());
//            return "admin_info";
//        }
//    }

    @GetMapping("/list")
    public String adminList(Model model){
        model.addAttribute("adminList", adminRepository.findAllByAdminAuthorization(Authorization.ADMIN));
        model.addAttribute("guestList", adminRepository.findAllByAdminAuthorization(Authorization.GUEST));
        model.addAttribute("waiterList", adminRepository.findAllByAdminAuthorization(Authorization.WAITER));
        return "admin-manage";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleError404(HttpServletRequest request, Exception e) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", e);
        mav.addObject("status", 404);
        mav.addObject("message", "요청하신 페이지를 찾을 수 없습니다.");
        mav.addObject("url", request.getRequestURL());
        mav.setViewName("error/404");
        log.info("404 error");
        return mav;
    }
}
