package org.mdjee.storemonitor.hibernate.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "product")
public class Product implements Serializable {

    @EmbeddedId
    private ProductId productId;

    @Column(name = "manufactured_date")
    private LocalDate manufacturedDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "available_quantity", nullable = false)
    private double availableQuantity;

    @Column(name = "reorder_level")
    private double reorderLevel;

    @Column(name = "buying_price", nullable = false)
    private double buyingPrice;

    @Column(name = "selling_price")
    private double sellingPrice;

    @Column(name = "remark")
    private String remark;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Store store;

    public Product() {
    }

    public Product(ProductId productId, LocalDate manufacturedDate, LocalDate expiryDate, double availableQuantity,
                   double reorderLevel, double buyingPrice, double sellingPrice, String remark, Store store) {
        this.productId = productId;
        this.manufacturedDate = manufacturedDate;
        this.expiryDate = expiryDate;
        this.availableQuantity = availableQuantity;
        this.reorderLevel = reorderLevel;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.remark = remark;
        this.store = store;
    }

    public ProductId getProductId() {
        return productId;
    }

    public void setProductId(ProductId productId) {
        this.productId = productId;
    }

    public LocalDate getManufacturedDate() {
        return manufacturedDate;
    }

    public void setManufacturedDate(LocalDate manufacturedDate) {
        this.manufacturedDate = manufacturedDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public double getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(double availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public double getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(double reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public double getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(double buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    // Check for expiry date
    // For given date
    public boolean isExpired(LocalDate date) {
        boolean status = false;
        // If there is no EXP date
        if(getExpiryDate() == null){
            return false;
        }
        // If check for today
        // date = LocalDate.now();
        int compare = getExpiryDate().compareTo(date);
        // check value
        if (compare <= 0) {
            status = true;
        }
        return status;
    }

    // Check for selling price
    public boolean isSellingPriceEmpty() {
        boolean status = false;

        if (getSellingPrice() == 0) {
            status = true;
        }
        return status;
    }

    // Check for re-order level
    public boolean isBelowReOrderLevel(){
        boolean status = false;

        if(getReorderLevel() >= getAvailableQuantity()){
            status = true;
        }
        return  status;
    }

}
