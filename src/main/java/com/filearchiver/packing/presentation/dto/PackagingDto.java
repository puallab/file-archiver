package com.filearchiver.packing.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PackagingDto {

    public record CreateRequest(
        @NotEmpty
        List<String> serials
    ) {
    }
}
