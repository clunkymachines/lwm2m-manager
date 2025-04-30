package com.clunkymachines.lwm2m.manager.ui.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {
    
    private Map<String, Object> data;
    private List<DataStoreListener> listeners;

    public DataStore() {
        data = new HashMap<>();
        listeners = new ArrayList<>();
    }

    public Object push(String endpoint, String path, Object newValue) {
        Object oldValue = data.put(toKey(endpoint,path), newValue);
        notifyListeners(endpoint, path, toStringValue(newValue), toStringValue(oldValue));
        return oldValue;
    }
    
    public String get(String endpoint, String path) {
		return toStringValue(data.get(toKey(endpoint,path)));
	}
    
    private String toStringValue(Object value) {
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

    private void notifyListeners(String endpoint, String path, String newValue, String oldValue) {
        for (DataStoreListener listener : listeners) {
            listener.newData(endpoint, path, newValue, oldValue);
        }
    }

    public static interface DataStoreListener {
        void newData(String endpoint, String path, String newValue, String oldValue);
    }
}

