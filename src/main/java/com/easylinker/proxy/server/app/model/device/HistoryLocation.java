package com.easylinker.proxy.server.app.model.device;

import com.easylinker.proxy.server.app.model.base.BaseEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
/**
 * 历史位置记录
 */
public class HistoryLocation extends BaseEntity {
    //Latitude and longitude:经纬度
    private String latitude;
    private String longitude;
    private String locationDescribe;

    @ManyToOne(targetEntity = Device.class, fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private Device device;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocationDescribe() {
        return locationDescribe;
    }

    public void setLocationDescribe(String locationDescribe) {
        this.locationDescribe = locationDescribe;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
