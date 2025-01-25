package com.clunkymachines.lwm2m.manager.model;

import org.eclipse.leshan.servers.security.SecurityInfo;

public record Device(String endpoint, String name, SecurityInfo securityInfo){
}
