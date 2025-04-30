package com.clunkymachines.lwm2m.manager.lwm2m;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.scandium.config.DtlsConfig;
import org.eclipse.leshan.core.model.InvalidDDFFileException;
import org.eclipse.leshan.core.model.InvalidModelException;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.node.LwM2mPath;
import org.eclipse.leshan.core.node.LwM2mSingleResource;
import org.eclipse.leshan.core.node.TimestampedLwM2mNodes;
import org.eclipse.leshan.core.request.SendRequest;
import org.eclipse.leshan.demo.LwM2mDemoConstant;
import org.eclipse.leshan.server.LeshanServer;
import org.eclipse.leshan.server.LeshanServerBuilder;
import org.eclipse.leshan.server.model.LwM2mModelProvider;
import org.eclipse.leshan.server.model.VersionedModelProvider;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.send.SendListener;
import org.eclipse.leshan.servers.security.SecurityStore;
import org.eclipse.leshan.transport.californium.server.endpoint.CaliforniumServerEndpointsProvider;
import org.eclipse.leshan.transport.californium.server.endpoint.coap.CoapServerProtocolProvider;
import org.eclipse.leshan.transport.californium.server.endpoint.coaps.CoapsServerProtocolProvider;

import com.clunkymachines.lwm2m.manager.ui.data.DataStore;

/**
 * Service in charge of providing the LWM2M server connectivity.
 *
 * Must call {@link #start()} to start listening to LWM2M ports
 */
public class Lwm2mServer {

    private final LeshanServer server;
    private final List<ObjectModel> models;
    private final LwM2mModelProvider modelProvider;
    private final DataStore dataStore;

    public Lwm2mServer(int localPort, int localSecurePort, SecurityStore securityStore) {
	// Prepare LWM2M server
	var builder = new LeshanServerBuilder();

	CaliforniumServerEndpointsProvider.Builder cfBuilder = new CaliforniumServerEndpointsProvider.Builder(
		new CoapServerProtocolProvider(), new CoapsServerProtocolProvider());

	// CoAP Config for Californium
	var coapConfig = cfBuilder.createDefaultConfiguration();

	// TODO make it configurable
	coapConfig.set(CoapConfig.MAX_RESOURCE_BODY_SIZE, 200_000); // we want to support large resource payload for
								    // FOTA
	coapConfig.set(CoapConfig.PREFERRED_BLOCK_SIZE, 1024); // by default block is 512, we support up 1024
	coapConfig.set(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY, false);

	cfBuilder.addEndpoint("coap://0.0.0.0:" + localPort);
	cfBuilder.addEndpoint("coaps://0.0.0.0:" + localSecurePort);

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

	// Define data store
	dataStore = new DataStore();
	server.getSendService().addListener(new SendListener() {

	    @Override
	    public void onError(Registration registration, String errorMessage, Exception error) {
	    }

	    @Override
	    public void dataReceived(Registration registration, TimestampedLwM2mNodes data, SendRequest request) {
		dataStore.push(registration.getEndpoint(), data.getMostRecentNodes());
	    }
	});
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

    public DataStore getDataStore() {
	return dataStore;
    }
}
