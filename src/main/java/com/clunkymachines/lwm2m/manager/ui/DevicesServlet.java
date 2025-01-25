package com.clunkymachines.lwm2m.manager.ui;

import java.io.IOException;
import java.util.*;

import org.eclipse.leshan.servers.security.SecurityInfo;

import com.clunkymachines.lwm2m.manager.model.Device;
import com.clunkymachines.lwm2m.manager.repository.DeviceRepository;

import gg.jte.*;
import gg.jte.output.PrintWriterOutput;
import gg.jte.resolve.ResourceCodeResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

public class DevicesServlet extends HttpServlet {

    private DeviceRepository deviceRepository;

    private final TemplateEngine templateEngine;

    public DevicesServlet(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
        templateEngine = TemplateEngine.create(new ResourceCodeResolver("ui/templates"),ContentType.Html);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var path = req.getPathInfo();
        if (path == null) {
            path = "";
        }

        switch (path) {
            case "/add" -> {
                templateEngine.render("create-device.jte", null, new PrintWriterOutput(resp.getWriter()));
            }
            default -> {
                var modelMap = new HashMap<String,Object>();
                modelMap.put("devices", deviceRepository.list());
                templateEngine.render("devices.jte", modelMap, new PrintWriterOutput(resp.getWriter()));
            }
        }
        System.err.println(req.getPathInfo());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO validation :D

        var endpoint = req.getParameter("endpoint");
        var name = req.getParameter("name");

        var psk = req.getParameter("psk");
        var pskid = req.getParameter("pskid");

        // extract SecutrityInfo
        SecurityInfo securityInfo = null;
        if (psk != null && !psk.isBlank() && pskid != null && !pskid.isBlank()) {
            securityInfo = SecurityInfo.newPreSharedKeyInfo(endpoint, pskid, HexFormat.of().parseHex(psk));
        } else {
            // no security? do we allow this to disable a device?
        }

        var device = new Device(endpoint,name,securityInfo);

        deviceRepository.add(device);
        // let's render back the list of device
        resp.sendRedirect("/device");
    }
}
