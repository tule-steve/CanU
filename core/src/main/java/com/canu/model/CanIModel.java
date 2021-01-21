package com.canu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "performanceTest_i")
public class CanIModel {

    @JsonIgnore
    @Id
    @Column(name = "id")
    Long id;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "salary")
    String salary;

    @Column(name = "company")
    String company;

    @Column(name = "phone")
    Integer phone;

    @Column(name = "nation")
    String nation;

    @Column(name = "city")
    String city;

    @Column(name = "address")
    String address;

    @Column(name = "tax_num")
    String taxNum;

    @Column(name = "bank_number")
    String bankNumber;

    @Column(name = "bank_owner")
    String bankOwner;

    @Column(name = "service_name")
    String serviceName;


    @Column(name = "description")
    String description;

    @Column(name = "service_nation")
    String serviceNation;

    @Column(name = "service_city")
    String serviceCity;

    @Column(name = "policy")
    String policy;



    //ManyToMany relationship
    //Voucher is owner of this relationship -> if voucher is persisted, the JoinTable voucher_category_map is also updated.
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "cani_skill_set",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_set_id"))
    private Set<SkillSetModel> skillSets = new HashSet<>();


    @OneToOne(mappedBy = "canIModel", fetch = FetchType.LAZY)
    private CanUModel canUModel;
}
