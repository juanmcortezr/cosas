package pe.gob.sunat.microservices.curso.security.client;

import java.security.Principal;

public class ApplicationUser implements Principal {
  private String username;
  private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String getName() {
    return username;
  }
}
