package com.canu.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "questionnaire")
public class FAQModel {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @NotBlank(message = "concern is required")
    @Column(name = "concern")
    String concern;

    @NotBlank(message = "guideline is required")
    @Column(name = "guideline")
    String guideline;

    @NotNull(message = "locale is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "locale")
    CountryModel.Locale locale;
}
