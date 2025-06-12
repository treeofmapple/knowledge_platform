package com.tom.service.knowledges.common;

import org.springframework.stereotype.Component;

import com.tom.service.knowledges.config.AwsProperties;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SystemShutdown {

    private final AwsFunctions awsFunctions;
    private final AwsProperties r2Properties;

    @PreDestroy
    public void onShutdown() {
        String ddlAuto = r2Properties.getDdlAuto();
        
        if ("create-drop".equalsIgnoreCase(ddlAuto)) {
            ServiceLogger.info("R2 ddl-auto is set to 'create-drop'. Clearing R2 bucket: {}", r2Properties.getBucket());
            try {
                var keysToDelete = awsFunctions.listAllObjectKeys();
                
                for (String key : keysToDelete) {
                    awsFunctions.deleteObject(key);
                    ServiceLogger.info("Deleted object from R2: {}", key);
                }
                ServiceLogger.info("Successfully cleared R2 bucket.");
            } catch (Exception e) {
                ServiceLogger.error("Failed to clear R2 bucket on shutdown.", e);
            }
        } else {
            ServiceLogger.info("R2 ddl-auto is not 'create-drop'. Skipping bucket cleanup.");
        }
    }
	
}
