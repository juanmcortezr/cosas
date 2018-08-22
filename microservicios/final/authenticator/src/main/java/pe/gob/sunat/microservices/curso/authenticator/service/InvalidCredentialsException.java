package pe.gob.sunat.microservices.curso.authenticator.service;

public class InvalidCredentialsException extends RuntimeException {
  public String getUsername() {
    return username;
  }

  private final String username;

  public InvalidCredentialsException(String message, String username) {
    super(message);
    this.username = username;
  }
}
