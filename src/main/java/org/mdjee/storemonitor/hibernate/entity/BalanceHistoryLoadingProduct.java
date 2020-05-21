package org.mdjee.storemonitor.hibernate.entity;

import javax.persistence.*;

@Entity
@Table(name = "balance_history_loading_product")
public class BalanceHistoryLoadingProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "batch_code", nullable = false)
    private String batchCode;

    @Column(name = "total_quantity", nullable = false)
    private double totalQuantity;

    @ManyToOne
    @JoinColumn(name = "balance_history_id", nullable = false)
    private BalanceHistory balanceHistory;

    public BalanceHistoryLoadingProduct() {
    }

    public BalanceHistoryLoadingProduct(String productId, String batchCode, double totalQuantity,
                                        BalanceHistory balanceHistory) {
        this.productId = productId;
        this.batchCode = batchCode;
        this.totalQuantity = totalQuantity;
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

    public double getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BalanceHistory getBalanceHistory() {
        return balanceHistory;
    }

    public void setBalanceHistory(BalanceHistory balanceHistory) {
        this.balanceHistory = balanceHistory;
    }
}
