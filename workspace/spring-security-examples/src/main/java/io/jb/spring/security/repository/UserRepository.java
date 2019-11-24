package io.jb.spring.security.repository;

import io.jb.spring.security.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<MyUser, Integer> {

    public Optional<MyUser> findByUserName(String userName);
    
}
