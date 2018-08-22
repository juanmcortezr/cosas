package pe.gob.sunat.microservices.curso.orders.service.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import pe.gob.sunat.microservices.curso.customers.client.CustomerServiceClient;

public class CustomerServiceRemoteInvokerCommand extends HystrixCommand<Boolean> {
  public static final int CONCURRENT_REQUESTS = 20;
  private final CustomerServiceClient customerServiceClient;
  private final Long id;

  public CustomerServiceRemoteInvokerCommand(CustomerServiceClient customerServiceClient, Long id) {
    super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GroupComandoLatencia"))
      .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
        .withExecutionIsolationSemaphoreMaxConcurrentRequests(CONCURRENT_REQUESTS)));
    this.customerServiceClient = customerServiceClient;
    this.id = id;
  }


  @Override
  protected Boolean run() throws Exception {
    return customerServiceClient.get(id).execute().isSuccessful();
  }

  @Override
  protected Boolean getFallback() {
    return false;
  }
}
