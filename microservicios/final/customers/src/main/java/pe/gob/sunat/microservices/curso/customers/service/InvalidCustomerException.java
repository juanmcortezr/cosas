package pe.gob.sunat.microservices.curso.customers.service;

public class InvalidCustomerException extends RuntimeException {
  private final String customerId;

  public InvalidCustomerException(String message, String customerId) {
    super(message);
    this.customerId = customerId;
  }

  public String getCustomerId() {
    return customerId;
  }
}
