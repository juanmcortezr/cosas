package pe.gob.sunat.microservices.curso.authenticator;

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
import pe.gob.sunat.microservices.curso.authenticator.api.InvalidCredentialExceptionMapper;
import pe.gob.sunat.microservices.curso.authenticator.api.SecurityResource;
import pe.gob.sunat.microservices.curso.authenticator.dao.UserDaoImpl;
import pe.gob.sunat.microservices.curso.authenticator.service.UserService;
import pe.gob.sunat.microservices.curso.monitoring.MonitoringUtil;


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

    final JdbiFactory factory = new JdbiFactory();
    final Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(), "postgresql");

    UserDaoImpl userDao = new UserDaoImpl(jdbi);

    UserService userService = new UserService(userDao);

    SecurityResource securityResource = new SecurityResource(userService);

    environment.jersey().register(new InvalidCredentialExceptionMapper());
    environment.jersey().register(securityResource);

    MonitoringUtil.register(configuration.getZipkinFactory(), environment);


  }

}
