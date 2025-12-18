package com.apphabit.backend.controllers;

import com.apphabit.backend.models.PlateRequest;
import com.apphabit.backend.models.PlateResponse;
import com.apphabit.backend.services.PlateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plates")
@CrossOrigin(origins = "*")
public class PlateController {

    private final PlateService plateService;

    public PlateController(PlateService plateService) {
        this.plateService = plateService;
    }

    @GetMapping
    public ResponseEntity<List<PlateResponse>> getAllPlates() {
        return ResponseEntity.ok(plateService.getAllPlates());
    }

    @PostMapping
    public ResponseEntity<PlateResponse> createPlate(@RequestBody PlateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(plateService.createPlate(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlate(@PathVariable Long id) {
        plateService.deletePlate(id);
        return ResponseEntity.noContent().build();
    }
}
