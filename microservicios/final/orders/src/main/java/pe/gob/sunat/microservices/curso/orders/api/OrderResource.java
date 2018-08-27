package pe.gob.sunat.microservices.curso.orders.api;

import pe.gob.sunat.microservices.curso.orders.model.Order;
import pe.gob.sunat.microservices.curso.orders.service.InvalidOrderException;
import pe.gob.sunat.microservices.curso.orders.service.OrderService;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

@RolesAllowed("ADMIN")
@Path("/v1/orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {
  private final OrderService orderService;

  public OrderResource(OrderService orderService) {
    this.orderService = orderService;
  }


  @POST
  public Order create(@Valid Order order) {
    try {
	  return orderService.create(order);
	}
	catch (Exception e) {
	  throw new InvalidOrderException(e.getMessage(), String.valueOf(order.getId()));		
	}
  }
  
  @GET
  @Path("/{id}")
  public Optional<Order> find(@PathParam("id") Long id) {
    return orderService.findById(id);
  }

  @DELETE
  @Path("/{id}")
  public void delete(@PathParam("id") Long id) {
    orderService.delete(id);
  }
  
  @PUT
  public void update(@Valid Order order) {
    orderService.update(order);
  }

  @GET
  @Path("/_customer")
  public List<Order> ordersList(@QueryParam("id") Long id) {
	  System.out.println("invocacion a _customer con id: " + id);
	  return orderService.ordersByCustomer(id);
  }
  
  @GET
  @Path("/_address")
  public List<Order> ordersByAddressList(@QueryParam("id") Long id) {
	  System.out.println("invocacion a _address con id: " + id);
    return orderService.ordersByAddress(id);
  }
}
