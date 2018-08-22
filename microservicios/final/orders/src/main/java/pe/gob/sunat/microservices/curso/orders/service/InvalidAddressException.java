package pe.gob.sunat.microservices.curso.orders.service;

public class InvalidAddressException extends RuntimeException {
  private final String addressId;

  public InvalidAddressException(String message, String addressId) {
    super(message);
    this.addressId = addressId;
  }

  public String getAddressId() {
    return addressId;
  }
}
