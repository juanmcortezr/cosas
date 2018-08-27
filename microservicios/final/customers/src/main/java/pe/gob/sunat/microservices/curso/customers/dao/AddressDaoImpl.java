package pe.gob.sunat.microservices.curso.customers.dao;

import org.jdbi.v3.core.Jdbi;
import pe.gob.sunat.microservices.curso.customers.model.Address;
import pe.gob.sunat.microservices.curso.customers.model.Customer;

import java.util.List;
import java.util.Optional;

public class AddressDaoImpl {
  private final Jdbi jdbi;

  public AddressDaoImpl(Jdbi jdbi) {
    this.jdbi = jdbi;
  }
  
  public Address create(Long customerId, Address address) {
    jdbi.inTransaction(handle -> {
      String insert = "INSERT INTO addresses " +
        "(street, name, country, owner_id) " +
        "VALUES (:street, :name, :country, :owner_id);";
      return handle.createUpdate(insert)
        .bind("street", address.getStreet())
        .bind("name", address.getName())
        .bind("country", address.getCountry())
        .bind("owner_id", customerId)
        .execute();
    });

    return address;
  }

  public Address update(Address address) {
    jdbi.inTransaction(handle -> {
    String update = "update addresses set " +
        "street=:street, name=:name, country=:country " +
        "where id=:id ";
    return handle.createUpdate(update)
  	    .bind("id", address.getId())
    	.bind("street", address.getStreet())
        .bind("name", address.getName())
        .bind("country", address.getCountry())
        .execute();
    });

    return address;
  }
  
  public void delete(Long id) {
    jdbi.inTransaction(handle -> {
      String delete = "DELETE FROM addresses where id=:id";
      return handle.createUpdate(delete)
        .bind("id", id)
        .execute();
    });
  }

  public List<Address> findByCustomer(Long id) {
    return jdbi.withHandle(handle ->
      handle.createQuery("SELECT * from addresses where owner_id=:id")
        .bind("id", id)
        .map(new AddressMapper())
        .list());
  }

  public Optional<Address> findByCustomerAndId(Long customerId, Long id) {
	    return jdbi.withHandle(handle ->
	      handle.createQuery("SELECT * from addresses where owner_id=:customerId and id=:id")
	      .bind("customerId", customerId)
	      .bind("id", id)
	        .map(new AddressMapper())
	        .findFirst());
  }
  
  public Optional<Address> find(Long id) {
    return jdbi.withHandle(handle ->
      handle.createQuery("SELECT * from addresses where id=:id")
        .bind("id", id)
        .map(new AddressMapper())
        .findFirst());
  }
}
