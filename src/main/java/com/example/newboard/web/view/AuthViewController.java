package com.example.newboard.web.view;

import com.example.newboard.service.UserService;
import com.example.newboard.web.dto.JoinRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthViewController {
    private final UserService userService;

    @GetMapping("/login")
    public String login(){ return "login"; }

    @GetMapping("/join")
    public String joinForm(Model model){
        model.addAttribute("joinRequest", new JoinRequest());
        return "join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute JoinRequest joinRequest, BindingResult br, Model model){
        if (br.hasErrors()) return "join";
        try {
            userService.join(joinRequest);
        } catch (IllegalArgumentException e){
            model.addAttribute("error", e.getMessage());
            return "join";
        }
        return "redirect:/login";
    }
}


