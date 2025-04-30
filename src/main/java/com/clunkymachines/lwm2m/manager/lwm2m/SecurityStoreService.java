package com.clunkymachines.lwm2m.manager.lwm2m;

import org.eclipse.leshan.core.peer.OscoreIdentity;
import org.eclipse.leshan.servers.security.*;

import com.clunkymachines.lwm2m.manager.repository.DeviceRepository;

public class SecurityStoreService implements SecurityStore {

  private final DeviceRepository deviceRepository;

  public SecurityStoreService(DeviceRepository deviceRepository) {
    this.deviceRepository = deviceRepository;
  }

  @Override
  public SecurityInfo getByEndpoint(String endpoint) {
    var device = deviceRepository.get(endpoint);
    if (device == null) {
      return null;
    } else {
      return device.securityInfo();
    }
  }

  @Override
  public SecurityInfo getByIdentity(String pskIdentity) {
    var device = deviceRepository.getByPskIdentity(pskIdentity);
    if (device == null) {
      return null;
    } else {
      return device.securityInfo();
    }
  }

  @Override
  public SecurityInfo getByOscoreIdentity(OscoreIdentity oscoreIdentity) {
    throw new UnsupportedOperationException("Unimplemented method 'getByOscoreIdentity'");
  }

}
