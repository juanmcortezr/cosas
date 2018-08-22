package pe.gob.sunat.microservices.curso.customers.service;

import pe.gob.sunat.microservices.curso.customers.service.command.OrderByAddressServiceRemoteInvokerCommand;
import pe.gob.sunat.microservices.curso.customers.service.command.OrderServiceRemoteInvokerCommand;
import pe.gob.sunat.microservices.curso.orders.client.OrderServiceClient;

public class OrderService {
  //private final CustomerServiceClient customerServiceClient;
  private final OrderServiceClient orderServiceClient;
  
  public OrderService(OrderServiceClient orderServiceClient) {
    //this.customerServiceClient = customerServiceClient;
    this.orderServiceClient = orderServiceClient;
  }

  public Boolean validateOrderByCustomer(Long id) {
	return
      new OrderServiceRemoteInvokerCommand(orderServiceClient, id)
        .execute();
  }
  
  public Boolean validateOrderByAddress(Long id) {
	return
      new OrderByAddressServiceRemoteInvokerCommand(orderServiceClient, id)
        .execute();
	/*try {
		return (orderServiceClient.ordersByAddressList(id).execute().body().size() > 0 ? true : false);
	}
	catch (Exception e) {
		e.printStackTrace();
		return false;
	}*/
  }
}
