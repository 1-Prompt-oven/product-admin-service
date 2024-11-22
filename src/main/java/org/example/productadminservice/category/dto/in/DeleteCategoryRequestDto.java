package org.example.productadminservice.category.dto.in;

import org.example.productadminservice.category.domain.Category;
import org.example.productadminservice.category.vo.in.DeleteCategoryRequestVo;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeleteCategoryRequestDto {

	private String categoryUuid;

	public DeleteCategoryRequestDto(String categoryUuid) {
		this.categoryUuid = categoryUuid;
	}

	public static Category toEntity(Category category) {
		return Category.builder()
				.categoryId(category.getCategoryId())
				.categoryUuid(category.getCategoryUuid())
				.parentCategoryUuid(category.getParentCategoryUuid())
				.categoryName(category.getCategoryName())
				.depth(category.getDepth())
				.deleted(true)
				.build();
	}

	public static DeleteCategoryRequestDto toDto(DeleteCategoryRequestVo deleteCategoryRequestVo) {
		return new DeleteCategoryRequestDto(deleteCategoryRequestVo.getCategoryUuid());
	}
}
