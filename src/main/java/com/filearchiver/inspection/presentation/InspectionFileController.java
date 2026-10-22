package com.filearchiver.inspection.presentation;

import com.filearchiver.inspection.presentation.dto.InspectionFileDto;
import com.filearchiver.inspection.service.InspectionFileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inspection-files")
@RequiredArgsConstructor
public class InspectionFileController {
    private final InspectionFileService inspectionFileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InspectionFileDto.RegisterResponse register(@Valid @RequestBody InspectionFileDto.RegisterRequest request) {
        Long fileId = inspectionFileService.register(request);
        return new InspectionFileDto.RegisterResponse(fileId);
    }
}
