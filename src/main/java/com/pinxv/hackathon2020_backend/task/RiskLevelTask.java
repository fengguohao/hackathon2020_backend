package com.pinxv.hackathon2020_backend.task;

import com.pinxv.hackathon2020_backend.dao.HighRiskAreaMapper;
import com.pinxv.hackathon2020_backend.entity.HighRiskArea;
import com.pinxv.hackathon2020_backend.selenium.RiskLevelCrawler;
import com.pinxv.hackathon2020_backend.util.GeographicalPositionUtil;
import com.pinxv.hackathon2020_backend.vo.PositionInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>description: </p>
 *
 * @date 2020/11/28
 */
@Component
public class RiskLevelTask {

    @Autowired
    private final HighRiskAreaMapper highRiskAreaMapper;

    public RiskLevelTask(HighRiskAreaMapper highRiskAreaMapper) {
        this.highRiskAreaMapper = highRiskAreaMapper;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void executeRiskLevelCrawler() throws InterruptedException {
        List<Pair<String, Integer>> riskLevels = RiskLevelCrawler.crawl();
        List<PositionInfoVO> positionInfoVOs = new ArrayList<>();
        for (Pair<String, Integer> riskLevel : riskLevels) {
            positionInfoVOs.add(GeographicalPositionUtil.getPositionInfo(riskLevel.getFirst()));
        }
        for (int i = 0; i < positionInfoVOs.size(); i++) {
            HighRiskArea highRiskArea = new HighRiskArea();
            highRiskArea.setArea(positionInfoVOs.get(i).getPositionName());
            highRiskArea.setAdcode(positionInfoVOs.get(i).getAdcode());
            highRiskArea.setLatitude(positionInfoVOs.get(i).getLatitude());
            highRiskArea.setLongitude(positionInfoVOs.get(i).getLongitude());
            highRiskArea.setRiskLevel(riskLevels.get(i).getSecond());
            this.highRiskAreaMapper.save(highRiskArea);
        }
    }

}
