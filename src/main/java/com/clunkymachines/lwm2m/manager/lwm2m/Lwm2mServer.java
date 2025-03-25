package com.clunkymachines.lwm2m.manager.lwm2m;

import java.io.IOException;
import java.util.List;

import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.leshan.core.model.*;
import org.eclipse.leshan.demo.LwM2mDemoConstant;
import org.eclipse.leshan.server.*;
import org.eclipse.leshan.server.model.*;
import org.eclipse.leshan.servers.security.SecurityStore;
import org.eclipse.leshan.transport.californium.server.endpoint.CaliforniumServerEndpointsProvider;
import org.eclipse.leshan.transport.californium.server.endpoint.coap.CoapServerProtocolProvider;
import org.eclipse.leshan.transport.californium.server.endpoint.coaps.CoapsServerProtocolProvider;

/**
 * Service in charge of providing the LWM2M server connectivity.
 *
 * Must call {@link #start()} to start listening to LWM2M ports
 */
public class Lwm2mServer {

  private final LeshanServer server;
  private final List<ObjectModel> models;
  private final LwM2mModelProvider modelProvider;


  public Lwm2mServer(int localPort, int localSecurePort, SecurityStore securityStore) {
    // Prepare LWM2M server
    var builder = new LeshanServerBuilder();

    CaliforniumServerEndpointsProvider.Builder cfBuilder = new CaliforniumServerEndpointsProvider.Builder(
        new CoapServerProtocolProvider(), new CoapsServerProtocolProvider());

    // CoAP Config for Californium
    var coapConfig = cfBuilder.createDefaultConfiguration();

    // TODO make it configurable
    coapConfig.set(CoapConfig.MAX_RESOURCE_BODY_SIZE, 200_000); // we want to support large resource payload for FOTA
    coapConfig.set(CoapConfig.PREFERRED_BLOCK_SIZE, 1024); // by default block is 512, we support up 1024
    coapConfig.set(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY, false);

    cfBuilder.addEndpoint("coap://0.0.0.0:"+localPort);
    cfBuilder.addEndpoint("coaps://0.0.0.0:"+localSecurePort);

    builder.setEndpointsProviders(cfBuilder.build());

    // Define model provider
    models = ObjectLoader.loadAllDefault();
    try {
      models.addAll(ObjectLoader.loadDdfResources("/models/", LwM2mDemoConstant.modelPaths));
    } catch (IOException | InvalidModelException | InvalidDDFFileException e) {
      throw new IllegalStateException("Error reading the models", e);
    }
    modelProvider = new VersionedModelProvider(models);
    builder.setObjectModelProvider(modelProvider);
    builder.setSecurityStore(securityStore);

    server = builder.build();
  }

  public void start() {
    server.start();
  }

  public void destroy() {
    server.destroy();
  }
  public LeshanServer getLeshanServer() {
    return server;
  }
}
