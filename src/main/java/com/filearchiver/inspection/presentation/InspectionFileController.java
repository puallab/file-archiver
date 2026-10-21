package com.filearchiver.inspection.presentation;

import com.filearchiver.inspection.presentation.dto.InspectionFileDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inspection-files")
public class InspectionFileController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String register(@Valid @RequestBody InspectionFileDto.RegisterRequest request) {
        return "OK";
    }
}
