package com.apphabit.backend.services;

import com.apphabit.backend.entities.Plate;
import com.apphabit.backend.entities.User;
import com.apphabit.backend.models.PlateRequest;
import com.apphabit.backend.models.PlateResponse;
import com.apphabit.backend.repositories.PlateRepository;
import com.apphabit.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlateService {

    private final PlateRepository plateRepository;
    private final UserRepository userRepository;

    public PlateService(PlateRepository plateRepository, UserRepository userRepository) {
        this.plateRepository = plateRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<PlateResponse> getAllPlates() {
        User user = getUser();
        return plateRepository.findByUserId(user.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PlateResponse createPlate(PlateRequest request) {
        User user = getUser();

        Plate plate = new Plate(request.getName(), request.getType(), user);
        Plate saved = plateRepository.save(plate);
        return toResponse(saved);
    }

    @Transactional
    public void deletePlate(Long plateId) {
        User user = getUser();
        Plate plate = plateRepository.findById(plateId)
                .orElseThrow(() -> new RuntimeException("Plate not found"));

        if (!plate.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        plateRepository.delete(plate);
    }

    private PlateResponse toResponse(Plate plate) {
        return new PlateResponse(plate.getId(), plate.getName(), plate.getType());
    }

    private User getUser() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        String username = (String) auth.getPrincipal();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
}
