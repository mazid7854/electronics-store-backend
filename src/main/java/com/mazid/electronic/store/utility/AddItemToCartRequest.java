package com.mazid.electronic.store.utility;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddItemToCartRequest {
    private String productId;
    private int quantity;
}
