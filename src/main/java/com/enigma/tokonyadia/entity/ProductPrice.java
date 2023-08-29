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
@Table(name = "m_product_price")
public class ProductPrice extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
    @Column(columnDefinition = "bigint check (price > 1)")
    private Long price;
    @Column(columnDefinition = "int check (stock > 0)")
    private Integer stock;
    private Boolean isActive;
}
