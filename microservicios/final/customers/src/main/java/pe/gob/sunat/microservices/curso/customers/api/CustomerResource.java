package pe.gob.sunat.microservices.curso.customers.api;


import pe.gob.sunat.microservices.curso.customers.model.Address;
import pe.gob.sunat.microservices.curso.customers.model.Customer;
import pe.gob.sunat.microservices.curso.customers.service.AddressService;
import pe.gob.sunat.microservices.curso.customers.service.CustomerService;
import pe.gob.sunat.microservices.curso.customers.service.InvalidCustomerException;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

@RolesAllowed("ADMIN")
@Path("/v1/customers")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerResource {
  private final CustomerService customerService;
  private final AddressService addressService;
  
  public CustomerResource(CustomerService customerService, AddressService addressService) {
    this.customerService = customerService;
    this.addressService = addressService;
  }

  @POST
  public Customer create(@Valid Customer customer) {
    return customerService.create(customer);		
  }

  @GET
  @Path("/{id}")
  public Optional<Customer> find(@PathParam("id") Long id, @QueryParam("address") @DefaultValue("false") Boolean includeAddresses) {
    return customerService.findById(id, includeAddresses);
  }

  @DELETE
  @Path("/{id}")
  public void delete(@PathParam("id") Long id) {
	try {
	  customerService.delete(id);
	}
	catch (Exception e) {
  	  throw new InvalidCustomerException(e.getMessage(), String.valueOf(id));				
	}
  }
  
  @PUT
  @Path("/{id}")
  public void update(@Valid Customer customer) {
    customerService.update(customer);
  }
  
  @GET
  @Path("/{id}/addresses")
  public List<Address> addressList(@PathParam("id") Long id) {
    return addressService.addressesByCustomer(id);
  }
  
  @POST
  @Path("/{id}/addresses")
  public Address addAddress(@PathParam("id") Long id, @Valid Address address) {
    return addressService.create(id, address);
  }

  @DELETE
  @Path("/{id}/addresses")
  public void deleteAddress(@PathParam("id") Long id) {
	try {
	  addressService.delete(id);
	}
	catch (Exception e) {
  	  throw new InvalidCustomerException(e.getMessage(), String.valueOf(id));		
	}
  }
}
