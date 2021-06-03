package com.canu.model;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "authentication_provider")
public class AuthProviderModel {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "provider_type")
    String providerType;

    @Column(name = "provider_key")
    String providerKey;


    ///Todo it always fetch -> need change to manytoone
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private CanUModel user;
}
