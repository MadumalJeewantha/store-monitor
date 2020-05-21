package org.mdjee.storemonitor.hibernate.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "store")
public class Store {

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "description")
    private String description;

    @Column(name = "units")
    private String units;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    public Store() {
    }

    public Store(String productId, String description, String units, List<Product> products) {
        this.productId = productId;
        this.description = description;
        this.units = units;
        this.products = products;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
