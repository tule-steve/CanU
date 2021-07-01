package com.canu.model;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "template")
public class TemplateModel {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "template")
    String template;

    @Column(name = "title")
    String title;

    @Column(name = "description")
    String description;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    NotificationDetailModel.Type type;

    @Column(name = "to_canu")
    Boolean toCanu = false;

    @Column(name = "to_cani")
    Boolean toCani = false;

    @Column(name = "to_admin")
    Boolean toAdmin = false;


}
