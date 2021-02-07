package com.canu.model;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "country")
public class CountryModel {

    @Id
    @Column(name = "country_id")
    String countryId;

    @Column(name = "country_name")
    String countryName;
}
