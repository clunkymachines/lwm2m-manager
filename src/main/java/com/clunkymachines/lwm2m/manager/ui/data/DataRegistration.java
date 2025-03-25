package com.clunkymachines.lwm2m.manager.ui.data;

import java.time.Instant;
import java.util.List;

public record DataRegistration(String regId, Instant registrationData, Instant registrationUpdate, String endpoint, List<DataObject> objects) {

}
