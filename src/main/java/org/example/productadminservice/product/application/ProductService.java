package org.example.productadminservice.product.application;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.opencsv.exceptions.CsvValidationException;

public interface ProductService {

	void addProductFromFile(MultipartFile multipartFile);
}
