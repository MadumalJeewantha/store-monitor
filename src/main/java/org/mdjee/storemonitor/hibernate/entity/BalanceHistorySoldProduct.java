package org.mdjee.storemonitor.hibernate.entity;

import javax.persistence.*;

@Entity
@Table(name = "balance_history_sold_product")
public class BalanceHistorySoldProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "batch_code", nullable = false)
    private String batchCode;

    @Column(name = "sold_quantity", nullable = false)
    private double soledQuantity;

    @ManyToOne
    @JoinColumn(name = "balance_history_id", nullable = false)
    private BalanceHistory balanceHistory;

    public BalanceHistorySoldProduct() {
    }

    public BalanceHistorySoldProduct(String productId, String batchCode, double soledQuantity,
                                     BalanceHistory balanceHistory) {
        this.productId = productId;
        this.batchCode = batchCode;
        this.soledQuantity = soledQuantity;
        this.balanceHistory = balanceHistory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public double getSoledQuantity() {
        return soledQuantity;
    }

    public void setSoledQuantity(double soledQuantity) {
        this.soledQuantity = soledQuantity;
    }

    public BalanceHistory getBalanceHistory() {
        return balanceHistory;
    }

    public void setBalanceHistory(BalanceHistory balanceHistory) {
        this.balanceHistory = balanceHistory;
    }
}
