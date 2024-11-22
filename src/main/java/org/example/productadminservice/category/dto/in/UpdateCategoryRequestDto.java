package org.example.productadminservice.category.dto.in;

import org.example.productadminservice.category.domain.Category;
import org.example.productadminservice.category.vo.in.UpdateCategoryRequestVo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UpdateCategoryRequestDto {

	private String categoryUuid;
	private String parentCategoryUuid;
	private String categoryName;

	@Builder
	public UpdateCategoryRequestDto(String categoryUuid, String parentCategoryUuid, String categoryName) {
		this.categoryUuid = categoryUuid;
		this.parentCategoryUuid = parentCategoryUuid;
		this.categoryName = categoryName;
	}

	public Category toEntity(Long categoryId) {
		return Category.builder()
				.categoryId(categoryId)
				.categoryUuid(categoryUuid)
				.parentCategoryUuid(parentCategoryUuid)
				.categoryName(categoryName)
				.deleted(false)
				.build();
	}

	public static UpdateCategoryRequestDto toDto(UpdateCategoryRequestVo vo) {
		return UpdateCategoryRequestDto.builder()
				.categoryUuid(vo.getCategoryUuid())
				.parentCategoryUuid(vo.getParentCategoryUuid())
				.categoryName(vo.getCategoryName())
				.build();
	}
}
