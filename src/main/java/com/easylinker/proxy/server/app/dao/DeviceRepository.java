package com.easylinker.proxy.server.app.dao;

import com.easylinker.proxy.server.app.model.device.Device;
import com.easylinker.proxy.server.app.model.device.DeviceGroup;
import com.easylinker.proxy.server.app.model.user.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Device findTopById(Long id);

    Device findTopByOpenId(Long openId);


    List<Device> findAllByAppUser(AppUser appUser);

    Page<Device> findAllByAppUser(AppUser appUser, Pageable pageable);

    Page<Device> findAllByAppUserAndDeviceGroup(AppUser appUser, DeviceGroup deviceGroup, Pageable pageable);

    List<Device> findAllByAppUserAndIsOnline(AppUser appUser, Boolean online);

    List<Device> findAllByIsOnline(Boolean online);

    @Query(
            "select id from Device "
    )
    List <Long>findAllId();


}
