package com.example.samuraitravel.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewForm {

    @NotNull(message = "評価を選択してください")
    @Min(1)
    @Max(5)
    private Integer rating;

    @NotBlank(message = "コメントを入力してください")
    @Size(max = 500)
    private String comment;
}
