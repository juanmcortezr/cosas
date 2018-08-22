package pe.gob.sunat.microservices.curso.customers.service;

import pe.gob.sunat.microservices.curso.customers.dao.AddressDaoImpl;
import pe.gob.sunat.microservices.curso.customers.dao.CustomerDaoImpl;
import pe.gob.sunat.microservices.curso.customers.model.Customer;

import java.util.Optional;

public class CustomerService {
  private final OrderService orderService;
  private final CustomerDaoImpl dao;
  private final AddressDaoImpl addressDao;

  public CustomerService(OrderService orderService, CustomerDaoImpl dao, AddressDaoImpl addressDao) {
	this.orderService = orderService;
    this.dao = dao;
    this.addressDao = addressDao;
  }

  public Customer create(Customer customer) {
    return dao.create(customer);
  }

  public Optional<Customer> findById(Long id, Boolean includeAddresses) {

		//boolean validatedOrders = orderService.validateOrderByCustomer(id);
		//System.out.println("validateOrders en consulta por customer: " + id + " " + validatedOrders);
	  
	  return dao.find(id).map(customer -> {
      if (includeAddresses) {
        customer.setAddresses(addressDao.findByCustomer(id));
      }
      return customer;
    });
  }

  public void delete(Long id) {
    
	boolean validatedAddressess = validateAddresses(id);
    boolean validatedOrders = orderService.validateOrderByCustomer(id);
	
	if (validatedAddressess) {
      throw new InvalidCustomerException("No se pudo puede borrar un cliente que tenga direcciones asociadas.", id.toString());
    }

	if (validatedOrders) {
      throw new InvalidCustomerException("No se pudo puede borrar un cliente que tenga pedidos asociados.", id.toString());
    }
	
	dao.delete(id);
  }
  
  public Customer update(Customer customer) {
	return dao.update(customer);
  }
  
  private boolean validateAddresses(Long id) {
	return (addressDao.findByCustomer(id).size() > 0 ? true : false);
  }
}
