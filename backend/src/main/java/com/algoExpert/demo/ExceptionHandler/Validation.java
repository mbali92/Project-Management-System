package com.algoExpert.demo.ExceptionHandler;

import com.algoExpert.demo.Entity.HttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSendException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.SocketException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class Validation {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String,String> handleValidation(ConstraintViolationException argumentException){
        Map<String,String> errorMap = new HashMap<>();
        argumentException.getConstraintViolations().forEach(errorMessage->{
            errorMap.put(errorMessage.getPropertyPath().toString(),errorMessage.getMessage());
        });
        return errorMap;
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidArgument.class)
    public HttpResponse handleInvalidArgument(InvalidArgument argument,HttpServletRequest request){
            return HttpResponse.builder()
                    .timeStamp(LocalTime.now().toString())
                    .status(HttpStatus.UNAUTHORIZED)
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .message("Requested Item Not Found")
                    .developerMessage(argument.getMessage())
                    .urlInstance(request.getServletPath())
                    .method(request.getMethod())
                    .build();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserAlreadyEnabled.class)
    public HttpResponse userAlreadyEnabled(UserAlreadyEnabled argument, HttpServletRequest request){
        return HttpResponse.builder()
                .timeStamp(LocalTime.now().toString())
                .status(HttpStatus.CONFLICT)
                .statusCode(HttpStatus.CONFLICT.value())
                .message("Requested Item Found")
                .developerMessage(argument.getMessage())
                .urlInstance(request.getServletPath())
                .method(request.getMethod())
                .build();
    }

    @ResponseStatus(HttpStatus.LOCKED)
    @ExceptionHandler(DisabledException.class)
    public HttpResponse disabledException(DisabledException disabledException,HttpServletRequest request){
        return HttpResponse.builder()
                .timeStamp(LocalTime.now().toString())
                .status(HttpStatus.LOCKED)
                .statusCode(HttpStatus.LOCKED.value())
                .message("User account not activated")
                .developerMessage(disabledException.getMessage())
                .urlInstance(request.getServletPath())
                .method(request.getMethod())
                .build();
    }
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    @ExceptionHandler(SocketException.class)
    public Map<String,String> socketException(SocketException socketException){
        Map<String,String> errorMap = new HashMap<>();
        errorMap.put("errorMessage", socketException.getMessage());
        return errorMap;
    }
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException.class)
    public Map<String,String> deniedException(AccessDeniedException deniedException){
        Map<String,String> errorMap = new HashMap<>();
        errorMap.put("errorMessage", deniedException.getMessage());
        return errorMap;
    }
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public HttpResponse badCredentialsException(BadCredentialsException badCredentialsException,HttpServletRequest request){
        return HttpResponse.builder()
                .timeStamp(LocalTime.now().toString())
                .status(HttpStatus.UNAUTHORIZED)
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message(badCredentialsException.getMessage())
                .developerMessage("Incorrect Username or Password")
                .urlInstance(request.getServletPath())
                .method(request.getMethod())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public HttpResponse serviceException(InternalAuthenticationServiceException serviceException ,HttpServletRequest request){
        return HttpResponse.builder()
                .timeStamp(LocalTime.now().toString())
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message("No account found for this username")
                .developerMessage(serviceException.getMessage())
                .urlInstance(request.getServletPath())
                .method(request.getMethod())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsernameNotFoundException.class)
    public HttpResponse usernameNotFoundException(UsernameNotFoundException usernameNotFoundException , HttpServletRequest request){
        return HttpResponse.builder()
                .timeStamp(LocalTime.now().toString())
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message("User Not Found")
                .developerMessage(usernameNotFoundException.getMessage())
                .urlInstance(request.getServletPath())
                .method(request.getMethod())
                .build();
    }
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(RuntimeException.class)
    public HttpResponse runtimeException(RuntimeException runtimeException , HttpServletRequest request){
        return HttpResponse.builder()
                .timeStamp(LocalTime.now().toString())
                .status(HttpStatus.NOT_IMPLEMENTED)
                .statusCode(HttpStatus.NOT_IMPLEMENTED.value())
                .message(runtimeException.getMessage())
                .developerMessage("Unidentified error for any uncaught exception")
                .urlInstance(request.getServletPath())
                .method(request.getMethod())
                .build();
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(JwtTokenMalformedException .class)
    public HttpResponse malformedJwtException(JwtTokenMalformedException  malformedJwtException , HttpServletRequest request){
        return HttpResponse.builder()
                .timeStamp(LocalTime.now().toString())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(malformedJwtException.getMessage())
                .developerMessage("jwt error")
                .urlInstance(request.getServletPath())
                .method(request.getMethod())
                .build();
    }
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(EmptyFieldNotRequired.class)
    public HttpResponse emptyFieldNotRequired(EmptyFieldNotRequired emptyFieldNotRequired , HttpServletRequest request){
        return HttpResponse.builder()
                .timeStamp(LocalTime.now().toString())
                .status(HttpStatus.FORBIDDEN)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .message(emptyFieldNotRequired.getMessage())
                .developerMessage("Missing inputs")
                .urlInstance(request.getServletPath())
                .method(request.getMethod())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public HttpResponse noSuchElementException(NoSuchElementException noSuchElementException , HttpServletRequest request){
        return HttpResponse.builder()
                .timeStamp(LocalTime.now().toString())
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message("User not registered")
                .developerMessage(noSuchElementException.getMessage())
                .urlInstance(request.getServletPath())
                .method(request.getMethod())
                .build();
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(JwtTokenExpiredException.class)
    public Map<String,String> jwtException(JwtTokenExpiredException jwtException){
        Map<String,String> errorMap = new HashMap<>();
        errorMap.put("errorMessage", jwtException.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    @ExceptionHandler(MailSendException.class)
    public Map<String,String> mailSendException(MailSendException mailSendException){
        Map<String,String> errorMap = new HashMap<>();
        errorMap.put("errorMessage", mailSendException.getMessage());
        return errorMap;
    }
}
