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
@Table(name = "performanceTest_d")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SkillSetModel {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "description")
    String description;

    @Column(name = "title")
    String title;

    @Column(name = "short_intro")
    String shortIntro;

    @Column(name = "slug")
    String slug;

    @Column(name = "quantity")
    Integer quantity;

    @Column(name = "icon_url")
    Integer iconUrl;

    @Column(name = "icon_awesome_class")
    String icon_awesome_class;

    @Column(name = "image_background")
    String image_background;

    @JsonIgnore
    @ManyToMany(mappedBy = "skillSets", cascade = CascadeType.PERSIST)
    private Set<CanIModel> canIs = new HashSet<>();
}
