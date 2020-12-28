package com.canu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "cani_profile")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CanIModel {

//    @JsonIgnore
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "name")
    String name;

    @Column(name = "salary")
    Long price;

    @Column(name = "currency")
    String currency;

    @Column(name = "company")
    String companyName;

    @Column(name = "phone")
    String phone;

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

    @Column(name = "bank_branch")
    String bankBranch;

    @Column(name = "bank_number")
    Long accountNumber;

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

    @Column(name = "avatar")
    String avatar;

    @Column(name = "title")
    String title;

    @Column(name = "rating")
    BigDecimal rating;

    @Column(name = "is_phone_verified")
    Boolean phoneVerified = false;

    @Column(name = "sentSmsCodeAt")
    LocalDateTime sentSmsCodeAt;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;

//    @Column(name = "updated_at")
//    @UpdateTimestamp
//    LocalDateTime updatedAt;

    //ManyToMany relationship
    //Voucher is owner of this relationship -> if voucher is persisted, the JoinTable voucher_category_map is also updated.
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "cani_skillset",
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

    public void setCanUModel(CanUModel canu) {
        this.canUModel = canu;
        canu.setCanIModel(this);
    }

    @Transient
    private Map<String, List<FileModel>> files;

    @Transient
    private Map<String, Integer> jobStatus;

    @JsonIgnore
    @ManyToMany(mappedBy = "favoriteCanIs")
    private Set<CanUModel> favoriteCanU = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CanIModel)) return false;
        return id != null && id.equals(((CanIModel) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
