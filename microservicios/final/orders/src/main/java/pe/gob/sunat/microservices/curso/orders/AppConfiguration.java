package pe.gob.sunat.microservices.curso.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smoketurner.dropwizard.zipkin.ConsoleZipkinFactory;
import com.smoketurner.dropwizard.zipkin.ZipkinFactory;
import com.smoketurner.dropwizard.zipkin.client.ZipkinClientConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class AppConfiguration extends Configuration {
  @NotBlank
  @NotNull
  private String securityServiceBaseUrl;
  @NotBlank
  @NotNull
  private String customersServiceBaseUrl;
  @NotBlank
  @NotNull
  private String customersServiceUsername;
  @NotBlank
  @NotNull
  private String customersServicePassword;
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

  public String getCustomersServiceBaseUrl() {
    return customersServiceBaseUrl;
  }

  public void setCustomersServiceBaseUrl(String customersServiceBaseUrl) {
    this.customersServiceBaseUrl = customersServiceBaseUrl;
  }

  @NotNull
  public String getSecurityServiceBaseUrl() {
    return securityServiceBaseUrl;
  }

  public void setSecurityServiceBaseUrl(@NotNull String securityServiceBaseUrl) {
    this.securityServiceBaseUrl = securityServiceBaseUrl;
  }

  @NotNull
  public String getCustomersServicePassword() {
    return customersServicePassword;
  }

  public void setCustomersServicePassword(@NotNull String customersServicePassword) {
    this.customersServicePassword = customersServicePassword;
  }

  @NotNull
  public String getCustomersServiceUsername() {
    return customersServiceUsername;
  }

  public void setCustomersServiceUsername(@NotNull String customersServiceUsername) {
    this.customersServiceUsername = customersServiceUsername;
  }
}

