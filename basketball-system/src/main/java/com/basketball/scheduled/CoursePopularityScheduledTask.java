package com.basketball.scheduled;

import com.basketball.service.ICoursePopularityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * 课程热度分析定时任务
 *
 * @author Basketball Team
 * @date 2025-10-18
 */
@Slf4j
@Component
public class CoursePopularityScheduledTask {

    @Resource
    private ICoursePopularityService coursePopularityService;

    /**
     * 每天凌晨2:00执行课程热度分析
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void analyzeDailyCoursePopularity() {
        try {
            log.info("开始执行课程热度分析定时任务");

            // 分析昨天的数据
            LocalDate yesterday = LocalDate.now().minusDays(1);
            coursePopularityService.analyzeDailyCoursePopularity(yesterday);

            log.info("课程热度分析定时任务执行完成");
        } catch (Exception e) {
            log.error("课程热度分析定时任务执行失败", e);
        }
    }
}
