package pe.gob.sunat.microservices.curso.customers;

import brave.Tracing;
import brave.http.HttpTracing;
import com.smoketurner.dropwizard.zipkin.ZipkinBundle;
import com.smoketurner.dropwizard.zipkin.ZipkinFactory;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;
import pe.gob.sunat.microservices.curso.customers.AppConfiguration;
import pe.gob.sunat.microservices.curso.customers.api.AddressResource;
import pe.gob.sunat.microservices.curso.customers.api.CustomerResource;
import pe.gob.sunat.microservices.curso.customers.dao.AddressDaoImpl;
import pe.gob.sunat.microservices.curso.customers.dao.CustomerDaoImpl;
import pe.gob.sunat.microservices.curso.customers.service.AddressService;
import pe.gob.sunat.microservices.curso.customers.service.CustomerService;
import pe.gob.sunat.microservices.curso.customers.service.OrderService;
import pe.gob.sunat.microservices.curso.monitoring.MonitoringUtil;
import pe.gob.sunat.microservices.curso.orders.client.OrderServiceClient;
import pe.gob.sunat.microservices.curso.orders.client.OrderServiceClientUtil;
import pe.gob.sunat.microservices.curso.customers.InvalidCustomerExceptionMapper;
import pe.gob.sunat.microservices.curso.customers.InvalidAddressExceptionMapper;
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

    bootstrap.addBundle(
      new ZipkinBundle<AppConfiguration>(getName()) {
        @Override
        public ZipkinFactory getZipkinFactory(AppConfiguration configuration) {
          return configuration.getZipkinFactory();
        }
      });

  }

  @Override
  public void run(AppConfiguration configuration, Environment environment) throws Exception {
    Optional<HttpTracing> register = MonitoringUtil.register(configuration.getZipkinFactory(), environment);
    Tracing tracing = register.get().tracing();

    SecurityUtil.register(configuration.getSecurityServiceBaseUrl(), environment, tracing);

    final JdbiFactory factory = new JdbiFactory();
    final Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(), "postgresql");

    CustomerDaoImpl customerDao = new CustomerDaoImpl(jdbi);
    AddressDaoImpl addressDao = new AddressDaoImpl(jdbi);
    
    OrderServiceClient orderServiceClient = OrderServiceClientUtil
    	      .register(
    	        configuration.getOrdersServiceBaseUrl(),
    	        tracing,
    	        configuration.getOrdersServiceUsername(),
    	        configuration.getOrdersServicePassword());

    OrderService orderService = new OrderService(orderServiceClient);
    CustomerService customerService = new CustomerService(orderService, customerDao, addressDao);
    AddressService addressService = new AddressService(orderService, addressDao, customerDao);

    CustomerResource customerResource = new CustomerResource(customerService, addressService);
    AddressResource addressResource = new AddressResource(addressService);

    environment.jersey().register(customerResource);
    environment.jersey().register(addressResource);

    environment.jersey().register(new InvalidAddressExceptionMapper());
    environment.jersey().register(new InvalidCustomerExceptionMapper());
    environment.jersey().register(new InvalidOrderExceptionMapper());  
  }
}
