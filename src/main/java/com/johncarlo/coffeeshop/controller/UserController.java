package com.johncarlo.coffeeshop.controller;

import com.johncarlo.coffeeshop.model.*;
import com.johncarlo.coffeeshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public String home(HttpSession httpSession, Model model) {
        Order order = (Order) httpSession.getAttribute("order");
        if(order != null) {
            model.addAttribute("itemCount", order.getItems().stream().mapToInt(Item::getQuantity).sum());
        }
        return "index";
    }

    @GetMapping("/signup")
    public String signup(Model model, HttpSession httpSession) {
        model.addAttribute("user", new User());
        Order order = (Order) httpSession.getAttribute("order");
        if(order != null) {
            model.addAttribute("itemCount", order.getItems().stream().mapToInt(Item::getQuantity).sum());
        }
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute User user, Model model) {
        String error = userService.signupUser(user);
        if (error != null) {
            model.addAttribute("user", user);
            model.addAttribute("error", error);
            return "signup";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/login")
    public String login(Model model, HttpSession httpSession) {
        model.addAttribute("user", new User());
        Order order = (Order) httpSession.getAttribute("order");
        if(order != null) {
            model.addAttribute("itemCount", order.getItems().stream().mapToInt(Item::getQuantity).sum());
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute User user, HttpSession httpSession, Model model) {
        User validUser = userService.loginUser(user);
        if (validUser == null) {
            model.addAttribute("user", user);
            model.addAttribute("error", true);
            return "login";
        } else {
            httpSession.setAttribute("user", validUser);
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logoutGet(HttpSession httpSession) {
        httpSession.removeAttribute("user");
        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logoutPost(HttpSession httpSession) {
        httpSession.removeAttribute("user");
        return "redirect:/";
    }

}
