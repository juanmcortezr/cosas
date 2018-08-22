package pe.gob.sunat.microservices.curso.customers.service;

import pe.gob.sunat.microservices.curso.customers.dao.AddressDaoImpl;
import pe.gob.sunat.microservices.curso.customers.dao.CustomerDaoImpl;
import pe.gob.sunat.microservices.curso.customers.model.Address;
import pe.gob.sunat.microservices.curso.customers.model.Customer;
import pe.gob.sunat.microservices.curso.customers.service.InvalidAddressException;

import java.util.List;
import java.util.Optional;

public class AddressService {
	private final OrderService orderService;
	private final AddressDaoImpl dao;
	private final CustomerDaoImpl customerDao;
	
	public AddressService(OrderService orderService, AddressDaoImpl dao, CustomerDaoImpl customerDao) {
		this.orderService = orderService;
		this.dao = dao;
		this.customerDao = customerDao;
	}
	
	public Address create(Long customerId, Address address) {

		boolean validatedCustomer = validateCustomer(address.getCustomerId());
	    if (!validatedCustomer) {
	        throw new InvalidAddressException("No se pudo validar al cliente, no se creara la direccion.", address.getCustomerId().toString());
	    }

		return dao.create(customerId, address);
	}

	public Address create(Address address) {
		return create(address.getCustomerId(), address);
	}
	
	public List<Address> addressesByCustomer(Long id) {
		return dao.findByCustomer(id);
	}
	
	public void delete(Long id) {

	    boolean validatedOrders = orderService.validateOrderByAddress(id);

		if (validatedOrders) {
	      throw new InvalidAddressException("No se pudo puede borrar una direccion que tenga pedidos asociados.", id.toString());
	    }
	    
		dao.delete(id);
	}

	public Address update(Address address) {
		return update(address);
	}
	
	public Optional<Address> findById(Long id) {
		
		//boolean validatedOrders = orderService.validateOrderByAddress(id);
		//System.out.println("validateOrders en consulta por address: " + id + " " + validatedOrders);
		
	    return dao.find(id);
	}
	
	private boolean validateCustomer(Long id) {
		return customerDao.find(id).isPresent();
	}
}
