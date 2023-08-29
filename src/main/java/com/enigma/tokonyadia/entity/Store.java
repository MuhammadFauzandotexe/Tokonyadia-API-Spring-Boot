package com.enigma.tokonyadia.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "m_store")
public class Store extends BaseEntity {
    @Column(name = "no_siup", unique = true)
    private String noSiup;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "address", nullable = false)
    private String address;
    @Column(name = "domain", unique = true, nullable = false)
    private String domain;
    @Column(name = "mobile_phone", nullable = false, unique = true)
    private String mobilePhone;
    @OneToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "seller_id", unique = true)
    private Seller seller;
}
