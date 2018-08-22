package pe.gob.sunat.microservices.curso.authenticator.api;

import pe.gob.sunat.microservices.curso.authenticator.service.InvalidCredentialsException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Provider
public class InvalidCredentialExceptionMapper implements ExceptionMapper<InvalidCredentialsException> {
  @Override
  public Response toResponse(InvalidCredentialsException exception) {
    Map data = new HashMap<>();
    data.put("mensaje", exception.getMessage());
    data.put("username", exception.getUsername());

    return Response.status(UNAUTHORIZED)
      .entity(data).build();
  }
}
