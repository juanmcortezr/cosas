package pe.gob.sunat.microservices.curso.orders.client;

import brave.Tracing;
import brave.okhttp3.TracingCallFactory;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import pe.gob.sunat.microservices.curso.security.BasicAuthInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class OrderServiceClientUtil {

  public static OrderServiceClient register(String baseUrl, Tracing tracing, String username, String password) {

    OkHttpClient client = new OkHttpClient.Builder()
      .addInterceptor(new BasicAuthInterceptor(username, password))
      .build();
    Call.Factory factory = TracingCallFactory.create(tracing, client);

    //System.out.println("base url: " + baseUrl + " " + username + " " + password);
    Retrofit retrofit = new Retrofit.Builder()
      .callFactory(factory)
      .baseUrl(baseUrl)
      .addConverterFactory(JacksonConverterFactory.create())
      .build();

    return retrofit.create(OrderServiceClient.class);
  }
}
