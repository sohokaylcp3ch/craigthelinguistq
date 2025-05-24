package com.easylinker.proxy.server.app.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.dao.DeviceGroupRepository;
import com.easylinker.proxy.server.app.dao.DeviceRepository;
import com.easylinker.proxy.server.app.model.device.Device;
import com.easylinker.proxy.server.app.model.device.DeviceGroup;
import com.easylinker.proxy.server.app.model.user.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备service
 */
@Service("DeviceService")
public class DeviceService {
    @Autowired

    DeviceRepository deviceRepository;


    @Autowired
    DeviceGroupRepository deviceGroupRepository;

    public void save(Device device) {
        deviceRepository.save(device);

    }

    public void delete(Device device) {
        deviceRepository.delete(device);

    }

    public Device findADevice(Long id) {
        return deviceRepository.findTopById(id);
    }


    public JSONObject getAllDevicesByAppUser(AppUser appUser, Pageable pageable) {
        JSONArray data = new JSONArray();
        JSONObject pageJson = new JSONObject();
        Page<Device> dataPage = deviceRepository.findAllByAppUser(appUser, pageable);

        pageJson.put("page", dataPage.getNumber());
        pageJson.put("totalElements", dataPage.getTotalElements());
        pageJson.put("totalPages", dataPage.getTotalPages());
        pageJson.put("size", dataPage.getSize());
        pageJson.put("isLast", dataPage.isLast());
        pageJson.put("isFirst", dataPage.isFirst());

        for (Device device : dataPage.getContent()) {
            JSONObject deviceJson = new JSONObject();
            deviceJson.put("id", device.getId());
            deviceJson.put("isOnline", device.isOnline());
            deviceJson.put("name", device.getDeviceName());
            deviceJson.put("barCode", device.getBarCode());
            deviceJson.put("lastActiveDate", device.getLastActiveDate());
            deviceJson.put("location", device.getLocation().toString());
            deviceJson.put("describe", device.getDeviceDescribe());
            data.add(deviceJson);
        }
        pageJson.put("data", data);
        return pageJson;
    }


    public JSONObject getAllDevicesByAppUserAndGroup(AppUser appUser, DeviceGroup deviceGroup, Pageable pageable) {
        JSONArray data = new JSONArray();
        Page<Device> dataPage = deviceRepository.findAllByAppUserAndDeviceGroup(appUser, deviceGroup, pageable);


        JSONObject pageJson = new JSONObject();
        pageJson.put("page", dataPage.getNumber());
        pageJson.put("total", dataPage.getTotalPages());
        pageJson.put("size", dataPage.getSize());
        pageJson.put("isLast", dataPage.isLast());
        pageJson.put("isFirst", dataPage.isFirst());
        pageJson.put("totalPages", dataPage.getTotalPages());


        pageJson.put("totalElements", dataPage.getTotalElements());

        for (Device device : dataPage.getContent()) {
            JSONObject deviceJson = new JSONObject();
            deviceJson.put("id", device.getId());
            deviceJson.put("barCode", device.getBarCode());
            deviceJson.put("name", device.getDeviceName());
            deviceJson.put("isOnline", device.isOnline());
            deviceJson.put("lastActiveDate", device.getLastActiveDate());
            deviceJson.put("describe", device.getDeviceDescribe());
            deviceJson.put("location", device.getLocation().toString());
            data.add(deviceJson);
        }
        pageJson.put("data", data);
        return pageJson;
    }


    public JSONObject getAllDevices(Pageable pageable) {
        JSONArray data = new JSONArray();
        Page<Device> dataPage = deviceRepository.findAll(pageable);
        JSONObject pageJson = new JSONObject();

        pageJson.put("page", dataPage.getNumber());
        pageJson.put("total", dataPage.getTotalPages());
        pageJson.put("size", dataPage.getSize());
        pageJson.put("isLast", dataPage.isLast());
        pageJson.put("totalPages", dataPage.getTotalPages());
        pageJson.put("isFirst", dataPage.isFirst());
        pageJson.put("totalElements", dataPage.getTotalElements());
        for (Device device : dataPage.getContent()) {
            JSONObject deviceJson = new JSONObject();
            if (device.getAppUser() == null) {
                deviceJson.put("isBind", false);
            } else {
                deviceJson.put("isBind", true);
            }
            deviceJson.put("id", device.getId());
            deviceJson.put("isOnline", device.isOnline());
            deviceJson.put("barCode", device.getBarCode());
            deviceJson.put("openId", device.getOpenId());
            deviceJson.put("name", device.getDeviceName());
            deviceJson.put("describe", device.getDeviceDescribe());
            deviceJson.put("location", device.getLocation().getLocationDescribe());
            deviceJson.put("lastActiveDate", device.getLastActiveDate());
            data.add(deviceJson);
        }
        pageJson.put("data", data);
        return pageJson;
    }

    public List<Device> findAllDevice() {


        return deviceRepository.findAll();
    }


}
