package pe.gob.sunat.microservices.curso.security.client;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SecurityServiceClient {

  @POST("v1/users")
  Call<User> create(@Body User user);

  @POST("v1/users/_login")
  Call<User> login(@Body User user);
}
