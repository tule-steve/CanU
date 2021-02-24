package com.canu.model;

import com.canu.dto.responses.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@SqlResultSetMapping(
        name = "MemberMapping",
        classes = {
                @ConstructorResult(
                        targetClass = Member.class,
                        columns = {
                                @ColumnResult(name = "userId", type = Long.class),
                                @ColumnResult(name = "name"),
                                @ColumnResult(name = "email"),
                                @ColumnResult(name = "createdAt", type = LocalDateTime.class),
                                @ColumnResult(name = "createdJob", type = Integer.class),
                                @ColumnResult(name = "finishedJob", type = Integer.class),
                                @ColumnResult(name = "processingJob", type = Integer.class)
                        }
                )
        }
)
@NamedNativeQuery(name = "CanUModel.getMembership", query =
        "select u.id as userId, u.name, u.email as email, u.created_at as createdAt, " +
        "   0 as createdJob, 0 as finishedJob, 0 as processingJob " +
        " from user u ",
        resultSetMapping = "MemberMapping")
@Entity
@Data
@Table(name = "user")
public class CanUModel {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "name")
    String name;

    @Column(name = "email")
    String email;

    @Column(name = "activated")
    Boolean activated;

    @Column(name = "delete_flag")
    Boolean deleteFlag;

    @Column(name = "token")
    String token;

    @Column(name = "password")
    String password;

    @Column(name = "phone")
    Integer phone;

    @Column(name = "nation")
    String nation;

    @Column(name = "city")
    String city;

    @Column(name = "address")
    String address;

    @Column(name = "provider_type")
    String providerType;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cani_id")
    private CanIModel canIModel;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private AuthProviderModel socialData;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
    List<FileModel> files = new ArrayList<>();
}
