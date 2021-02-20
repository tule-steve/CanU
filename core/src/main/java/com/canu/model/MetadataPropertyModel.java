package com.canu.model;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "properties")
public class MetadataPropertyModel {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "field")
    String key;

    @Column(name = "value")
    String value;
}
