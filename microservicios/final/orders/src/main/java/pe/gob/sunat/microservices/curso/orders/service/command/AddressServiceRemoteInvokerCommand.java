package pe.gob.sunat.microservices.curso.orders.service.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import pe.gob.sunat.microservices.curso.customers.client.AddressServiceClient;

public class AddressServiceRemoteInvokerCommand extends HystrixCommand<Boolean> {
  public static final int CONCURRENT_REQUESTS = 20;
  private final AddressServiceClient addressServiceClient;
  private final Long id;

  public AddressServiceRemoteInvokerCommand(AddressServiceClient addressServiceClient, Long id) {
    super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GroupComandoLatencia"))
      .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
        .withExecutionIsolationSemaphoreMaxConcurrentRequests(CONCURRENT_REQUESTS)));
    this.addressServiceClient = addressServiceClient;
    this.id = id;
  }


  @Override
  protected Boolean run() throws Exception {
	//System.out.println("previo a ejecucion del command de address");
	//System.out.println("AddressServiceRemoteInvokerCommand: " + addressServiceClient.getAddress(id).execute().toString());
    return addressServiceClient.getAddress(id).execute().isSuccessful();
  }

  @Override
  protected Boolean getFallback() {
    return false;
  }
}
