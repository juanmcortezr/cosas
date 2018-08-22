package pe.gob.sunat.microservices.curso.authenticator.api;

import pe.gob.sunat.microservices.curso.authenticator.model.User;
import pe.gob.sunat.microservices.curso.authenticator.service.UserService;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/v1/users")
@Produces(MediaType.APPLICATION_JSON)
public class SecurityResource {
  private final UserService userService;

  public SecurityResource(UserService userService) {
    this.userService = userService;
  }

  @POST
  @Path("/_login")
  public void login(@Valid User user) {
    userService.login(user.getUsername(), user.getPassword());
  }

  @POST
  public User create(@Valid User user) {
    return userService.create(user);
  }
}
