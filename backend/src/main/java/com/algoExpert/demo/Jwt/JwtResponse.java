package com.algoExpert.demo.Jwt;

import com.algoExpert.demo.role.Role;
import lombok.*;

import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtResponse {

    private String jwtToken;
    private String refreshToken;
    protected String email;
    protected int userId;
    protected String fullname;
    protected String responseMessage;
    protected List<Role> role;

}
