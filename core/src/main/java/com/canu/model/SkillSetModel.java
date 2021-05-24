package com.canu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "skill_set")
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

//    @Column(name = "short_intro")
//    String shortIntro;

    @Column(name = "slug")
    String slug;

    @Column(name = "quantity")
    Integer quantity;

    @Column(name = "icon_url")
    String iconUrl;

    @Column(name = "description_kr")
    String descriptionKr;

    @Column(name = "title_kr")
    String titleKr;

    @Column(name = "description_jp")
    String descriptionJp;

    @Column(name = "title_jp")
    String titleJp;

    @Column(name = "description_vn")
    String descriptionVn;

    @Column(name = "title_vn")
    String titleVn;

    @Column(name = "description_cn")
    String descriptionCn;

    @Column(name = "title_cn")
    String titleCn;

    @Column(name = "is_delete")
    Boolean isDelete = false;


    @Column(name = "c_point_rate", columnDefinition = "decimal(5, 2)", precision = 5, scale = 2)
    BigDecimal cpointRate;


//    @Column(name = "icon_awesome_class")
//    String icon_awesome_class;
//
//    @Column(name = "image_background")
//    String image_background;

    @JsonIgnore
    @ManyToMany(mappedBy = "skillSets", cascade = CascadeType.PERSIST)
    private Set<CanIModel> canIs = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "skillSets", cascade = CascadeType.PERSIST)
    private Set<JobModel> jobs = new HashSet<>();

    @Transient
    private Set<Long> service;
}
