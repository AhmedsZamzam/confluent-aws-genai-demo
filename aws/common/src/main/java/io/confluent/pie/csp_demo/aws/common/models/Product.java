package io.confluent.pie.csp_demo.aws.common.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Product
 */
@Getter
@Setter
public class Product {
    private String product_id;
    private String description;
}
