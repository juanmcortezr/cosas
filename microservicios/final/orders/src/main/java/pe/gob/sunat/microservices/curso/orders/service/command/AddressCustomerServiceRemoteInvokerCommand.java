package pe.gob.sunat.microservices.curso.orders.service.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import pe.gob.sunat.microservices.curso.customers.client.AddressServiceClient;

public class AddressCustomerServiceRemoteInvokerCommand extends HystrixCommand<Boolean> {
  public static final int CONCURRENT_REQUESTS = 20;
  private final AddressServiceClient addressServiceClient;
  private final Long customerId;
  private final Long id;

  public AddressCustomerServiceRemoteInvokerCommand(AddressServiceClient addressServiceClient, Long customerId, Long id) {
    super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GroupComandoLatencia"))
      .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
        .withExecutionIsolationSemaphoreMaxConcurrentRequests(CONCURRENT_REQUESTS)));
    this.addressServiceClient = addressServiceClient;
    this.customerId = customerId;
    this.id = id;
  }


  @Override
  protected Boolean run() throws Exception {
	//System.out.println("previo a ejecucion del command de address");
	//System.out.println("AddressServiceRemoteInvokerCommand: " + addressServiceClient.getAddress(id).execute().toString());
    return addressServiceClient.findByCustomer(customerId, id).execute().isSuccessful();
  }

  @Override
  protected Boolean getFallback() {
    return false;
  }
}
