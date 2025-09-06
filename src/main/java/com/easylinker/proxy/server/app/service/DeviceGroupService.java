package com.easylinker.proxy.server.app.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easylinker.proxy.server.app.dao.DeviceGroupRepository;
import com.easylinker.proxy.server.app.dao.DeviceRepository;
import com.easylinker.proxy.server.app.model.device.Device;
import com.easylinker.proxy.server.app.model.device.DeviceGroup;
import com.easylinker.proxy.server.app.model.user.AppUser;
import com.sun.javafx.geom.transform.BaseTransform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备组service
 */
@Service("DeviceGroupService")
public class DeviceGroupService {
    @Autowired
    DeviceGroupRepository deviceGroupRepository;

    @Autowired
    DeviceRepository deviceRepository;

    public void save(DeviceGroup deviceGroup) {
        deviceGroupRepository.save(deviceGroup);
    }

    public JSONObject getADeviceGroupByName(String name) {
        DeviceGroup deviceGroup = deviceGroupRepository.findTopByGroupName(name);
        if (deviceGroup != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", deviceGroup.getGroupName());
            jsonObject.put("comment", deviceGroup.getComment());
            return jsonObject;
        } else {
            return null;
        }

    }


    public JSONArray getAllByAppUser(AppUser appUser) {
        JSONArray data = new JSONArray();
        List<DeviceGroup> deviceGroupList = deviceGroupRepository.findAllByAppUser(appUser);
        for (DeviceGroup group : deviceGroupList) {
            JSONObject dataJson = new JSONObject();
            dataJson.put("id", group.getId());
            dataJson.put("name", group.getGroupName());
            dataJson.put("comment", group.getComment());
            dataJson.put("user", group.getAppUser().getId());
            data.add(dataJson);
        }
        return data;
    }


    public void delete(DeviceGroup deviceGroup) {
        deviceGroupRepository.delete(deviceGroup);
    }


    public DeviceGroup findADeviceGroupById(Long id) {
        return deviceGroupRepository.findTopById(id);
    }


    public DeviceGroup findADeviceGroupByName(String groupName) {
        return deviceGroupRepository.findTopByGroupName(groupName);
    }

    /**
     * 分页获取分组
     *
     * @param pageable
     * @return
     */
    public JSONArray getAllDeviceGroupByPage(AppUser appUser, Pageable pageable) {
        JSONArray data = new JSONArray();
        Page<DeviceGroup> page = deviceGroupRepository.findAllByAppUser(appUser, pageable);
        for (DeviceGroup group : page.getContent()) {
            JSONObject dataJson = new JSONObject();
            dataJson.put("name", group.getGroupName());
            dataJson.put("user", group.getAppUser().getId());
            dataJson.put("comment", group.getComment());
            dataJson.put("id", group.getId());
            data.add(dataJson);
        }
        return data;
    }

    /**
     * 分页获取所有分组
     *
     * @param pageable
     * @return
     */

    public JSONArray getAllDeviceGroupByPage(Pageable pageable) {
        JSONArray data = new JSONArray();
        Page<DeviceGroup> page = deviceGroupRepository.findAll(pageable);
        for (DeviceGroup group : page.getContent()) {
            JSONObject dataJson = new JSONObject();
            dataJson.put("id", group.getId());
            dataJson.put("user", group.getAppUser().getId());
            dataJson.put("name", group.getGroupName());
            dataJson.put("comment", group.getComment());
            data.add(dataJson);
        }
        return data;
    }
}
