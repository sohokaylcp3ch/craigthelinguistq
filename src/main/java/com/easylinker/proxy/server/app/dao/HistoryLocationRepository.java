package com.easylinker.proxy.server.app.dao;

import com.easylinker.proxy.server.app.model.device.HistoryLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryLocationRepository extends JpaRepository<HistoryLocation,Long> {
}
