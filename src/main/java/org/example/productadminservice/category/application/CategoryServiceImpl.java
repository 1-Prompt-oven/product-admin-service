package org.example.productadminservice.category.application;

import org.example.productadminservice.category.domain.Category;
import org.example.productadminservice.category.dto.in.AddCategoryRequestDto;
import org.example.productadminservice.category.dto.in.DeleteCategoryRequestDto;
import org.example.productadminservice.category.dto.in.UpdateCategoryRequestDto;
import org.example.productadminservice.category.infrastructure.CategoryRepository;
import org.example.productadminservice.common.error.BaseException;
import org.example.productadminservice.common.response.BaseResponseStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;


    @Override
    public void addCategory(AddCategoryRequestDto addCategoryRequestDto) {

        if (addCategoryRequestDto.getParentCategoryUuid() == null ||
            addCategoryRequestDto.getParentCategoryUuid().isEmpty()) {

            categoryRepository.save(addCategoryRequestDto.createRootCategory());
            return;
        }

        Category parentCategory = categoryRepository.findByCategoryUuid(
                addCategoryRequestDto.getParentCategoryUuid())
            .orElseThrow(() -> new BaseException(BaseResponseStatus.CATEGORY_NOT_FOUND));

        categoryRepository.save(addCategoryRequestDto.createChildCategory(parentCategory));
	}

    @Override
    public void updateCategory(UpdateCategoryRequestDto updateCategoryRequestDto) {
        Long categoryId = categoryRepository.findByCategoryUuid(updateCategoryRequestDto.getCategoryUuid())
            .orElseThrow(() -> new BaseException(BaseResponseStatus.CATEGORY_NOT_FOUND))
            .getCategoryId();

        categoryRepository.save(updateCategoryRequestDto.toEntity(categoryId));
    }

    @Override
    public void deleteCategory(DeleteCategoryRequestDto deleteCategoryRequestDto) {
        Category category = categoryRepository.findByCategoryUuid(deleteCategoryRequestDto.getCategoryUuid())
            .orElseThrow(() -> new BaseException(BaseResponseStatus.CATEGORY_NOT_FOUND));

        categoryRepository.save(DeleteCategoryRequestDto.toEntity(category));

        //TODO: Delete all products under this category
    }
}
