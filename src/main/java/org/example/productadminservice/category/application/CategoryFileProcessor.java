package org.example.productadminservice.category.application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.example.productadminservice.category.domain.Category;
import org.example.productadminservice.category.infrastructure.CategoryRepository;
import org.example.productadminservice.common.error.BaseException;
import org.example.productadminservice.common.response.BaseResponseStatus;
import org.example.productadminservice.common.utils.UuidGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryFileProcessor {
	private final CategoryRepository categoryRepository;
	private static final String EXCLUDE_CATEGORY = "ETC";

	public void addCategoryFromFile(MultipartFile file) {
		try (BufferedReader reader = createReader(file)) {
			Map<Integer, String> columnToMainCategoryUuid = processMainCategories(reader);
			processSubCategories(reader, columnToMainCategoryUuid);
		} catch (IOException e) {
			throw new BaseException(BaseResponseStatus.FILE_PROCESSING_ERROR);
		}
	}

	private BufferedReader createReader(MultipartFile file) throws IOException {
		return new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
	}

	private Map<Integer, String> processMainCategories(BufferedReader reader) throws IOException {
		String headerLine = reader.readLine();
		if (headerLine == null || headerLine.trim().isEmpty()) {
			return Map.of();
		}

		Map<Integer, String> columnToMainCategoryUuid = new HashMap<>();
		String[] mainCategories = headerLine.split(",");

		for (int i = 0; i < mainCategories.length; i++) {
			String mainCategoryName = mainCategories[i].trim();
			if (mainCategoryName.isEmpty()) {
				continue;
			}

			String categoryUuid = getOrCreateMainCategory(mainCategoryName);
			columnToMainCategoryUuid.put(i, categoryUuid);
		}

		return columnToMainCategoryUuid;
	}

	private String getOrCreateMainCategory(String categoryName) {
		if (categoryRepository.existsByCategoryName(categoryName)) {
			return categoryRepository.findByCategoryName(categoryName)
				.orElseThrow(() -> new BaseException(BaseResponseStatus.CATEGORY_NOT_FOUND))
				.getCategoryUuid();
		}

		Category mainCategory = Category.builder()
			.categoryName(categoryName)
			.categoryUuid(UuidGenerator.generateCategoryUuid())
			.parentCategoryUuid(null)
			.depth(0)
			.build();

		return categoryRepository.save(mainCategory).getCategoryUuid();
	}

	private void processSubCategories(BufferedReader reader, Map<Integer, String> columnToMainCategoryUuid)
		throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.trim().isEmpty()) {
				continue;
			}

			processSubCategoryLine(line, columnToMainCategoryUuid);
		}
	}

	private void processSubCategoryLine(String line, Map<Integer, String> columnToMainCategoryUuid) {
		String[] columns = line.split(",");
		for (int i = 0; i < columns.length; i++) {
			String subCategoryName = columns[i].trim();

			if (shouldSkipCategory(subCategoryName, columnToMainCategoryUuid.get(i))) {
				continue;
			}

			createSubCategoryIfNotExists(subCategoryName, columnToMainCategoryUuid.get(i));
		}
	}

	private boolean shouldSkipCategory(String categoryName, String parentUuid) {
		return categoryName.isEmpty()
			|| categoryName.equalsIgnoreCase(EXCLUDE_CATEGORY)
			|| parentUuid == null
			|| categoryRepository.existsByCategoryName(categoryName);
	}

	private void createSubCategoryIfNotExists(String categoryName, String parentUuid) {
		Category subCategory = Category.builder()
			.categoryName(categoryName)
			.categoryUuid(UuidGenerator.generateCategoryUuid())
			.parentCategoryUuid(parentUuid)
			.depth(1)
			.build();

		categoryRepository.save(subCategory);
	}
}