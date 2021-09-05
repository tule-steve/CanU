package com.canu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "template_multi_lang")
public class TemplateMultiLangModel {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "template")
    String template;

    @Column(name = "title")
    String title;

    @Column(name = "description")
    String description;

    @Column(name = "email")
    String email;

    @Column(name = "lang")
    @Enumerated(EnumType.STRING)
    CountryModel.Locale locale;

    @JsonIgnore
//    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    TemplateModel parent;

}
