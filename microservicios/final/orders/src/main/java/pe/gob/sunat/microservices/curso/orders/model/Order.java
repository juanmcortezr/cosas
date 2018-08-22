package pe.gob.sunat.microservices.curso.orders.model;

import java.math.BigDecimal;
import java.util.Date;

public class Order {
  private Long id;
  private Date createdAt;
  private BigDecimal amount;
  private Long customerId;
  private Long deliveryAddressId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Long getDeliveryAddressId() {
    return deliveryAddressId;
  }

  public void setDeliveryAddressId(Long deliveryAddressId) {
    this.deliveryAddressId = deliveryAddressId;
  }
}
