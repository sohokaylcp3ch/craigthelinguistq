package com.easylinker.proxy.server.app.service;

import com.alibaba.fastjson.JSONArray;
import com.easylinker.proxy.server.app.dao.DailyLogRepository;
import com.easylinker.proxy.server.app.model.daily.DailyLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * 日志service
 */
@Service("DailyLogService")
public class DailyLogService {
    @Autowired
    DailyLogRepository dailyLogRepository;

    public void save(DailyLog dailyLog) {

        dailyLogRepository.save(dailyLog);
    }

    /**
     * 根据who 查找log
     */

//    public JSONArray getDailyLogsByWho(Long whosId) {
//        Page<DailyLog> page = dailyLogRepository.findAllByWhosId(whosId);
//
//
//    }
}
