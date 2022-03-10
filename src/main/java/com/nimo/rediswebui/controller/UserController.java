package com.nimo.rediswebui.controller;

import com.nimo.rediswebui.entity.UserInfo;
import com.nimo.rediswebui.exception.Result;
import com.nimo.rediswebui.req.UserLoginReq;
import com.nimo.rediswebui.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author nimo
 * @version V1.0
 * @date 2022-01-26 13:42
 * @Description: userController
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/")
    public ModelAndView home() {
        return new ModelAndView("login");
    }

    @GetMapping(value = "/index")
    public ModelAndView index() {
        return new ModelAndView("login");
    }

    @GetMapping(value = "/reg")
    public ModelAndView reg() {
        return new ModelAndView("register");
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Validated UserLoginReq req) {
        userService.register(req);
        return Result.ok();
    }

    @PostMapping("/login")
    public Result<String> login(HttpServletRequest request,@RequestBody @Validated UserLoginReq req) {
        UserInfo user =userService.login(req);
        HttpSession session= request.getSession();
        session.setAttribute("user", user);
        return Result.ok(user.getUserName());
    }


}
