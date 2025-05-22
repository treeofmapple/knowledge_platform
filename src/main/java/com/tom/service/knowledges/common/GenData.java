package com.tom.service.knowledges.common;

import com.tom.aws.awstest.product.Product;

public class GenData extends GenerateDataUtil {

	public Product genProduct() {
		Product pro = new Product();

        pro.setName(generateUniqueProductName());

		pro.setQuantity(getRandomInt(10, 1000));

		pro.setPrice(getRandonBigDecimal(10, 120));
		
		pro.setManufacturer(generateCompanyName());

		pro.setActive(getRandomNumber(100) < 90); // 90 % chance

		return pro;
	}
	
	
}
