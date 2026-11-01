package com.filearchiver.packing.presentation;

import com.filearchiver.packing.presentation.dto.PackagingDto;
import com.filearchiver.packing.application.PackagingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/packagings")
@RequiredArgsConstructor
public class PackagingController {

    private final PackagingService packagingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PackagingDto.PackResponse createPackaging(@Valid @RequestBody PackagingDto.PackRequest request) {
        return packagingService.pack(request);
    }

    @DeleteMapping("/{id}")
    public String cancelPackaging(@PathVariable Long id) {
        return "OK";
    }

    @GetMapping
    public String getPackagingList(Pageable pageable) {
        return "OK";
    }

    @GetMapping("/{id}")
    public String getPackaging(@PathVariable Long id) {
        return "OK";
    }
}
