package pe.gob.sunat.microservices.curso.security;

import brave.Tracing;
import brave.okhttp3.TracingCallFactory;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Environment;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import pe.gob.sunat.microservices.curso.security.client.ApplicationUser;
import pe.gob.sunat.microservices.curso.security.client.SecurityServiceClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class SecurityUtil {
  public static SecurityServiceClient register(String securityBaseUrl, Environment environment, Tracing tracing) {

    OkHttpClient client = new OkHttpClient.Builder()
      .build();

    Call.Factory factory = TracingCallFactory.create(tracing, client);

    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(securityBaseUrl)
      .callFactory(factory)
      .addConverterFactory(JacksonConverterFactory.create())
      .build();
    SecurityServiceClient securityServiceClient = retrofit.create(SecurityServiceClient.class);



    environment.jersey().register(new AuthDynamicFeature(
      new BasicCredentialAuthFilter.Builder<ApplicationUser>()
        .setAuthenticator(new CredentialsAuthenticator(securityServiceClient))
        .setAuthorizer(new ApplicationAuthorizer())
        .setRealm("Sunat Realm")
        .buildAuthFilter()));
    environment.jersey().register(RolesAllowedDynamicFeature.class);
    //If you want to use @Auth to inject a custom Principal type into your resource
    environment.jersey().register(new AuthValueFactoryProvider.Binder<>(ApplicationUser.class));

    return securityServiceClient;
  }
}
