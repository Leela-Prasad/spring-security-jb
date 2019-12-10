package io.jb.spring.security.web.controller;

import io.jb.spring.security.models.AuthenticationRequest;
import io.jb.spring.security.models.AuthenticationResponse;
import io.jb.spring.security.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        }catch(BadCredentialsException e) {
            throw  new RuntimeException("Incorrect Username or Password", e);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        String token = jwtUtil.generateToken(userDetails);


        return new AuthenticationResponse(token);

    }

}
