package com.mazid.electronic.store.dataTransferObjects;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {

    private String categoryId;

    @NotBlank(message = "Title can not be blank!")
    @Size(min = 4,message = "Title must be at least 4 characters!")
    private String title;

    @NotBlank(message = "Description can not be blank!")
    private String description;

    private String coverImage;
}
