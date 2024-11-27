package org.example.productadminservice.product.application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.example.productadminservice.common.error.BaseException;
import org.example.productadminservice.common.response.BaseResponseStatus;
import org.example.productadminservice.common.utils.Encrypter;
import org.example.productadminservice.product.domain.ProductContent;
import org.example.productadminservice.product.dto.in.AddProductRequestDto;
import org.example.productadminservice.product.infrastructure.ProductRepository;
import org.example.productadminservice.product.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final Encrypter encrypter;
	private final ProductMapper productMapper;

	public void addProductFromFile(MultipartFile multipartFile) {
		if (multipartFile.isEmpty()) {
			throw new IllegalArgumentException("File is empty");
		}

		if (!multipartFile.getOriginalFilename().endsWith(".csv")) {
			throw new IllegalArgumentException("File must be CSV format");
		}

		try (Reader reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));
			 CSVReader csvReader = new CSVReader(reader)) {

			String[] headers = csvReader.readNext();
			int lineNumber = 1;
			int baseNumber = 1;  // 기본 번호

			String[] line;
			while ((line = csvReader.readNext()) != null && !isEmptyLine(line)) {
				lineNumber++;
				String baseName = line[4];  // 원본 productName
				String[] baseLine = line.clone();

				// 각 라인당 100개의 데이터 생성
				for (int i = 0; i < 100; i++) {
					try {
						String[] newLine = baseLine.clone();
						// productUuid 수정
						newLine[0] = generateNewProductUuid(baseLine[0], baseNumber + i);
						// productName 수정 (원본 이름 + 번호)
						newLine[4] = String.format("%s_%d", baseName, baseNumber + i);

						AddProductRequestDto productDto = parseProductLine(newLine, lineNumber);
						addProduct(productDto);

						log.debug("Created product {}: {}", baseNumber + i, newLine[4]);
					} catch (Exception e) {
						log.error("Error processing duplicate {} of line {}: {}",
							i + 1, lineNumber, e.getMessage());
					}
				}
				baseNumber += 100;  // 다음 라인을 위해 번호 증가
			}
		} catch (IOException | CsvValidationException e) {
			throw new RuntimeException("Failed to process CSV file: " + e.getMessage(), e);
		}
	}

	private String generateNewProductUuid(String baseUuid, int number) {
		// PR-000001 형식의 UUID를 새로운 번호로 생성
		String prefix = baseUuid.substring(0, baseUuid.lastIndexOf("-") + 1);
		return String.format("%s%06d", prefix, number);
	}

	private boolean isEmptyLine(String[] line) {
		return line == null || line.length == 0 ||
			Arrays.stream(line).allMatch(field -> field == null || field.trim().isEmpty());
	}

	private AddProductRequestDto parseProductLine(String[] line, int lineNumber) {
		validateLineLength(line, lineNumber);

		try {
			List<ProductContent> contents = parseProductContents(line, lineNumber);

			return AddProductRequestDto.builder()
				.productUuid(parseStringField(line[0], "productUuid", lineNumber))
				.sellerUuid(parseStringField(line[1], "sellerUuid", lineNumber))
				.topCategoryUuid(parseStringField(line[2], "topCategoryUuid", lineNumber))
				.subCategoryUuid(parseStringField(line[3], "subCategoryUuid", lineNumber))
				.productName(parseStringField(line[4], "productName", lineNumber))
				.price(parseDoubleField(line[5], "price", lineNumber))
				.prompt(parseStringField(line[6], "prompt", lineNumber))
				.description(parseStringField(line[7], "description", lineNumber))
				.llmId(parseLongField(line[8], "llmId", lineNumber))
				.deleted(false)
				.contents(contents)
				.build();
		} catch (Exception e) {
			StringBuilder errorMsg = new StringBuilder()
				.append("Error parsing line ").append(lineNumber).append(":\n")
				.append("Cause: ").append(e.getMessage()).append("\n")
				.append("Column values:\n");

			String[] columnNames = {"productUuid", "sellerUuid", "topCategoryUuid", "subCategoryUuid",
				"productName", "price", "prompt", "description", "llmId", "deleted",
				"contentUrl1", "contentOrder1", "sampleValue1",
				"contentUrl2", "contentOrder2", "sampleValue2"};

			for (int i = 0; i < Math.min(line.length, columnNames.length); i++) {
				errorMsg.append(columnNames[i]).append(": ").append(line[i]).append("\n");
			}

			throw new IllegalArgumentException(errorMsg.toString());
		}
	}

	private void validateLineLength(String[] line, int lineNumber) {
		int minimumLength = 10; // 기본 필드의 최소 길이
		if (line.length < minimumLength) {
			throw new IllegalArgumentException(
				String.format("Line %d: Invalid number of columns. Expected at least %d, but got %d",
					lineNumber, minimumLength, line.length));
		}
	}

	private List<ProductContent> parseProductContents(String[] line, int lineNumber) {
		List<ProductContent> contents = new ArrayList<>();

		// 첫 번째 content set
		if (line.length > 12 && isValidContentSet(line[10], line[11], line[12])) {
			try {
				ProductContent content1 = ProductContent.builder()
					.contentUrl(line[10].trim())
					.contentOrder(parseInt(line[11], "contentOrder1", lineNumber))
					.sampleValue(line[12].trim())
					.build();
				contents.add(content1);
				log.debug("Added first content: {}", content1);
			} catch (Exception e) {
				log.error("Error parsing first content set at line {}: {}", lineNumber, e.getMessage());
			}
		}

		// 두 번째 content set
		if (line.length > 15 && isValidContentSet(line[13], line[14], line[15])) {
			try {
				ProductContent content2 = ProductContent.builder()
					.contentUrl(line[13].trim())
					.contentOrder(parseInt(line[14], "contentOrder2", lineNumber))
					.sampleValue(line[15].trim())
					.build();
				contents.add(content2);
				log.debug("Added second content: {}", content2);
			} catch (Exception e) {
				log.error("Error parsing second content set at line {}: {}", lineNumber, e.getMessage());
			}
		}

		// contents가 비어있으면 로그 출력
		if (contents.isEmpty()) {
			log.warn("No valid contents found for line {}", lineNumber);
		}

		return contents;
	}

	private boolean isValidContentSet(String url, String order, String sampleValue) {
		return url != null && !url.trim().isEmpty() &&
			order != null && !order.trim().isEmpty() &&
			sampleValue != null && !sampleValue.trim().isEmpty();
	}

	private String parseStringField(String value, String fieldName, int lineNumber) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException(
				String.format("Line %d: %s cannot be empty", lineNumber, fieldName));
		}
		return value.trim();
	}

	private Double parseDoubleField(String value, String fieldName, int lineNumber) {
		try {
			if (value == null || value.trim().isEmpty()) {
				throw new IllegalArgumentException(
					String.format("Line %d: %s cannot be empty", lineNumber, fieldName));
			}
			return Double.parseDouble(value.trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
				String.format("Line %d: Invalid %s format: %s", lineNumber, fieldName, value));
		}
	}

	private Long parseLongField(String value, String fieldName, int lineNumber) {
		try {
			if (value == null || value.trim().isEmpty()) {
				throw new IllegalArgumentException(
					String.format("Line %d: %s cannot be empty", lineNumber, fieldName));
			}
			return Long.parseLong(value.trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
				String.format("Line %d: Invalid %s format: %s", lineNumber, fieldName, value));
		}
	}

	private int parseInt(String value, String fieldName, int lineNumber) {
		try {
			if (value == null || value.trim().isEmpty()) {
				throw new IllegalArgumentException(
					String.format("Line %d: %s cannot be empty", lineNumber, fieldName));
			}
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
				String.format("Line %d: Invalid %s format: %s", lineNumber, fieldName, value));
		}
	}

	private void addProduct(AddProductRequestDto addProductRequestDto) {
		String encryptedPrompt = encrypter.encrypt(addProductRequestDto.getPrompt())
			.orElseThrow(() -> new BaseException(BaseResponseStatus.ENCRYPTION_ERROR));

		if (addProductRequestDto.getContents() == null || addProductRequestDto.getContents().isEmpty()) {
			log.warn("Product {} has no contents", addProductRequestDto.getProductUuid());
		}

		productRepository.save(productMapper.createProduct(addProductRequestDto, encryptedPrompt));
	}


}