package com.algoExpert.demo.Controller;

import com.algoExpert.demo.AppNotification.DeadlineTaskReminder;
import com.algoExpert.demo.Dto.RefreshTokenRequest;
import com.algoExpert.demo.Entity.HttpResponse;
import com.algoExpert.demo.Entity.Project;
import com.algoExpert.demo.Entity.RefreshToken;
import com.algoExpert.demo.Entity.User;
import com.algoExpert.demo.ExceptionHandler.InvalidArgument;
import com.algoExpert.demo.Jwt.JwtResponse;
import com.algoExpert.demo.Records.AuthRequest;
import com.algoExpert.demo.Records.RegistrationRequest;
import com.algoExpert.demo.Repository.Service.AuthService;
import com.algoExpert.demo.Jwt.JwtService;
import com.algoExpert.demo.Repository.Service.Impl.RefreshTokenSevice;
import com.algoExpert.demo.Repository.Service.TaskService;
import com.algoExpert.demo.Repository.Service.UserNotificationService;
import com.algoExpert.demo.UserAccount.AccountInfo.Service.PasswordResetService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private DeadlineTaskReminder taskReminder;
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserNotificationService userNotificationService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    PasswordResetService passwordResetService;


    @Autowired
    private RefreshTokenSevice tokenService;
//    create user
    @PostMapping("/registerUser")
    public User registerUser(@RequestBody RegistrationRequest request) throws  InvalidArgument, MessagingException, IOException {
        return authService.registerUser(request);
    }

    @PostMapping("/loginUser")
    public HttpResponse userLogin(@RequestBody AuthRequest userCredentials) throws Exception {
        return authService.loginUser(userCredentials);
    }

    @PostMapping("/userPasswordReset")
    public String createResetPasswordEmail(@RequestParam("email") String userEmail){
        return passwordResetService.sendPasswordRecoveryEmail(userEmail);
    }

    @GetMapping("/forgotPassword")
    public RedirectView passwordChangePage(@RequestParam("token") String token){
        return new RedirectView("http://localhost:5173/privacy?token=" + token);
    }
    @GetMapping("/refreshTokenLogin/{refreshToken}")
    public JwtResponse refreshTokenLogin(@PathVariable String refreshToken){
        return authService.refreshTokenLogin(String.format(refreshToken));
    }

    @PostMapping("/changeForgotPassword/{token}/{newPassword}")
    public String changeForgotPassword(@PathVariable String token, @PathVariable String newPassword){
        return authService.forgotPasswordChange(token,newPassword);
    }


}
