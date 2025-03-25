package com.clunkymachines.lwm2m.manager.ui;

import java.io.IOException;
import java.util.*;

import org.eclipse.leshan.server.LeshanServer;

import com.clunkymachines.lwm2m.manager.ui.data.*;

import gg.jte.*;
import gg.jte.output.PrintWriterOutput;
import gg.jte.resolve.ResourceCodeResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

public class DeviceDetailsServlet extends HttpServlet {
  private final TemplateEngine templateEngine;
  private final LeshanServer leshanServer;

  public DeviceDetailsServlet(LeshanServer leshanServer) {
    this.leshanServer = leshanServer;
    templateEngine = TemplateEngine.create(new ResourceCodeResolver("ui/templates"), ContentType.Html);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    var endpoint = req.getParameter("endpoint");
    if (endpoint == null || endpoint.isBlank()) {
      resp.sendError(400, "parameter 'endpoint' is missing");
      return;
    }
    var registration = leshanServer.getRegistrationService().getByEndpoint(endpoint);
    if (registration == null) {
      resp.sendError(404, "No registration for " + endpoint);
      return;
    }

    var model = leshanServer.getModelProvider().getObjectModel(registration);

    // create the list of objects for the template data
    var templateMap = new HashMap<String,Object>();

    var objects = new ArrayList<DataObject>();
    for (var o : registration.getSupportedObject().keySet()) {
      var instances = new ArrayList<DataObjectInstance>();
      var objectModel = model.getObjectModel(o);
      for (var path :registration.getAvailableInstances()) {
        if (path.getObjectId() == o) {
            var resources = new ArrayList<DataResource>();
            for (var resourceEntry : objectModel.resources.entrySet()) {
                resources.add(new DataResource(resourceEntry.getKey(), resourceEntry.getValue().name, resourceEntry.getValue().operations.isReadable(), resourceEntry.getValue().operations.isWritable(), resourceEntry.getValue().operations.isExecutable()));
            }
            instances.add(new DataObjectInstance(path.getObjectInstanceId(), resources));
        }
      }
      objects.add(new DataObject(o, objectModel.version , objectModel.name, objectModel.description, instances));
    }

    templateMap.put("objects", objects);
    templateEngine.render("device-details.jte", templateMap, new PrintWriterOutput(resp.getWriter()));

  }

}
