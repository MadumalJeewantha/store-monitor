package org.mdjee.storemonitor.hibernate.entity;

import com.google.common.base.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ProductId implements Serializable {

    @Column(name = "product_id")
    private String productId;

    @Column(name = "batch_code")
    private String batchCode;

    public ProductId() {
    }

    public ProductId(String productId, String batchCode) {
        this.productId = productId;
        this.batchCode = batchCode;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductId productId1 = (ProductId) o;
        return Objects.equal(getProductId(), productId1.getProductId()) &&
                Objects.equal(getBatchCode(), productId1.getBatchCode());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getProductId(), getBatchCode());
    }
}
