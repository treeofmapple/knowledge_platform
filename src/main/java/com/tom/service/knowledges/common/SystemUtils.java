package com.tom.service.knowledges.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SystemUtils {

	public String getUserIp() {
	    var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
	        .getRequest();
	    return request.getRemoteAddr();
	}
	
	public String getPublicIp() throws IOException {
		String publicIp = "Unknown";
		@SuppressWarnings("deprecation")
		URL url = new URL("https://ifconfig.me");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", "curl");

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
			publicIp = reader.readLine();
		}
		return publicIp;
	}
	
	public long parseDuration(String duration) {
	    long millis = 0L;
	    String regex = "(\\d+)([smhdwSMHDW])";
	    var matcher = Pattern.compile(regex).matcher(duration);
	    while (matcher.find()) {
	        long value = Long.parseLong(matcher.group(1));
	        switch (matcher.group(2).toUpperCase()) {
	            case "S" -> millis += value * 1000;
	            case "M" -> millis += value * 60 * 1000;
	            case "H" -> millis += value * 60 * 60 * 1000;
	            case "D" -> millis += value * 24 * 60 * 60 * 1000;
	            case "W" -> millis += value * 7 * 24 * 60 * 60 * 1000;
	            default -> throw new IllegalArgumentException("Invalid time unit in duration: " + matcher.group(2));
	        }
	    }
	    return millis;
	}

}
