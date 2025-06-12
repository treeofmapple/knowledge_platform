package com.tom.service.knowledges.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
public class WhitelistLoader {

    public String[] loadWhitelist() throws IOException {
        List<String> paths = new ArrayList<>();
        var resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:/whitelist/*.txt");

        for (Resource resource : resources) {
            if (resource.getFilename() != null && resource.getFilename().equals("csp-directives.txt")) {
                continue;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                paths.addAll(reader.lines()
                        .map(String::trim)
                        .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                        .collect(Collectors.toList()));
            }
        }

        return paths.toArray(new String[0]);
    }
    
    public String loadCspDirectives() throws IOException {
        var resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource("classpath:/whitelist/csp-directives.txt");

        if (!resource.exists()) {
            return "script-src 'self'; style-src 'self'; font-src 'self';";
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.lines()
                         .map(String::trim)
                         .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                         .collect(Collectors.joining(" "));
        }
    }
}