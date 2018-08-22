package pe.gob.sunat.microservices.curso.orders.client;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderServiceClient {

  @GET("v1/orders/_customer")
  Call<List<Order>> ordersList(@Query("id") Long id);

  @GET("v1/orders/_address")
  Call<List<Order>> ordersByAddressList(@Query("id") Long id);
}


