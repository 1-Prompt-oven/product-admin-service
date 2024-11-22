package org.example.productadminservice.category.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long categoryId;

	@Column(nullable = false, length = 100)
	private String categoryName;

	@Column(nullable = false, length = 100, unique = true)
	private String categoryUuid;

	private String parentCategoryUuid;

	@Column(nullable = false)
	private int depth;

	@Column(nullable = false)
	private boolean deleted;

}