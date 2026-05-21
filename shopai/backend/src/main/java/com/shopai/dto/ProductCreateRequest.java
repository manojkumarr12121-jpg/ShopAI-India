package com.shopai.dto;
import jakarta.validation.constraints.*; import lombok.*;
import java.math.BigDecimal;
@Data @NoArgsConstructor @AllArgsConstructor
public class ProductCreateRequest {
    @NotBlank public String title;
    @NotNull  public Long   categoryId;
    @NotBlank public String brand;
    @NotNull @DecimalMin("0") public BigDecimal price;
    public BigDecimal discountPercent;
    public Integer    stockQty;
    public String     sku;
    public String     imageUrl;
    public String     description;
    public Boolean    isTrending = false;
    public Boolean    isFeatured = false;
}
