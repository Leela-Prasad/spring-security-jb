package io.jb.spring.security.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "User")
@Data
public class MyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String userName;
    private String password;
    private String roles;
    private Boolean active;



}
