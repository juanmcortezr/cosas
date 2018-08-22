package pe.gob.sunat.microservices.curso.orders.service;

import pe.gob.sunat.microservices.curso.customers.client.AddressServiceClient;
import pe.gob.sunat.microservices.curso.orders.service.command.AddressServiceRemoteInvokerCommand;
//import pe.gob.sunat.microservices.curso.orders.service.command.CustomerServiceRemoteInvokerCommand;

public class AddressService {
  //private final CustomerServiceClient customerServiceClient;
  private final AddressServiceClient addressServiceClient;
  
  public AddressService(AddressServiceClient addressServiceClient) {
    //this.customerServiceClient = customerServiceClient;
    this.addressServiceClient = addressServiceClient;
  }

  /*public Boolean validateCustomer(Long id) {
    return
      new CustomerServiceRemoteInvokerCommand(customerServiceClient, id)
        .execute();
  }*/
  
  public Boolean validateAddress(Long id) {
	/*try {
		return addressServiceClient.getAddress(id).execute().isSuccessful();
	}
	catch (Exception e) {
		e.printStackTrace();
		return false;
	}*/
	return
      new AddressServiceRemoteInvokerCommand(addressServiceClient, id)
        .execute();
  }
}
