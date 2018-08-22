package pe.gob.sunat.microservices.curso.customers.api;


import pe.gob.sunat.microservices.curso.customers.model.Address;
import pe.gob.sunat.microservices.curso.customers.model.Customer;
import pe.gob.sunat.microservices.curso.customers.service.AddressService;
import pe.gob.sunat.microservices.curso.customers.service.InvalidAddressException;
import pe.gob.sunat.microservices.curso.customers.service.InvalidCustomerException;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/v1/addressess")
@Produces(MediaType.APPLICATION_JSON)
public class AddressResource {

  private final AddressService addressService;

  public AddressResource(AddressService addressService) {
    this.addressService = addressService;
  }

  @POST
  public Address create(@Valid Address address) {
	try {
      return addressService.create(address);
	}
	catch (Exception e) {
	  throw new InvalidAddressException(e.getMessage(), String.valueOf(address.getId()));
	}
  }

  @GET
  @Path("/{id}")
  public Optional<Address> find(@PathParam("id") Long id) {
	// System.out.println("se llego al metodo con id: " + id + " " +addressService.findById(id).get().getId());
    return addressService.findById(id);
  }

  @DELETE
  @Path("/{id}")
  public void delete(@PathParam("id") Long id) {
	  addressService.delete(id);
  }
  
  @PUT
  @Path("/{id}")
  public void update(@Valid Address address) {
	  addressService.update(address);
  }  
}

