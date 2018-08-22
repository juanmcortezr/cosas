package pe.gob.sunat.microservices.curso.orders.service;

import pe.gob.sunat.microservices.curso.customers.client.CustomerServiceClient;
//import pe.gob.sunat.microservices.curso.orders.service.command.AddressServiceRemoteInvokerCommand;
import pe.gob.sunat.microservices.curso.orders.service.command.CustomerServiceRemoteInvokerCommand;

public class CustomerService {
  private final CustomerServiceClient customerServiceClient;
  //private final CustomerServiceClient addressServiceClient;
  
  public CustomerService(CustomerServiceClient customerServiceClient) {
    this.customerServiceClient = customerServiceClient;
    //this.addressServiceClient = addressServiceClient;
  }

  public Boolean validateCustomer(Long id) {
    return
      new CustomerServiceRemoteInvokerCommand(customerServiceClient, id)
        .execute();
  }
  
  /*public Boolean validateAddress(Long id) {
    return
      new AddressServiceRemoteInvokerCommand(addressServiceClient, id)
        .execute();
  }*/
}
