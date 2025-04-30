package com.clunkymachines.lwm2m.manager.ui;

import java.io.IOException;

import org.eclipse.leshan.core.node.LwM2mPath;
import org.eclipse.leshan.core.node.LwM2mSingleResource;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.response.ReadResponse;

import com.clunkymachines.lwm2m.manager.lwm2m.Lwm2mServer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

public class DeviceRequestServlet extends HttpServlet {

    private final Lwm2mServer server;

    public DeviceRequestServlet(Lwm2mServer server) {
	this.server = server;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	var type = req.getParameter("type");
	var endpoint = req.getParameter("endpoint");
	var path = req.getParameter("path");
	if ("read".equalsIgnoreCase(type)) {
	    var registration = server.getLeshanServer().getRegistrationService().getByEndpoint(endpoint);
	    if (registration == null) {
		resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		resp.getWriter().format("No registration for endpoint '%s'", endpoint);
		return;
	    }

	    try {
		ReadResponse response = server.getLeshanServer().send(registration, new ReadRequest(path));
		if (response.isSuccess()) {
		    resp.setStatus(HttpServletResponse.SC_CREATED);
		    server.getDataStore().push(registration.getEndpoint(), new LwM2mPath(path), response.getContent());
		}
	    } catch (InterruptedException e) {
		resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		resp.getWriter().format("request interrupted ");
		return;
	    }
	}
    }
}
