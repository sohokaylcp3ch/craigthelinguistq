package com.easylinker.proxy.server.app.service;

import com.easylinker.proxy.server.app.dao.HistoryLocationRepository;
import com.easylinker.proxy.server.app.model.device.HistoryLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("HistoryLocationService")
public class HistoryLocationService {
    @Autowired
    HistoryLocationRepository historyLocationRepository;
    public void save(HistoryLocation historyLocation){
        historyLocationRepository.save(historyLocation);
    }
}
