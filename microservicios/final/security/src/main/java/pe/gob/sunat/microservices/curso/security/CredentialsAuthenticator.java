package pe.gob.sunat.microservices.curso.security;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import pe.gob.sunat.microservices.curso.security.client.ApplicationUser;
import pe.gob.sunat.microservices.curso.security.client.SecurityServiceClient;
import pe.gob.sunat.microservices.curso.security.client.User;

import java.io.IOException;
import java.util.Optional;

public class CredentialsAuthenticator implements Authenticator<BasicCredentials, ApplicationUser> {
  private final SecurityServiceClient securityServiceClient;

  public CredentialsAuthenticator(SecurityServiceClient securityServiceClient) {
    this.securityServiceClient = securityServiceClient;
  }

  @Override
  public Optional<ApplicationUser> authenticate(BasicCredentials credentials) throws AuthenticationException {
    User user = new User();
    user.setUsername(credentials.getUsername());
    user.setPassword(credentials.getPassword());
    try {
      if (securityServiceClient.login(user).execute().isSuccessful()) {
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setUsername(user.getUsername());
        applicationUser.setPassword(user.getPassword());
        return Optional.of(applicationUser);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }
}
