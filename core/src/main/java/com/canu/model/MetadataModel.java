package com.canu.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "metadata")
public class MetadataModel {
    @Id
    @Column(name = "field")
    String key;

    @Column(name = "value")
    String value;
}
