package com.canu.model;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "location")
public class AddressModel {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "cani_id")
    Long cani_id;

    @Column(name = "job_id")
    Long job_id;

    @Column(name = "address1")
    String address1;

    @Column(name = "address2")
    String address2;

    @Column(name = "country")
    String country;

    @Column(name = "city")
    String city;
}
