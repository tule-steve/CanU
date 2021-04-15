package com.canu.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "country")
public class CountryModel {

    public enum Locale {
        en,
        vn,
        kr,
        ja,
        all
    }

    @Id
    @Column(name = "country_id")
    String countryId;

    @Column(name = "country_name")
    String countryName;
}
