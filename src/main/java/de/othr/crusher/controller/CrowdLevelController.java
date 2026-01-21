package de.othr.crusher.controller;

import de.othr.crusher.model.GymEntity;
import de.othr.crusher.repository.GymRepository;
import de.othr.crusher.service.CrowdLevelService;
import de.othr.crusher.service.CrowdLevelService.CrowdLevel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for asynchronously fetching crowd level data. Used for lazy loading crowd levels
 * on gym detail pages.
 */
@RestController
@RequestMapping("/api/gyms")
public class CrowdLevelController {

  private final GymRepository gymRepository;
  private final CrowdLevelService crowdLevelService;

  public CrowdLevelController(GymRepository gymRepository, CrowdLevelService crowdLevelService) {
    this.gymRepository = gymRepository;
    this.crowdLevelService = crowdLevelService;
  }

  /**
   * Fetches crowd level for a specific gym asynchronously.
   *
   * @param id gym ID
   * @return CrowdLevel data or 404 if not available
   */
  @GetMapping("/{id}/crowd-level")
  public ResponseEntity<CrowdLevel> getCrowdLevel(@PathVariable("id") Long id) {
    GymEntity gym =
        gymRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));

    // Check if gym has crowd level URL configured
    if (gym.getCrowdLevelUrl() == null || gym.getCrowdLevelUrl().isBlank()) {
      return ResponseEntity.notFound().build();
    }

    // Fetch crowd level
    CrowdLevel crowdLevel = crowdLevelService.getCrowdLevel(gym.getCrowdLevelUrl());

    if (crowdLevel == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(crowdLevel);
  }
}
