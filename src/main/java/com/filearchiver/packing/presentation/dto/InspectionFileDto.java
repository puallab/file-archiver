package com.filearchiver.packing.presentation.dto;

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

        @NotNull
        LocalDateTime inspectedAt,

        String sensorId,

        String uploaderEquipment,

        @NotBlank
        @Size(max = 500)
        String filePath
        ) {
    }

    public record RegisterResponse(
        Long fileId
    ) {
    }
}
