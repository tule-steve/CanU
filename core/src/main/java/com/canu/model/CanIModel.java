package com.canu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CanIModel {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "name")
    String name;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "salary")
    String price;

    @Column(name = "company")
    String companyName;

    @Column(name = "phone")
    Integer phone;

    @Column(name = "nation")
    String national;

    @Column(name = "city")
    String area;

    @Column(name = "address")
    String address;

    @Column(name = "tax_num")
    String tax;

    @Column(name = "bank_name")
    String bankName;

    @Column(name = "bank_number")
    String accountNumber;

    @Column(name = "bank_owner")
    String accountName;

    @Column(name = "service_name")
    String serviceType;


    @Column(name = "description")
    String description;

    @Column(name = "service_nation")
    String nationalService;

    @Column(name = "service_city")
    String areaService;

    @Column(name = "policy")
    String policy;



    //ManyToMany relationship
    //Voucher is owner of this relationship -> if voucher is persisted, the JoinTable voucher_category_map is also updated.
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "performance_skill",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_set_id"))
    private List<SkillSetModel> skillSets = new ArrayList<>();

    @Transient
    private Set<Long> service;

    @Transient
    private String email;

    @JsonIgnore
    @OneToOne(mappedBy = "canIModel", fetch = FetchType.LAZY)
    private CanUModel canUModel;

    public void setCanUModel(CanUModel canu){
        this.canUModel = canu;
        canu.setCanIModel(this);
    }
}
