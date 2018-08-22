package pe.gob.sunat.microservices.curso.customers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import pe.gob.sunat.microservices.curso.customers.service.InvalidOrderException;

import java.util.HashMap;
import java.util.Map;

@Provider
public class InvalidOrderExceptionMapper implements ExceptionMapper<InvalidOrderException> {
  @Override
  public Response toResponse(InvalidOrderException exception) {
    Map data = new HashMap();
    data.put("message", exception.getMessage());
    data.put("id", exception.getOrderId());
    return Response.status(422).entity(data).type(MediaType.APPLICATION_JSON_TYPE).build();
  }
}
