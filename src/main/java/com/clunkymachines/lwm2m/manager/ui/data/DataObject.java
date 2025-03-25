package com.clunkymachines.lwm2m.manager.ui.data;

import java.util.List;

public record DataObject(int id, String version, String name, String description, List<DataObjectInstance> instances) {
}
