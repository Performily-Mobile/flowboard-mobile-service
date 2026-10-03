package com.performily.flowboard.wellbeing.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOfficeResource(
        @NotBlank @Size(max = 80) String name,
        @Size(max = 60) String area,
        @NotBlank @Size(max = 150) String address,
        @NotBlank @Size(max = 30) String floor,
        @Size(max = 150) String reference
) {}
