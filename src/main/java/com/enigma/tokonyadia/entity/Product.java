package com.enigma.tokonyadia.entity;

import com.enigma.tokonyadia.service.ProductImageService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "m_product")
public class Product extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductPrice> productPrices;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductImage> productImages;

    public List<ProductPrice> getProductPrices() {
        return Collections.unmodifiableList(productPrices);
    }

    public void addProductPrice(ProductPrice productPrice) {
        productPrices.add(productPrice);
    }

    public List<ProductImage> getProductImages() {
        return Collections.unmodifiableList(productImages);
    }

    public void addProductImage(ProductImage productImage) {
        productImages.add(productImage);
    }

    public void addAllProductImage(List<ProductImage> productImages) {
        this.productImages.addAll(productImages);
    }
}
