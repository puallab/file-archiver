package com.filearchiver.packing.presentation;

import com.filearchiver.packing.presentation.dto.PackagingDto;
import jakarta.validation.Valid;
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
public class PackagingController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createPackaging(@Valid @RequestBody PackagingDto.CreateRequest request) {
        return "OK";
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
