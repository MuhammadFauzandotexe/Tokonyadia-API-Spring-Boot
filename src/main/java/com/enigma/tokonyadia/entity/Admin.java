package com.enigma.tokonyadia.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "m_admin")
public class Admin extends BaseEntity {
    private String fullName;
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private UserCredential userCredential;
}
