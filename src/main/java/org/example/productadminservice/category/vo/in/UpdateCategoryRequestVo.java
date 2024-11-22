package org.example.productadminservice.category.vo.in;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCategoryRequestVo {

	private String categoryUuid;
	private String parentCategoryUuid;
	private String categoryName;

	@Builder
	public UpdateCategoryRequestVo(String categoryUuid, String parentCategoryUuid, String categoryName) {
		this.categoryUuid = categoryUuid;
		this.parentCategoryUuid = parentCategoryUuid;
		this.categoryName = categoryName;
	}
}
