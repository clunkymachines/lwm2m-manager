package com.clunkymachines.lwm2m.manager.ui.data;

public record DataResource(int id, String name, boolean read, boolean write, boolean execute) {
}
