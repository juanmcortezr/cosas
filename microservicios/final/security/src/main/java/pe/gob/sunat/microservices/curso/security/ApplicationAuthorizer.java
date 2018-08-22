package pe.gob.sunat.microservices.curso.security;

import io.dropwizard.auth.Authorizer;
import pe.gob.sunat.microservices.curso.security.client.ApplicationUser;

public class ApplicationAuthorizer implements Authorizer<ApplicationUser> {
  @Override
  public boolean authorize(ApplicationUser principal, String role) {
    //No tenemos aplicada autorización
    return true;
  }
}
