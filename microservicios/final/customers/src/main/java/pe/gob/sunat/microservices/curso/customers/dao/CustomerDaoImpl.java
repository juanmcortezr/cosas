package pe.gob.sunat.microservices.curso.customers.dao;

import org.jdbi.v3.core.Jdbi;

import pe.gob.sunat.microservices.curso.customers.model.Address;
import pe.gob.sunat.microservices.curso.customers.model.Customer;

import java.util.Optional;

public class CustomerDaoImpl {
  private final Jdbi jdbi;

  public CustomerDaoImpl(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  public Customer create(Customer customer) {
    jdbi.inTransaction(handle -> {
      String insert = "INSERT " +
        "INTO customers (first_name, last_name, business_name, email, tax_id) " +
        "VALUES (:first_name, :last_name, :business_name, :email, :tax_id);";
      return handle.createUpdate(insert)
        .bind("first_name", customer.getFirstName())
        .bind("last_name", customer.getLastName())
        .bind("business_name", customer.getBusinessName())
        .bind("email", customer.getEmail())
        .bind("tax_id", customer.getTaxId())
        .execute();
    });

    return customer;
  }

  public Customer update(Customer customer) {
	    jdbi.inTransaction(handle -> {
	    String update = "update customers set " +
	        "first_name=:first_name, last_name=:last_name, business_name=:business_name, email:=email, tax_id:=tax_id " +
	        "where id=:id ";
	    return handle.createUpdate(update)
	  	    .bind("id",  customer.getId())
	        .bind("first_name", customer.getFirstName())
	        .bind("last_name", customer.getLastName())
	        .bind("business_name", customer.getBusinessName())
	        .bind("email", customer.getEmail())
	        .bind("tax_id", customer.getTaxId())
	        .execute();
	    });
	    
	    return customer;
  }
  
  
  public Optional<Customer> find(Long id) {
    return jdbi.withHandle(handle ->
      handle.createQuery("SELECT * from customers where id=:id")
        .bind("id", id)
        .map(new CustomerMapper())
        .findFirst());
  }

  public void delete(Long id) {
    jdbi.inTransaction(handle -> {
      String delete = "DELETE FROM customers where id=:id";
      return handle.createUpdate(delete)
        .bind("id", id)
        .execute();
    });
  }
}
