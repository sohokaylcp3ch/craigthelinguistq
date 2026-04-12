package com.easylinker.proxy.server.app.service;

import com.easylinker.proxy.server.app.dao.DeviceLocationRepository;
import com.easylinker.proxy.server.app.model.device.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("DeviceLocationService")
public class DeviceLocationService {
    @Autowired
    DeviceLocationRepository deviceLocationRepository;

    public void save(Location location) {
        deviceLocationRepository.save(location);
    }
}
