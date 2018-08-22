package pe.gob.sunat.microservices.curso.orders;

import brave.Tracing;
import brave.http.HttpTracing;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;

import pe.gob.sunat.microservices.curso.customers.client.AddressServiceClient;
import pe.gob.sunat.microservices.curso.customers.client.AddressServiceClientUtil;
import pe.gob.sunat.microservices.curso.customers.client.CustomerServiceClient;
import pe.gob.sunat.microservices.curso.customers.client.CustomerServiceClientUtil;
import pe.gob.sunat.microservices.curso.monitoring.MonitoringUtil;
import pe.gob.sunat.microservices.curso.orders.api.OrderResource;
import pe.gob.sunat.microservices.curso.orders.dao.OrderDaoImpl;
import pe.gob.sunat.microservices.curso.orders.service.AddressService;
import pe.gob.sunat.microservices.curso.orders.service.CustomerService;
import pe.gob.sunat.microservices.curso.orders.service.OrderService;
import pe.gob.sunat.microservices.curso.security.SecurityUtil;

import java.util.Optional;


public class App extends Application<AppConfiguration> {
  public static void main(String[] args) throws Exception {
    new App().run(args);
  }

  @Override
  public void initialize(Bootstrap<AppConfiguration> bootstrap) {
    bootstrap.addBundle(new MigrationsBundle<AppConfiguration>() {
      @Override
      public DataSourceFactory getDataSourceFactory(AppConfiguration configuration) {
        return configuration.getDataSourceFactory();
      }
    });

    bootstrap.setConfigurationSourceProvider(
      new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
        new EnvironmentVariableSubstitutor()
      )
    );

  }

  @Override
  public void run(AppConfiguration configuration, Environment environment) throws Exception {
    Optional<HttpTracing> register = MonitoringUtil.register(configuration.getZipkinFactory(), environment);
    Tracing tracing = register.get().tracing();

    final JdbiFactory factory = new JdbiFactory();
    final Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(), "postgresql");

    SecurityUtil.register(configuration.getSecurityServiceBaseUrl(), environment, tracing);

    OrderDaoImpl orderDao = new OrderDaoImpl(jdbi);

    CustomerServiceClient customerServiceClient = CustomerServiceClientUtil
      .register(
        configuration.getCustomersServiceBaseUrl(),
        tracing,
        configuration.getCustomersServiceUsername(),
        configuration.getCustomersServicePassword());

    AddressServiceClient addressServiceClient = AddressServiceClientUtil
    	      .register(
    	        configuration.getCustomersServiceBaseUrl(),
    	        tracing,
    	        configuration.getCustomersServiceUsername(),
    	        configuration.getCustomersServicePassword());
    
    CustomerService customerService = new CustomerService(customerServiceClient);
    AddressService addressService = new AddressService(addressServiceClient);
    OrderService orderService = new OrderService(customerService, addressService, orderDao);

    OrderResource orderResource = new OrderResource(orderService);

    environment.jersey().register(orderResource);
    environment.jersey().register(new InvalidOrderExceptionMapper());  
    environment.jersey().register(new InvalidAddressExceptionMapper());
    environment.jersey().register(new InvalidCustomerExceptionMapper());
  }

}
