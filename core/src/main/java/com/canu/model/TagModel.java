package com.canu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "tag")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TagModel {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "tag")
    String tag;

    @JsonIgnore
    @ManyToMany(mappedBy = "tags", cascade = CascadeType.PERSIST)
    private Set<JobModel> jobs = new HashSet<>();
}
