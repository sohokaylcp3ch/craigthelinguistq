package com.easylinker.proxy.server.app.dao;

import com.easylinker.proxy.server.app.model.device.Device;
import com.easylinker.proxy.server.app.model.device.DeviceJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceJobRepository extends JpaRepository<DeviceJob, Long> {

    DeviceJob findTopByDevice(Device device);
}
