package pe.gob.sunat.microservices.curso.orders.dao;

import org.jdbi.v3.core.Jdbi;

import pe.gob.sunat.microservices.curso.orders.model.Order;

import java.util.List;
import java.util.Optional;

public class OrderDaoImpl {
  private final Jdbi jdbi;

  public OrderDaoImpl(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  public Order create(Order order) {
    jdbi.inTransaction(handle -> {
      String insert = "INSERT " +
        "INTO orders (created_at, amount, customer_id, delivery_address_id) " +
        "VALUES (:created_at, :amount, :customer_id, :delivery_address_id);";
      return handle.createUpdate(insert)
        .bind("created_at", order.getCreatedAt())
        .bind("amount", order.getAmount())
        .bind("customer_id", order.getCustomerId())
        .bind("delivery_address_id", order.getDeliveryAddressId())
        .execute();
    });

    return order;
  }

  public Order update(Order order) {
    jdbi.inTransaction(handle -> {
    	String update = "update orders set " +
        "amount=:amount " +
        "where id=:id ";
    	return handle.createUpdate(update)
  	    .bind("id", order.getId())
        .bind("amount", order.getAmount())
        .execute();
    });

    return order;
  }
  
  public Optional<Order> find(Long id) {
    return jdbi.withHandle(handle ->
      handle.createQuery("SELECT * from orders where id=:id")
        .bind("id", id)
        .map(new OrderMapper())
        .findFirst());
  }

  public void delete(Long id) {
    jdbi.inTransaction(handle -> {
      String delete = "DELETE FROM orders where id=:id";
      return handle.createUpdate(delete)
        .bind("id", id)
        .execute();
    });
  }

  public List<Order> findByCustomer(Long id) {
    return jdbi.withHandle(handle ->
      handle.createQuery("SELECT * from orders where customer_id=:id")
        .bind("id", id)
        .map(new OrderMapper())
        .list());
  }

  public List<Order> findByAddress(Long id) {
	    return jdbi.withHandle(handle ->
	      handle.createQuery("SELECT * from orders where delivery_address_id=:id")
	        .bind("id", id)
	        .map(new OrderMapper())
	        .list());	
  }
}
