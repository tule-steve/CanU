package com.canu.model;

import lombok.Data;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "skill_set")
public class SkillSetModel {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "name")
    String name;

    @ManyToMany(mappedBy = "skillSets", cascade = CascadeType.PERSIST)
    private Set<CanIModel> canIs = new HashSet<>();
}
