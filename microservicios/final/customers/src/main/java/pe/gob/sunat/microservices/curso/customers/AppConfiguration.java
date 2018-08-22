package pe.gob.sunat.microservices.curso.customers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smoketurner.dropwizard.zipkin.ConsoleZipkinFactory;
import com.smoketurner.dropwizard.zipkin.ZipkinFactory;
import com.smoketurner.dropwizard.zipkin.client.ZipkinClientConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class AppConfiguration extends Configuration {
  @NotBlank
  @NotNull
  private String securityServiceBaseUrl;

  
  @NotNull
  private String ordersServiceBaseUrl;
  @NotBlank
  @NotNull
  private String ordersServiceUsername;
  @NotBlank
  @NotNull
  private String ordersServicePassword;
  
  @Valid
  @NotNull
  private DataSourceFactory database = new DataSourceFactory();

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

  @JsonProperty("database")
  public void setDataSourceFactory(DataSourceFactory factory) {
    this.database = factory;
  }

  @JsonProperty("database")
  public DataSourceFactory getDataSourceFactory() {
    return database;
  }

  @NotNull
  public String getSecurityServiceBaseUrl() {
    return securityServiceBaseUrl;
  }

  public void setSecurityServiceBaseUrl(@NotNull String securityServiceBaseUrl) {
    this.securityServiceBaseUrl = securityServiceBaseUrl;
  }


  public String getOrdersServiceBaseUrl() {
    return ordersServiceBaseUrl;
  }

  public void setOrdersServiceBaseUrl(String ordersServiceBaseUrl) {
    this.ordersServiceBaseUrl = ordersServiceBaseUrl;
  }

  @NotNull
  public String getOrdersServicePassword() {
    return ordersServicePassword;
  }

  public void setOrdersServicePassword(@NotNull String ordersServicePassword) {
    this.ordersServicePassword = ordersServicePassword;
  }

  @NotNull
  public String getOrdersServiceUsername() {
    return ordersServiceUsername;
  }

  public void setOrdersServiceUsername(@NotNull String ordersServiceUsername) {
    this.ordersServiceUsername = ordersServiceUsername;
  }
}

