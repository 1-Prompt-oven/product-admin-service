package org.example.productadminservice.category.application;

import org.example.productadminservice.category.dto.in.AddCategoryRequestDto;
import org.example.productadminservice.category.dto.in.DeleteCategoryRequestDto;
import org.example.productadminservice.category.dto.in.UpdateCategoryRequestDto;

public interface CategoryService {
    void addCategory(AddCategoryRequestDto addCategoryRequestDto);
    void updateCategory(UpdateCategoryRequestDto updateCategoryRequestDto);
    void deleteCategory(DeleteCategoryRequestDto deleteCategoryRequestDto);

}