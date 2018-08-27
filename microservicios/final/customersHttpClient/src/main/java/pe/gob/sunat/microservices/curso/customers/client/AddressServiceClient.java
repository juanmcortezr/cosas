package pe.gob.sunat.microservices.curso.customers.client;

import java.util.Optional;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AddressServiceClient {
  //@POST("v1/customers")
  //Call<Customer> create(@Body Customer customer);

  //@GET("v1/customers/{id}")
  //Call<Customer> get(@Path("id") Long id);
  
  @POST("v1/addressess")
  Call<Address> create(@Body Address address);

  @GET("v1/addressess/{id}")
  Call<Address> getAddress(@Path("id") Long id);
  

  @GET("v1/addressess/{id}")
  Call<Address> validateAddressCustomer(@Path("id") Long id);

  @GET("v1/addressess/customer/{customerId}/{id}")
  Call<Address> findByCustomer(@Path("customerId") Long customerId, @Path("id") Long id);
}


