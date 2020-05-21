package org.mdjee.storemonitor.hibernate.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "profit")
public class Profit {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "batch_code", nullable = false)
    private String batchCode;

    @Column(name = "buying_price", nullable = false)
    private double buyingPrice;

    @Column(name = "sold_price", nullable = false)
    private double soldPrice;

    @Column(name = "profit_amount", nullable = false)
    private double profitAmount;

    @Column(name = "quantity", nullable = false)
    private double quantity;

    @ManyToOne
    @JoinColumn(name = "balance_history_id", nullable = false)
    private BalanceHistory balanceHistory;


    public Profit() {

    }

    public Profit(String productId, String batchCode, double buyingPrice, double soldPrice, double profitAmount,
                  BalanceHistory balanceHistory, Double quantity) {
        this.productId = productId;
        this.batchCode = batchCode;
        this.buyingPrice = buyingPrice;
        this.soldPrice = soldPrice;
        this.profitAmount = profitAmount;
        this.balanceHistory = balanceHistory;
        this.quantity = quantity;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
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

    public double getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(double buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public double getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(double soldPrice) {
        this.soldPrice = soldPrice;
    }

    public double getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(double profitAmount) {
        this.profitAmount = profitAmount;
    }

    public BalanceHistory getBalanceHistory() {
        return balanceHistory;
    }

    public void setBalanceHistory(BalanceHistory balanceHistory) {
        this.balanceHistory = balanceHistory;
    }
}
