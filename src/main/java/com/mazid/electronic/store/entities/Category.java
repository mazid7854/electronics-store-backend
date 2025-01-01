package com.mazid.electronic.store.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @Column(name = "id")
    private String categoryId;

    @Column(name = "category_title",length = 60, nullable = false,unique = true)
    private String title;

    @Column(name = "category_description",length = 500)
    private String description;
    private String coverImage;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "category")
    private List<Product> products= new ArrayList<>();
}
