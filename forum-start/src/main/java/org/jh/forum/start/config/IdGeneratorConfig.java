package org.jh.forum.start.config;

import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import org.jh.forum.start.utils.DistributeWorkerId;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * @author SugarMGP
 */
@Configuration
public class IdGeneratorConfig {

    @Resource
    private DistributeWorkerId distributeWorkerId;

    @PostConstruct
    public void init() {
        IdGeneratorOptions options = new IdGeneratorOptions(distributeWorkerId.getWorkerId());
        // 使用 2025年6月1日 00:00:00 为开始时间
        options.BaseTime = 1748707200000L;
        YitIdHelper.setIdGenerator(options);
    }
}