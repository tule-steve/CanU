package com.canu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Builder
@Table(name = "user_deleted_message")
@AllArgsConstructor
public class CanUDeletedMessage {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "user_id")
    Long userId;

    @Column(name = "participant_id")
    Long participantId;

    @Column(name = "deleted_mess_id")
    Long lastedMessage;








}
