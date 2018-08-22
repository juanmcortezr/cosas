package pe.gob.sunat.microservices.curso.customers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import pe.gob.sunat.microservices.curso.customers.service.InvalidAddressException;

import java.util.HashMap;
import java.util.Map;

@Provider
public class InvalidAddressExceptionMapper implements ExceptionMapper<InvalidAddressException> {
  @Override
  public Response toResponse(InvalidAddressException exception) {
    Map data = new HashMap();
    data.put("message", exception.getMessage());
    data.put("id", exception.getAddressId());
    return Response.status(422).entity(data).type(MediaType.APPLICATION_JSON_TYPE).build();
  }
}
