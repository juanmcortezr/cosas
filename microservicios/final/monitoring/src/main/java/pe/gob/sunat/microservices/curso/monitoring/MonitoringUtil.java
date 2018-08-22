package pe.gob.sunat.microservices.curso.monitoring;

import brave.http.HttpTracing;
import com.smoketurner.dropwizard.zipkin.ZipkinFactory;
import com.smoketurner.dropwizard.zipkin.client.ZipkinClientBuilder;
import com.smoketurner.dropwizard.zipkin.client.ZipkinClientConfiguration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;

import javax.ws.rs.client.Client;
import java.util.Optional;

public class MonitoringUtil {

  public static Optional<HttpTracing> register(ZipkinFactory zipkinFactory, Environment environment) {
    final Optional<HttpTracing> tracing = zipkinFactory.build(environment);

    /*final Client client;
    if (tracing.isPresent()) {
      client =
        new ZipkinClientBuilder(environment, tracing.get())
          .build(configuration.getZipkinClient());
    } else {
      final ZipkinClientConfiguration clientConfig = configuration.getZipkinClient();
      client =
        new JerseyClientBuilder(environment)
          .using(clientConfig)
          .build(clientConfig.getServiceName());
    }*/

    PrometheusConfig config = PrometheusConfig.DEFAULT;
    CollectorRegistry collectorRegistry = CollectorRegistry.defaultRegistry;
    Clock clock = Clock.SYSTEM;

    PrometheusMeterRegistry registry = new PrometheusMeterRegistry(config, collectorRegistry, clock);

    new ClassLoaderMetrics().bindTo(registry);
    //new JettyStatisticsMetrics()
    new JvmMemoryMetrics().bindTo(registry);
    new JvmGcMetrics().bindTo(registry);
    new ProcessorMetrics().bindTo(registry);
    new JvmThreadMetrics().bindTo(registry);
    new FileDescriptorMetrics().bindTo(registry);
    new UptimeMetrics().bindTo(registry);
    new LogbackMetrics().bindTo(registry);

    io.prometheus.client.hotspot.DefaultExports.initialize();
    environment.servlets().addServlet("metricsServlet", io.prometheus.client.exporter.MetricsServlet.class).addMapping("/prom");

    return tracing;
  }
}
