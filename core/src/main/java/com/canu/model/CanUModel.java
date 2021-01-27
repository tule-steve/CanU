package com.canu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "performanceTest_u")
public class CanUModel {

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

    @Column(name = "provider_type")
    String providerType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cani_id")
    private CanIModel canIModel;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private AuthProviderModel socialData;





}
