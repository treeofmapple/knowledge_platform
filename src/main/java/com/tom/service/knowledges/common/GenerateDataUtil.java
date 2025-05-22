package com.tom.service.knowledges.common;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.datafaker.Faker;

@Data
@NoArgsConstructor
public class GenerateDataUtil {

	private Faker faker;
	
	@SuppressWarnings("unused")
	private final AtomicLong atomicCounter = new AtomicLong(1);
	private final ThreadLocalRandom loc = ThreadLocalRandom.current();
	private final Set<String> generatedNames = new HashSet<>();
	
    protected String generateUniqueProductName() {
    	return generate(() -> faker.company().name());
    }

    protected String generateCompanyName() {
        return generate(() -> faker.company().name());
    }
    
    private String generate(Supplier<String> fakerSupplier) {
    	String name;
    	do {
    		name = fakerSupplier.get();
    	} while (generatedNames.contains(name));
    	
        generatedNames.add(name);
        return name;
    	
    }
    
	protected int getRandomNumber(int value) {
		return loc.nextInt(value);
	}
    
	protected int getRandomInt(int min, int max) {
		return loc.nextInt(min, max);
	}

	protected double getRandomDouble(int min) {
		return loc.nextDouble(min);
	}
	
	protected double getRandomDouble(int min, int max) {
		return loc.nextDouble(min, max);
	}
	
	protected double getRandomDouble(double min) {
		return loc.nextDouble(min);
	}
	
	protected double getRandomDouble(double min, double max) {
		return loc.nextDouble(min, max);
	}
	
	protected BigDecimal getRandonBigDecimal(int min) {
		return BigDecimal.valueOf(loc.nextDouble(min));
	}
	
	protected BigDecimal getRandonBigDecimal(int min, int max) {
		return BigDecimal.valueOf(loc.nextDouble(min, max));
	}

	protected BigDecimal getRandonBigDecimal(double min) {
		return BigDecimal.valueOf(loc.nextDouble(min));
	}
	
	protected BigDecimal getRandonBigDecimal(double min, double max) {
		return BigDecimal.valueOf(loc.nextDouble(min, max));
	}
}

