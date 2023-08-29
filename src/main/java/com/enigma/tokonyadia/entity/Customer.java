package com.enigma.tokonyadia.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_customer")
public class Customer extends BaseEntity {
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "mobile_phone", unique = true)
    private String mobilePhone;
    @OneToOne
    @JoinColumn(name = "user_credential_id")
    private UserCredential userCredential;
}
