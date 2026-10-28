package com.filearchiver.inspection.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InspectionFileDto {

    public record RegisterRequest(
        @NotBlank
        String serial,

        String sensorId,

        @NotNull
        LocalDateTime inspectedAt,

        @NotBlank
        @Size(max = 500)
        String filePath,

        String uploaderEquipment
    ) {
    }

    public record RegisterResponse(
        Long fileId
    ) {
    }
}
