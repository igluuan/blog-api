package com.devluan.blog_api.infrastructure.web;

import com.devluan.blog_api.application.dto.post.request.PostRegisterRequest;
import com.devluan.blog_api.application.dto.user.request.UserRegisterRequest;
import com.devluan.blog_api.application.service.post.PostApplicationService;
import com.devluan.blog_api.application.service.user.UserApplicationService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/")
@AllArgsConstructor
public class WebController {

    private final PostApplicationService postApplicationService;
    private final UserApplicationService userApplicationService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("posts", postApplicationService.getAllPosts());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String register(UserRegisterRequest request) {
        userApplicationService.createUser(request);
        return "redirect:/login";
    }

    @GetMapping("/posts/new")
    public String newPostForm(Model model) {
        model.addAttribute("post", new PostRegisterRequest(null, null, null, null));
        return "new-post";
    }

    @GetMapping("/myaccount")
    public String myAccount(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        userApplicationService.findUserByUsername(username).ifPresent(user -> model.addAttribute("user", user));
        return "myaccount";
    }
