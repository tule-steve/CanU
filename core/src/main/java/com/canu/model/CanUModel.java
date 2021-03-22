package com.canu.model;

import com.canu.dto.responses.Member;
import com.canu.exception.GlobalValidationException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;

@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "MemberMapping",
                classes = {
                        @ConstructorResult(
                                targetClass = Member.class,
                                columns = {
                                        @ColumnResult(name = "userId", type = Long.class),
                                        @ColumnResult(name = "firstName"),
                                        @ColumnResult(name = "lastName"),
                                        @ColumnResult(name = "email"),
                                        @ColumnResult(name = "createdAt", type = LocalDateTime.class),
                                        @ColumnResult(name = "createdJob", type = Integer.class),
                                        @ColumnResult(name = "finishedJob", type = Integer.class),
                                        @ColumnResult(name = "processingJob", type = Integer.class),
                                        @ColumnResult(name = "caniId", type = Long.class)
                                }
                        )
                }
        ),
        @SqlResultSetMapping(name = "SqlResultSetMapping.count", columns = @ColumnResult(name = "cnt"))
})

@NamedNativeQueries({
        @NamedNativeQuery(name = "CanUModel.getMembership", query =
                "select u.id as userId, u.first_name as name, u.email as email, u.created_at as createdAt, " +
                "   0 as createdJob, 0 as finishedJob, 0 as processingJob, u.cani_id as caniId" +
                " from user u ",
                resultSetMapping = "MemberMapping"),

        @NamedNativeQuery(name = "CanUModel.getMembership.count", query =
                "select count(u.id) as cnt from user u ",
                resultSetMapping = "SqlResultSetMapping.count"),
})
@Entity
@Data
@Table(name = "user")
public class CanUModel {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Column(name = "email")
    String email;

    @Column(name = "avatar")
    String avatar;

    @JsonIgnore
    @Column(name = "activated")
    Boolean activated;

    @JsonIgnore
    @Column(name = "delete_flag")
    Boolean deleteFlag;

    @JsonIgnore
    @Column(name = "token")
    String token;

    @JsonIgnore
    @Column(name = "password")
    String password;

    @Column(name = "phone")
    String phone;

    @Column(name = "nation")
    String nation;

    @Column(name = "city")
    String city;

    @Column(name = "address")
    String address;

    @JsonIgnore
    @Column(name = "provider_type")
    String providerType;

    @JsonIgnore
    @Column(name = "is_admin")
    Boolean isAdmin = false;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cani_id")
    private CanIModel canIModel;

    @JsonIgnore
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private AuthProviderModel socialData;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
    List<FileModel> files = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "canus", cascade = CascadeType.PERSIST)
    private Set<JobModel> jobs = new HashSet<>();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "creationUser")
    List<JobModel> createdJob;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "requestedUser")
    List<JobModel> pickedJob;

    @JsonIgnore
    public String getName() {
        if (firstName == null && lastName == null) {
            return null;
        }
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }


    public Long getCaniId(){
        if(canIModel == null){
            return null;
        }
        return canIModel.getId();
    }

    public void validatePrivilege(CanUModel ownerUser){
        if(!isAdmin && !this.getId().equals(ownerUser.getId())){
            throw new GlobalValidationException("do not have privilege to do");
        }
    }
}
