package de.othr.crusher.controller;

import de.othr.crusher.model.BoulderColor;
import de.othr.crusher.model.BoulderEntity;
import de.othr.crusher.model.GradeEntity;
import de.othr.crusher.model.GymEntity;
import de.othr.crusher.model.SectorEntity;
import de.othr.crusher.repository.BoulderRepository;
import de.othr.crusher.repository.GradeRepository;
import de.othr.crusher.repository.GymRepository;
import de.othr.crusher.repository.SectorRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for managing boulders within a sector in the admin area.
 * <p>
 * Provides basic CRUD operations for boulder problems, including color and grade assignment.
 * </p>
 */
@Controller
@RequestMapping("/admin/gyms/{gymId}/sectors/{sectorId}/boulders")
public class BoulderController {

    private final GymRepository gymRepository;
    private final SectorRepository sectorRepository;
    private final BoulderRepository boulderRepository;
    private final GradeRepository gradeRepository;

    public BoulderController(
            GymRepository gymRepository,
            SectorRepository sectorRepository,
            BoulderRepository boulderRepository,
            GradeRepository gradeRepository) {
        this.gymRepository = gymRepository;
        this.sectorRepository = sectorRepository;
        this.boulderRepository = boulderRepository;
        this.gradeRepository = gradeRepository;
    }

    /**
     * Displays the form for creating a new boulder in a sector.
     *
     * @param gymId identifier of the parent gym
     * @param sectorId identifier of the parent sector
     * @param model Spring model to pass data to the view
     * @return view name for the boulder creation form
     */
    @GetMapping("/new")
    public String showCreateForm(
            @PathVariable("gymId") long gymId,
            @PathVariable("sectorId") long sectorId,
            Model model) {
        GymEntity gym = findGymOrThrow(gymId);
        SectorEntity sector = findSectorInGymOrThrow(gymId, sectorId);
        
        BoulderEntity boulder = new BoulderEntity();
        boulder.setSector(sector);

        List<GradeEntity> availableGrades = gradeRepository.findByGymId(gymId);

        model.addAttribute("gym", gym);
        model.addAttribute("sector", sector);
        model.addAttribute("boulder", boulder);
        model.addAttribute("availableGrades", availableGrades);
        model.addAttribute("availableColors", BoulderColor.values());
        return "pages/admin/boulder-new";
    }

    /**
     * Displays the form for editing an existing boulder.
     *
     * @param gymId identifier of the parent gym
     * @param sectorId identifier of the parent sector
     * @param boulderId identifier of the boulder
     * @param model Spring model to pass data to the view
     * @return view name for the boulder edit form
     */
    @GetMapping("/{boulderId}/edit")
    public String showEditForm(
            @PathVariable("gymId") long gymId,
            @PathVariable("sectorId") long sectorId,
            @PathVariable("boulderId") long boulderId,
            Model model) {
        GymEntity gym = findGymOrThrow(gymId);
        SectorEntity sector = findSectorInGymOrThrow(gymId, sectorId);
        BoulderEntity boulder = findBoulderInSectorOrThrow(sectorId, boulderId);

        List<GradeEntity> availableGrades = gradeRepository.findByGymId(gymId);

        model.addAttribute("gym", gym);
        model.addAttribute("sector", sector);
        model.addAttribute("boulder", boulder);
        model.addAttribute("availableGrades", availableGrades);
        model.addAttribute("availableColors", BoulderColor.values());
        return "pages/admin/boulder-edit";
    }

    /**
     * Creates a new boulder for the given sector. Validates input and either redisplays
     * the form with errors or saves the new boulder.
     *
     * @param gymId identifier of the parent gym
     * @param sectorId identifier of the parent sector
     * @param boulder boulder payload from the form
     * @param result validation result
     * @param model Spring model for re-rendering the form if needed
     * @return redirect to the sector detail page or back to the form on validation errors
     */
    @PostMapping
    public String createBoulder(
            @PathVariable("gymId") long gymId,
            @PathVariable("sectorId") long sectorId,
            @Valid @ModelAttribute("boulder") BoulderEntity boulder,
            BindingResult result,
            Model model) {
        GymEntity gym = findGymOrThrow(gymId);
        SectorEntity sector = findSectorInGymOrThrow(gymId, sectorId);

        if (result.hasErrors()) {
            List<GradeEntity> availableGrades = gradeRepository.findByGymId(gymId);
            model.addAttribute("gym", gym);
            model.addAttribute("sector", sector);
            model.addAttribute("availableGrades", availableGrades);
            model.addAttribute("availableColors", BoulderColor.values());
            return "pages/admin/boulder-new";
        }

        // Validate that the grade belongs to the same gym
        if (boulder.getGrade() != null) {
            GradeEntity grade = gradeRepository.findById(boulder.getGrade().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid grade"));
            if (!grade.getGym().getId().equals(gymId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grade does not belong to this gym");
            }
            boulder.setGrade(grade);
        }

        boulder.setSector(sector);
        boulderRepository.save(boulder);
        return "redirect:/admin/gyms/" + gymId + "/sectors/" + sectorId;
    }

    /**
     * Updates an existing boulder.
     *
     * @param gymId identifier of the parent gym
     * @param sectorId identifier of the parent sector
     * @param boulderId identifier of the boulder
     * @param formBoulder boulder payload from the form
     * @param result validation result
     * @param model Spring model for re-rendering the form if needed
     * @return redirect to the sector detail page or back to edit when validation errors occur
     */
    @PutMapping("/{boulderId}")
    public String updateBoulder(
            @PathVariable("gymId") long gymId,
            @PathVariable("sectorId") long sectorId,
            @PathVariable("boulderId") long boulderId,
            @Valid @ModelAttribute("boulder") BoulderEntity formBoulder,
            BindingResult result,
            Model model) {
        GymEntity gym = findGymOrThrow(gymId);
        SectorEntity sector = findSectorInGymOrThrow(gymId, sectorId);
        BoulderEntity boulder = findBoulderInSectorOrThrow(sectorId, boulderId);

        if (result.hasErrors()) {
            List<GradeEntity> availableGrades = gradeRepository.findByGymId(gymId);
            formBoulder.setId(boulder.getId());
            formBoulder.setSector(sector);
            model.addAttribute("gym", gym);
            model.addAttribute("sector", sector);
            model.addAttribute("boulder", formBoulder);
            model.addAttribute("availableGrades", availableGrades);
            model.addAttribute("availableColors", BoulderColor.values());
            return "pages/admin/boulder-edit";
        }

        // Validate that the grade belongs to the same gym
        if (formBoulder.getGrade() != null) {
            GradeEntity grade = gradeRepository.findById(formBoulder.getGrade().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid grade"));
            if (!grade.getGym().getId().equals(gymId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grade does not belong to this gym");
            }
            boulder.setGrade(grade);
        }

        boulder.setDescription(formBoulder.getDescription());
        boulder.setColor(formBoulder.getColor());
        boulderRepository.save(boulder);
        return "redirect:/admin/gyms/" + gymId + "/sectors/" + sectorId;
    }

    /**
     * Deletes an existing boulder.
     *
     * @param gymId identifier of the parent gym
     * @param sectorId identifier of the parent sector
     * @param boulderId identifier of the boulder
     * @return redirect to the sector detail page
     */
    @DeleteMapping("/{boulderId}")
    public String deleteBoulder(
            @PathVariable("gymId") long gymId,
            @PathVariable("sectorId") long sectorId,
            @PathVariable("boulderId") long boulderId) {
        findGymOrThrow(gymId);
        findSectorInGymOrThrow(gymId, sectorId);
        BoulderEntity boulder = findBoulderInSectorOrThrow(sectorId, boulderId);
        boulderRepository.delete(boulder);
        return "redirect:/admin/gyms/" + gymId + "/sectors/" + sectorId;
    }

    private GymEntity findGymOrThrow(long gymId) {
        return gymRepository
                .findById(gymId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));
    }

    private SectorEntity findSectorInGymOrThrow(long gymId, long sectorId) {
        SectorEntity sector = sectorRepository
                .findById(sectorId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sector not found"));

        if (sector.getGym() == null || sector.getGym().getId() == null
                || !sector.getGym().getId().equals(gymId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sector does not belong to gym");
        }
        return sector;
    }

    private BoulderEntity findBoulderInSectorOrThrow(long sectorId, long boulderId) {
        BoulderEntity boulder = boulderRepository
                .findById(boulderId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Boulder not found"));

        if (boulder.getSector() == null || boulder.getSector().getId() == null
                || !boulder.getSector().getId().equals(sectorId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Boulder does not belong to sector");
        }
        return boulder;
    }
}

