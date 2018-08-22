package pe.gob.sunat.microservices.curso.customers.model;

import org.hibernate.validator.constraints.NotBlank;

import java.util.Collections;
import java.util.List;

public class Customer {
  private Long id;
  @NotBlank
  private String firstName;
  @NotBlank
  private String lastName;
  private String businessName;
  @NotBlank
  private String email;
  @NotBlank
  private String taxId;
  private List<Address> addresses = Collections.emptyList();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getBusinessName() {
    return businessName;
  }

  public void setBusinessName(String businessName) {
    this.businessName = businessName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getTaxId() {
    return taxId;
  }

  public void setTaxId(String taxId) {
    this.taxId = taxId;
  }

  public List<Address> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<Address> addresses) {
    this.addresses = addresses;
  }
}
