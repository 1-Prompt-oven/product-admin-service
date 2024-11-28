package org.example.productadminservice.product.application;

import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

	void addProductFromFile(MultipartFile multipartFile);
}
