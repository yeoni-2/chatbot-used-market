package com.example.chatbot_used_market.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController implements ErrorController {
  @GetMapping("/error")
  public String handleError(HttpServletRequest request, Model model){
    Integer status = (Integer)request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

    if (status != null){
      if (status == HttpStatus.NOT_FOUND.value()) model.addAttribute("error", "404 Not found");
    }

    return "error";
  }
}
