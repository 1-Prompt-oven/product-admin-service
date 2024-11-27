package org.example.productadminservice.product.presentation;

import org.example.productadminservice.common.response.BaseResponse;
import org.example.productadminservice.product.application.ProductService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "상품 관리 API", description = "상품 관련 API endpoints")
@RequestMapping("/v1/admin/product")
public class ProductController {

	private final ProductService productService;

	@Operation(summary = "CSV 파일 기반으로 상품 데이터 생성")
	@PostMapping(value = "/csv", consumes = "multipart/form-data")
	public BaseResponse<Void> addProductFromFile(@RequestPart("file") MultipartFile file) {
		productService.addProductFromFile(file);
		return new BaseResponse<>();
	}
}
