package com.clunkymachines.lwm2m.manager.ui.data;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.leshan.core.node.LwM2mMultipleResource;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.node.LwM2mObject;
import org.eclipse.leshan.core.node.LwM2mObjectInstance;
import org.eclipse.leshan.core.node.LwM2mPath;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.LwM2mResourceInstance;
import org.eclipse.leshan.core.node.LwM2mRoot;
import org.eclipse.leshan.core.node.LwM2mSingleResource;

public class DataStore {

    private Map<String, Object> data;
    private List<DataStoreListener> listeners;

    public DataStore() {
	data = new HashMap<>();
	listeners = new ArrayList<>();
    }

    public void push(String endpoint, Map<LwM2mPath, LwM2mNode> newData) {
	addFlatMapValues(endpoint, //
		newData.entrySet().stream() //
			.flatMap(e -> nodeToValueMap(e.getKey(), e.getValue()).entrySet().stream()) //
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
    }

    public void push(String endpoint, LwM2mPath path, LwM2mNode newValue) {
	addFlatMapValues(endpoint, nodeToValueMap(path, newValue));
    }

    private void addFlatMapValues(String endpoint, Map<String, String> flatData) {
	// add data
	data.putAll(flatData.entrySet().stream()
		.collect(Collectors.toMap(e -> toKey(endpoint, e.getKey()), Entry::getValue)));

	// notify listener
	notifyListeners(endpoint, flatData);
    }

    private Map<String, String> nodeToValueMap(LwM2mPath path, LwM2mNode newNode) {
	HashMap<String, String> newData = new HashMap<>();
	if (path.isRoot()) {
	    if (newNode instanceof LwM2mRoot object) {
		for (Entry<Integer, LwM2mObject> objectEntry : object.getObjects().entrySet()) {
		    newData.putAll(nodeToValueMap(path.append(objectEntry.getKey()), objectEntry.getValue()));
		}
	    }
	} else if (path.isObject()) {
	    if (newNode instanceof LwM2mObject object) {
		for (Entry<Integer, LwM2mObjectInstance> instanceEntry : object.getInstances().entrySet()) {
		    newData.putAll(nodeToValueMap(path.append(instanceEntry.getKey()), instanceEntry.getValue()));
		}
	    }
	} else if (path.isObjectInstance()) {
	    if (newNode instanceof LwM2mObjectInstance instance) {
		for (Entry<Integer, LwM2mResource> resourceEntry : instance.getResources().entrySet()) {
		    newData.putAll(nodeToValueMap(path.append(resourceEntry.getKey()), resourceEntry.getValue()));
		}
	    }
	} else if (path.isResource()) {
	    if (newNode instanceof LwM2mSingleResource singleResource) {
		newData.put(path.toString(), toStringValue(singleResource.getValue()));
	    } else if (newNode instanceof LwM2mMultipleResource multipleResource) {
		for (Entry<Integer, LwM2mResourceInstance> resourceInstanceEntry : multipleResource.getInstances()
			.entrySet()) {
		    newData.putAll(nodeToValueMap(path.append(resourceInstanceEntry.getKey()),
			    resourceInstanceEntry.getValue()));
		}
	    }
	} else if (path.isResourceInstance()) {
	    if (newNode instanceof LwM2mResourceInstance instance) {
		newData.put(path.toString(), toStringValue(instance.getValue()));
	    }
	} else {
	    throw new IllegalArgumentException("unexpected type of path");
	}
	return newData;
    }

    public String get(String endpoint, String path) {
	return toStringValue(data.get(toKey(endpoint, path)));
    }

    private String toStringValue(Object value) {
	if (value instanceof byte[] bytes) {
	    return Base64.getEncoder().encodeToString(bytes); // or Hex.encodeHexString(bytes)
	}
	return value != null ? value.toString() : "";
    }

    private String toKey(String endpoint, String path) {
	return endpoint + "/" + path;
    }

    public void addListener(DataStoreListener listener) {
	listeners.add(listener);
    }

    public void removeListener(DataStoreListener listener) {
	listeners.remove(listener);
    }

    private void notifyListeners(String endpoint, Map<String /* path */, String/* value */> data) {
	for (DataStoreListener listener : listeners) {
	    listener.newData(endpoint, data);
	}
    }

    public static interface DataStoreListener {
	void newData(String endpoint, Map<String /* path */, String/* value */> data);
    }
}
