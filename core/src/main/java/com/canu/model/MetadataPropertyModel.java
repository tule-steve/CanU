package com.canu.model;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "properties")
public class MetadataPropertyModel {
    @Id
    @Column(name = "field")
    String key;

    @Column(name = "value")
    String value;
}
