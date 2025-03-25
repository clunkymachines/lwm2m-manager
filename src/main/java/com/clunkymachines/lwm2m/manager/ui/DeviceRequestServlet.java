package com.clunkymachines.lwm2m.manager.ui;

import java.io.IOException;

import org.eclipse.leshan.core.request.ReadRequest;

import com.clunkymachines.lwm2m.manager.lwm2m.Lwm2mServer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

public class DeviceRequestServlet extends HttpServlet {

  private final Lwm2mServer server;

  public DeviceRequestServlet(Lwm2mServer server) {
    this.server = server;
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // TODO: validation
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

      var request = new ReadRequest(path);

    }
  }
}
