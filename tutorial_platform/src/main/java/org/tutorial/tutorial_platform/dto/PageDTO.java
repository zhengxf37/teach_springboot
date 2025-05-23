package org.tutorial.tutorial_platform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PageDTO {
    @Min(value = 1, message = "页码必须≥1")
    private int page = 1;

    @Min(value = 1, message = "每页条数最小为1")
    @Max(value = 100, message = "每页条数最大为100")
    private int size = 10;
}
