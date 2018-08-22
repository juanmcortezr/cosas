package pe.gob.sunat.microservices.curso.orders.service;

public class InvalidOrderException extends RuntimeException {
  private final String orderId;

  public InvalidOrderException(String message, String orderId) {
    super(message);
    this.orderId = orderId;
  }

  public String getOrderId() {
    return orderId;
  }
}
