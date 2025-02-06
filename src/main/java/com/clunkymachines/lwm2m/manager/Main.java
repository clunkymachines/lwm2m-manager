package com.clunkymachines.lwm2m.manager;

import java.nio.file.Paths;

import org.eclipse.jetty.ee10.servlet.*;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;
import org.slf4j.*;

import com.clunkymachines.lwm2m.manager.lwm2m.*;
import com.clunkymachines.lwm2m.manager.repository.*;
import com.clunkymachines.lwm2m.manager.ui.DevicesServlet;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOG.info("LWM2M Manager starting");
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.start();

        var deviceRepository = new DeviceRepository(dbManager);

        try {
            var httpServer = new Server(8080);
            var webAppContext = new WebAppContext();
            webAppContext.setContextPath("/");

            webAppContext.setBaseResourceAsPath(Paths.get(Main.class.getClassLoader().getResource("ui/static").toURI()));
            webAppContext.setParentLoaderPriority(true);
            httpServer.setHandler(webAppContext);

            DefaultServlet staticServlet = new DefaultServlet();
            ServletHolder staticHolder = new ServletHolder(staticServlet);
            staticHolder.setInitParameter("resourceBase",Main.class.getClassLoader().getResource("ui/static").toExternalForm());
            staticHolder.setInitParameter("pathInfoOnly", "true");
            staticHolder.setInitParameter("gzip", "true");
            staticHolder.setInitParameter("cacheControl", "public, max-age=31536000");
            webAppContext.addServlet(staticHolder, "/*");

            var deviceServletHolder = new ServletHolder(new DevicesServlet(deviceRepository));
            webAppContext.addServlet(deviceServletHolder, "/device/*");
            LOG.info("Starting LWM2M server");

            Lwm2mServer lwm2mServer = new Lwm2mServer(5683, 5684, new SecurityStoreService(deviceRepository));
            lwm2mServer.start();

            LOG.info("Starting web server");
            httpServer.start();
        } catch (Exception e) {
            LOG.error("HTTP Server error", e);
        }
    }
}