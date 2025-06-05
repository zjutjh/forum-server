package org.jh.forum.start.utils;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 通过 Nacos 元数据分配 WorkerId
 *
 * @author SugarMGP
 */
@Slf4j
@Component
public class DistributeWorkerId implements ApplicationListener<ApplicationReadyEvent> {
    private static final String METADATA_WORKER_ID = "workerId";
    private static final int MAX_WORKER_ID = 64;

    @Resource
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Resource
    private NacosServiceManager nacosServiceManager;

    private NamingService namingService;

    @Value("${spring.application.name}")
    private String serviceName;

    @Getter
    private short workerId;

    @PostConstruct
    public void init() {
        namingService = nacosServiceManager.getNamingService();
    }

    /**
     * 分配 WorkerId
     *
     * @return 当前实例 WorkerId
     */
    private short allocateWorkerId() throws Exception {
        boolean[] usedWorkerIds = new boolean[MAX_WORKER_ID];
        List<Instance> instances = namingService.getAllInstances(serviceName);
        for (Instance instance : instances) {
            String workerIdStr = instance.getMetadata().get(METADATA_WORKER_ID);
            if (workerIdStr != null) {
                try {
                    short workerId = Short.parseShort(workerIdStr);
                    if (workerId >= 0 && workerId < MAX_WORKER_ID) {
                        usedWorkerIds[workerId] = true;
                    }
                } catch (NumberFormatException ignored) {
                    // 忽略非法 workerId
                }
            }
        }
        for (short i = 0; i < MAX_WORKER_ID; i++) {
            if (!usedWorkerIds[i]) {
                return i;
            }
        }
        throw new RuntimeException("No available workerId found");
    }

    /**
     * 注册 WorkerId
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            this.workerId = allocateWorkerId();

            // 获取当前实例的IP和端口
            String currentIp = nacosDiscoveryProperties.getIp();
            int currentPort = nacosDiscoveryProperties.getPort();

            // 获取当前实例的Instance对象
            Instance currentInstance = namingService.selectInstances(serviceName, true)
                    .stream()
                    .filter(instance -> instance.getIp().equals(currentIp) && instance.getPort() == currentPort)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Current instance not found"));

            // 更新workerId到元数据
            currentInstance.getMetadata().put(METADATA_WORKER_ID, String.valueOf(workerId));

            // 注册更新后的实例
            namingService.registerInstance(serviceName, currentInstance);
        } catch (Exception e) {
            log.error("分配 WorkerId 异常", e);
        }
    }
}