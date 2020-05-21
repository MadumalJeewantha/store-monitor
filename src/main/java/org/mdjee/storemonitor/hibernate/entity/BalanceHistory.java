package org.mdjee.storemonitor.hibernate.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "balance_history")
public class BalanceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "vehicle_id", nullable = false)
    private String vehicleId;

    @Column(name = "stock_loaded_date", nullable = false)
    private LocalDateTime stockLoadedDate;

    @Column(name = "stock_balanced_date")
    private LocalDateTime stockBalancedDate;

    @OneToMany(mappedBy = "balanceHistory", cascade = CascadeType.ALL)
    private List<BalanceHistorySoldProduct> balanceHistorySoldProducts = new ArrayList<>();

    @OneToMany(mappedBy = "balanceHistory", cascade = CascadeType.ALL)
    private List<BalanceHistoryNotSoldProduct> balanceHistoryNotSoldProducts = new ArrayList<>();

    @OneToMany(mappedBy = "balanceHistory", cascade = CascadeType.ALL)
    private List<BalanceHistoryReturn> balanceHistoryReturns = new ArrayList<>();

    @OneToMany(mappedBy = "balanceHistory", cascade = CascadeType.ALL)
    private List<BalanceHistoryLoadingProduct> balanceHistoryLoadingProducts = new ArrayList<>();

    @OneToMany(mappedBy = "balanceHistory", cascade = CascadeType.ALL)
    private List<Profit> profits = new ArrayList<>();

    public BalanceHistory() {
    }

    public BalanceHistory(String vehicleId, LocalDateTime stockLoadedDate, LocalDateTime stockBalancedDate,
                          List<BalanceHistorySoldProduct> balanceHistorySoldProducts,
                          List<BalanceHistoryNotSoldProduct> balanceHistoryNotSoldProducts,
                          List<BalanceHistoryReturn> balanceHistoryReturns,
                          List<BalanceHistoryLoadingProduct> balanceHistoryLoadingProducts,
                          List<Profit> profits) {
        this.vehicleId = vehicleId;
        this.stockLoadedDate = stockLoadedDate;
        this.stockBalancedDate = stockBalancedDate;
        this.balanceHistorySoldProducts = balanceHistorySoldProducts;
        this.balanceHistoryNotSoldProducts = balanceHistoryNotSoldProducts;
        this.balanceHistoryReturns = balanceHistoryReturns;
        this.balanceHistoryLoadingProducts = balanceHistoryLoadingProducts;
        this.profits = profits;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDateTime getStockLoadedDate() {
        return stockLoadedDate;
    }

    public void setStockLoadedDate(LocalDateTime stockLoadedDate) {
        this.stockLoadedDate = stockLoadedDate;
    }

    public LocalDateTime getStockBalancedDate() {
        return stockBalancedDate;
    }

    public void setStockBalancedDate(LocalDateTime stockBalancedDate) {
        this.stockBalancedDate = stockBalancedDate;
    }

    public List<BalanceHistorySoldProduct> getBalanceHistorySoldProducts() {
        return balanceHistorySoldProducts;
    }

    public void setBalanceHistorySoldProducts(List<BalanceHistorySoldProduct> balanceHistorySoldProducts) {
        this.balanceHistorySoldProducts = balanceHistorySoldProducts;
    }

    public List<BalanceHistoryNotSoldProduct> getBalanceHistoryNotSoldProducts() {
        return balanceHistoryNotSoldProducts;
    }

    public void setBalanceHistoryNotSoldProducts(List<BalanceHistoryNotSoldProduct> balanceHistoryNotSoldProducts) {
        this.balanceHistoryNotSoldProducts = balanceHistoryNotSoldProducts;
    }

    public List<BalanceHistoryReturn> getBalanceHistoryReturns() {
        return balanceHistoryReturns;
    }

    public void setBalanceHistoryReturns(List<BalanceHistoryReturn> balanceHistoryReturns) {
        this.balanceHistoryReturns = balanceHistoryReturns;
    }

    public List<BalanceHistoryLoadingProduct> getBalanceHistoryLoadingProducts() {
        return balanceHistoryLoadingProducts;
    }

    public void setBalanceHistoryLoadingProducts(List<BalanceHistoryLoadingProduct> balanceHistoryLoadingProducts) {
        this.balanceHistoryLoadingProducts = balanceHistoryLoadingProducts;
    }

    public List<Profit> getProfits() {
        return profits;
    }

    public void setProfits(List<Profit> profits) {
        this.profits = profits;
    }
}
