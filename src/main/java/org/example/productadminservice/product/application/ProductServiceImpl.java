package org.example.productadminservice.product.application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.example.productadminservice.common.error.BaseException;
import org.example.productadminservice.common.response.BaseResponseStatus;
import org.example.productadminservice.common.utils.Encrypter;
import org.example.productadminservice.product.domain.Product;
import org.example.productadminservice.product.domain.ProductContent;
import org.example.productadminservice.product.dto.in.AddProductRequestDto;
import org.example.productadminservice.product.infrastructure.ProductRepository;
import org.example.productadminservice.product.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;

import jakarta.annotation.PreDestroy;
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
	private final Random random = new Random();

	private static final int BATCH_SIZE = 1000;
	private static final int THREAD_POOL_SIZE = 4;
	private static final int MAX_PRODUCTS_PER_BASE = 100;
	private static final int MIN_PRICE = 500;
	private static final int MAX_PRICE = 5000;
	private static final int PRICE_UNIT = 500;
	private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	private int bulkInsertSize = 1000;

	@Override
	public void addProductFromFile(MultipartFile multipartFile) {
		validateFile(multipartFile);

		try (Reader reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));
			 CSVReader csvReader = new CSVReader(reader)) {

			String[] headers = csvReader.readNext();
			List<Future<List<Product>>> futures = new ArrayList<>();
			List<String[]> allLines = new ArrayList<>();

			// 모든 라인을 먼저 읽어옴
			String[] line;
			while ((line = csvReader.readNext()) != null && !isEmptyLine(line)) {
				allLines.add(line);
			}

			// 라인들을 섞어서 처리
			Collections.shuffle(allLines);

			List<String[]> batchLines = new ArrayList<>();
			int baseNumber = 1;

			for (String[] currentLine : allLines) {
				batchLines.add(currentLine);

				if (batchLines.size() >= BATCH_SIZE) {
					futures.add(processBatch(batchLines, baseNumber));
					baseNumber += (batchLines.size() * MAX_PRODUCTS_PER_BASE);
					batchLines = new ArrayList<>();
				}
			}

			if (!batchLines.isEmpty()) {
				futures.add(processBatch(batchLines, baseNumber));
			}

			saveAllProducts(futures);

		} catch (Exception e) {
			log.error("Failed to process CSV file", e);
			throw new RuntimeException("Failed to process CSV file: " + e.getMessage(), e);
		}
	}

	private Future<List<Product>> processBatch(List<String[]> lines, int startingBaseNumber) {
		return executorService.submit(() -> {
			List<Product> products = new ArrayList<>();
			int currentBaseNumber = startingBaseNumber;

			for (String[] baseLine : lines) {
				String baseName = baseLine[4];
				List<String> prompts = new ArrayList<>();
				List<AddProductRequestDto> dtos = new ArrayList<>();

				int productsToCreate = 50 + random.nextInt(51);

				for (int i = 0; i < productsToCreate; i++) {
					String[] newLine = baseLine.clone();
					newLine[0] = generateNewProductUuid(baseLine[0], currentBaseNumber + i);
					newLine[4] = generateVariantProductName(baseName, currentBaseNumber + i);
					newLine[5] = String.valueOf(generateRandomPrice());

					AddProductRequestDto dto = parseProductLine(newLine, currentBaseNumber + i);
					dtos.add(dto);
					prompts.add(dto.getPrompt());
				}

				List<String> encryptedPrompts = encryptPromptsBatch(prompts);

				for (int i = 0; i < dtos.size(); i++) {
					products.add(productMapper.createProduct(dtos.get(i), encryptedPrompts.get(i)));
				}

				currentBaseNumber += MAX_PRODUCTS_PER_BASE;
			}

			Collections.shuffle(products);
			return products;
		});
	}

	private String generateVariantProductName(String baseName, int number) {
		String[] purposes = {
			"Beginner", "Professional", "Advanced", "Basic", "Universal",
			"Optimized", "Custom", "Template", "Enhanced", "Standard"
		};

		String[] characteristics = {
			"Detailed", "Creative", "Practical", "Efficient", "Intuitive",
			"Expandable", "Integrated", "Structured", "Premium", "Quality"
		};

		String[] tasks = {
			"Story Generation", "Content Creation", "Analysis", "Translation",
			"Summarization", "Writing", "Research", "Conversation",
			"Description", "Documentation"
		};

		String purpose = purposes[random.nextInt(purposes.length)];
		String characteristic = characteristics[random.nextInt(characteristics.length)];

		StringBuilder nameBuilder = new StringBuilder(baseName.trim());
		nameBuilder.append(" - ")
			.append(purpose)
			.append(" ")
			.append(characteristic);

		if (random.nextDouble() < 0.3) {
			String task = tasks[random.nextInt(tasks.length)];
			nameBuilder.append(" for ").append(task);
		}

		return nameBuilder.toString();
	}

	private double generateRandomPrice() {
		int priceSteps = (MAX_PRICE - MIN_PRICE) / PRICE_UNIT;
		int randomStep = random.nextInt(priceSteps + 1);
		return MIN_PRICE + (randomStep * PRICE_UNIT);
	}

	private void validateFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw new IllegalArgumentException("File is empty");
		}
		if (!file.getOriginalFilename().endsWith(".csv")) {
			throw new IllegalArgumentException("File must be CSV format");
		}
	}

	private void saveAllProducts(List<Future<List<Product>>> futures) {
		try {
			List<Product> allProducts = new ArrayList<>();

			for (Future<List<Product>> future : futures) {
				allProducts.addAll(future.get());

				if (allProducts.size() >= bulkInsertSize) {
					productRepository.saveAll(allProducts);
					allProducts.clear();
				}
			}

			if (!allProducts.isEmpty()) {
				productRepository.saveAll(allProducts);
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to save products", e);
		}
	}

	@PreDestroy
	public void cleanup() {
		executorService.shutdown();
		try {
			if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
				executorService.shutdownNow();
			}
		} catch (InterruptedException e) {
			executorService.shutdownNow();
		}
	}

	private String generateNewProductUuid(String baseUuid, int number) {
		String prefix = baseUuid.substring(0, baseUuid.lastIndexOf("-") + 1);
		return String.format("%s%06d", prefix, number);
	}

	private boolean isEmptyLine(String[] line) {
		return line == null || line.length == 0 ||
			Arrays.stream(line).allMatch(field -> field == null || field.trim().isEmpty());
	}

	private List<String> encryptPromptsBatch(List<String> prompts) {
		return prompts.parallelStream()
			.map(prompt -> encrypter.encrypt(prompt)
				.orElseThrow(() -> new BaseException(BaseResponseStatus.ENCRYPTION_ERROR)))
			.collect(Collectors.toList());
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
				.temporaryEnrolled(false)
				.sells(0L)
				.likeCount(0L)
				.contents(contents)
				.build();
		} catch (Exception e) {
			StringBuilder errorMsg = new StringBuilder()
				.append("Error parsing line ").append(lineNumber).append(":\n")
				.append("Cause: ").append(e.getMessage()).append("\n")
				.append("Column values:\n");

			String[] columnNames = {
				"productUuid", "sellerUuid", "topCategoryUuid", "subCategoryUuid",
				"productName", "price", "prompt", "description", "llmId", "deleted",
				"contentUrl1", "contentOrder1", "sampleValue1",
				"contentUrl2", "contentOrder2", "sampleValue2"
			};

			for (int i = 0; i < Math.min(line.length, columnNames.length); i++) {
				errorMsg.append(columnNames[i]).append(": ").append(line[i]).append("\n");
			}

			throw new IllegalArgumentException(errorMsg.toString());
		}
	}

	private void validateLineLength(String[] line, int lineNumber) {
		int minimumLength = 10;
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
}