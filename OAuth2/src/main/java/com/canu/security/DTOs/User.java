package com.canu.security.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "performanceTest_u_auth")
public class User {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "email")
    String email;

    @Column(name = "password")
    String password;

    @Column(name = "phone")
    Integer phone;

    @Column(name = "nation")
    String nation;

    @Column(name = "city")
    String city;

    @Column(name = "address")
    String address;

    @NotNull
    @Enumerated(EnumType.STRING)
    AuthProvider provider;

    @Column(name = "provider_id")
    String providerId;

    @Column(name = "name")
    private String name;

    @Column(name = "image_url")
    String imageUrl;





}
