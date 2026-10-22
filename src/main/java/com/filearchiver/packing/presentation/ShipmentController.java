package com.filearchiver.packing.presentation;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {

    // 요청 필드는 docs/api.md 미정
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createShipment() {
        return "OK";
    }

    @DeleteMapping("/{id}")
    public String cancelShipment(@PathVariable Long id) {
        return "OK";
    }

    @GetMapping
    public String getShipmentList(Pageable pageable) {
        return "OK";
    }

    @GetMapping("/{id}")
    public String getShipment(@PathVariable Long id) {
        return "OK";
    }

    @GetMapping("/{id}/progress")
    public String getShipmentProgress(@PathVariable Long id) {
        return "OK";
    }

    @GetMapping("/{id}/download-url")
    public String getShipmentDownloadUrl(@PathVariable Long id) {
        return "OK";
    }
}
