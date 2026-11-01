package com.filearchiver.packing.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PackagingDto {

    public record PackRequest(
        @NotEmpty
        List<String> serials,

        @NotBlank
        String MaterialCode
    ) {
    }

    public record PackResponse(
        String packagingNo,
        LocalDateTime packagingTime
    ){
    }
}
