package com.easylinker.proxy.server.app.dao;

import com.easylinker.proxy.server.app.model.device.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceLocationRepository extends JpaRepository<Location,Long> {
}
