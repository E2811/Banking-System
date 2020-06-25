package com.midterm.bankingSystem.exception;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public void handDataNotFoundException(DataNotFoundException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(LowBalance.class)
    public void handLowBalance(LowBalance e, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(InvalidAccountUser.class)
    public void handLInvalidAccountUser(InvalidAccountUser e, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }
    @ExceptionHandler(NotEnoughData.class)
    public void handLNotEnoughData(NotEnoughData e, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
    @ExceptionHandler(UserWithNameUsed.class)
    public void handLUserFound(UserWithNameUsed e, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
    @ExceptionHandler(FraudDetection.class)
    public void handFraudDetention(FraudDetection e, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
}


