package io.jb.spring.security.service;

import io.jb.spring.security.model.MyUser;
import io.jb.spring.security.model.MyUserDetails;
import io.jb.spring.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<MyUser> user = userRepository.findByUserName(userName);

        System.out.println("User : " + user);

        user.orElseThrow(() -> new RuntimeException("Not Found : " + userName));

        return new MyUserDetails(user.get());
    }
}
