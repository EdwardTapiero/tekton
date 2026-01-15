package com.tekton.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuración de ejecución asíncrona para el registro de historial de llamadas.
 * Permite que el registro no afecte el rendimiento de las peticiones principales.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${async.executor.core-pool-size:2}")
    private int corePoolSize;

    @Value("${async.executor.max-pool-size:5}")
    private int maxPoolSize;

    @Value("${async.executor.queue-capacity:100}")
    private int queueCapacity;

    @Value("${async.executor.thread-name-prefix:api-history-}")
    private String threadNamePrefix;

    @Bean(name = "apiHistoryExecutor")
    public Executor apiHistoryExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}
