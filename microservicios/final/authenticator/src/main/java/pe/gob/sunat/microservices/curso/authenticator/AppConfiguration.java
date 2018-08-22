package pe.gob.sunat.microservices.curso.authenticator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smoketurner.dropwizard.zipkin.ConsoleZipkinFactory;
import com.smoketurner.dropwizard.zipkin.ZipkinFactory;
import com.smoketurner.dropwizard.zipkin.client.ZipkinClientConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class AppConfiguration extends Configuration {

  @Valid
  @NotNull
  private DataSourceFactory database = new DataSourceFactory();

  @JsonProperty("database")
  public void setDataSourceFactory(DataSourceFactory factory) {
    this.database = factory;
  }

  @JsonProperty("database")
  public DataSourceFactory getDataSourceFactory() {
    return database;
  }

  @Valid
  @NotNull
  public final ZipkinFactory zipkin = new ConsoleZipkinFactory();

  @Valid
  @NotNull
  private final ZipkinClientConfiguration zipkinClient = new ZipkinClientConfiguration();

  @JsonProperty
  public ZipkinFactory getZipkinFactory() {
    return zipkin;
  }

  @JsonProperty
  public ZipkinClientConfiguration getZipkinClient() {
    return zipkinClient;
  }

}

