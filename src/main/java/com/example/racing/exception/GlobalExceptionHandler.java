package com.example.racing.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileSizeExceededException.class)
    public String handleFileSizeExceeded(FileSizeExceededException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("team", new com.example.racing.model.Team());
        return "teams/form";
    }

    @ExceptionHandler(LogoMissingException.class)
    public String handleLogoMissing(LogoMissingException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "teams/form";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationException(MethodArgumentNotValidException ex, Model model) {
        Object target = ex.getBindingResult().getTarget();
        model.addAttribute("team", target);
        model.addAttribute("org.springframework.validation.BindingResult.team", ex.getBindingResult());
        return "teams/form";
    }
}
