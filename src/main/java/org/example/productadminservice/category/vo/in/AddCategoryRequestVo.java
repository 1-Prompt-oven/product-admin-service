package org.example.productadminservice.category.vo.in;

import org.example.productadminservice.category.dto.in.AddCategoryRequestDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddCategoryRequestVo {
    private String categoryName;
    private String parentCategoryUuid;

    @Builder
    public AddCategoryRequestVo(String categoryName, String parentCategoryUuid) {
        this.categoryName = categoryName;
        this.parentCategoryUuid = parentCategoryUuid;
    }

    public AddCategoryRequestDto toDto(AddCategoryRequestVo addCategoryRequestVo) {
        return AddCategoryRequestDto.builder()
                .categoryName(addCategoryRequestVo.getCategoryName())
                .parentCategoryUuid(addCategoryRequestVo.getParentCategoryUuid())
                .build();
    }
}
