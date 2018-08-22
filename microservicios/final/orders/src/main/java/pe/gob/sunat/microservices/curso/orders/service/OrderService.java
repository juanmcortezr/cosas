package pe.gob.sunat.microservices.curso.orders.service;

import pe.gob.sunat.microservices.curso.orders.dao.OrderDaoImpl;
import pe.gob.sunat.microservices.curso.orders.model.Order;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class OrderService {

  private final CustomerService customerService;
  private final AddressService addressService;
  private final OrderDaoImpl dao;

  public OrderService(CustomerService customerService, AddressService addressService, OrderDaoImpl dao) {
    this.customerService = customerService;
    this.addressService = addressService;
    this.dao = dao;
  }

  public Order create(Order order) {
    Boolean validatedCustomer = customerService.validateCustomer(order.getCustomerId());
    //System.out.println("deliveryAddressId: " + order.getDeliveryAddressId());
    Boolean validatedAddress = addressService.validateAddress(order.getDeliveryAddressId());
    //System.out.println("deliveryAddressId: " + validatedAddress);

    if (!validatedCustomer) {
      throw new InvalidCustomerException("No se pudo validar al cliente. Se cancela la creación del pedido.", order.getCustomerId().toString());
    }
    
    if (!validatedAddress) {
        throw new InvalidAddressException("No se pudo validar la direccion. Se cancela la creación del pedido.", order.getDeliveryAddressId().toString());
    }
    
    order.setCreatedAt(new Date());
    return dao.create(order);
  }

  public List<Order> ordersByCustomer(Long id) {
	return dao.findByCustomer(id);
  }
  
  public Optional<Order> findById(Long id) {
	return dao.find(id);
  }
	
  public List<Order> ordersByAddress(Long id) {
    return dao.findByAddress(id);
  }
  
  public void delete(Long id) {
    dao.delete(id);
  }

  public Order update(Order order) {
    return dao.update(order);
  }  
}
