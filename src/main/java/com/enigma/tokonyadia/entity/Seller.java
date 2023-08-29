package com.enigma.tokonyadia.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "m_seller")
public class Seller extends BaseEntity {
    @OneToOne(mappedBy = "seller", fetch = FetchType.LAZY)
    private Store store;
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private UserCredential userCredential;
}
