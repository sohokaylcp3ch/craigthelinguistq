package com.easylinker.proxy.server.app.service;

import com.easylinker.proxy.server.app.dao.DeviceJobRepository;
import com.easylinker.proxy.server.app.model.device.Device;
import com.easylinker.proxy.server.app.model.device.DeviceJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceJobService {
    @Autowired
    DeviceJobRepository deviceJobRepository;

    public void save(DeviceJob scheduleJob) {
        deviceJobRepository.save(scheduleJob);
    }

    /**
     * 根据设备的ID查询任务
     *
     * @param device
     * @return
     */

    public DeviceJob findAJobByDevice(Device device) {
        return deviceJobRepository.findTopByDevice(device);

    }

    /**
     * 删除设备绑定的JOB
     *
     * @param deviceJob
     */

    public void delete(DeviceJob deviceJob) {
        deviceJobRepository.delete(deviceJob);

    }
}
