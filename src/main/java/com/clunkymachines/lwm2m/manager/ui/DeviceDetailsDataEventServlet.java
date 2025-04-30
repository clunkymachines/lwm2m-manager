package com.clunkymachines.lwm2m.manager.ui;

import java.io.IOException;

import org.eclipse.jetty.ee10.servlets.EventSource;
import org.eclipse.jetty.ee10.servlets.EventSourceServlet;

import com.clunkymachines.lwm2m.manager.ui.data.DataStore;
import com.clunkymachines.lwm2m.manager.ui.data.DataStore.DataStoreListener;

import jakarta.servlet.http.HttpServletRequest;

public class DeviceDetailsDataEventServlet extends EventSourceServlet {

	private static final long serialVersionUID = 1L;

	private DataStore dataStore;

	public DeviceDetailsDataEventServlet(DataStore store) {
		this.dataStore = store;
	}

	@Override
	protected EventSource newEventSource(HttpServletRequest request) {
		return new DeviceDetailsDataEventSource(request.getParameter("ep"));
	}

	public class DeviceDetailsDataEventSource implements EventSource {

		DataStoreListener listener;
		String endpointToListen;

		public DeviceDetailsDataEventSource(String endpoint) {
			this.endpointToListen = endpoint;
			
			System.out.println("SSE connection for " + endpointToListen + " " + this.hashCode());
		}

		@Override
		public void onOpen(final Emitter emitter) throws IOException {
			listener = (endpoint, path, newValue, oldValue) -> {
				System.out.println(endpoint +  path  + "<div>" + newValue + "</div>");
				try {
					if (endpoint.equals(endpointToListen)) {
						emitter.event(endpoint + path, "<div>" + newValue + "</div>");
					}
				} catch (IOException e) {
					System.out.println("something wrong");
					e.printStackTrace();
				}
			};

			dataStore.addListener(listener);
		}

		@Override
		public void onClose() {
			dataStore.removeListener(listener);
			System.out.println("SSE connection closed for " + endpointToListen + " " + this.hashCode());
		}
	}

}