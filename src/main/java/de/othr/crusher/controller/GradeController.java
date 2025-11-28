package de.othr.crusher.controller;

import de.othr.crusher.model.GradeEntity;
import de.othr.crusher.model.GymEntity;
import de.othr.crusher.repository.GradeRepository;
import de.othr.crusher.repository.GymRepository;
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

/**
 * Controller for viewing and editing grades within a gym in the admin area.
 */
@Controller
@RequestMapping("/admin/gyms/{gymId}/grades")
public class GradeController {

    private final GradeRepository gradeRepository;
    private final GymRepository gymRepository;

    public GradeController(GradeRepository gradeRepository, GymRepository gymRepository) {
        this.gradeRepository = gradeRepository;
        this.gymRepository = gymRepository;
    }

    /**
     * Displays a single grade.
     *
     * @param gymId identifier of the parent gym
     * @param gradeId identifier of the grade
     * @param model Spring model to pass data to the view
     * @return view name for the grade detail page
     */
    @GetMapping("/{gradeId}")
    public String showGrade(
            @PathVariable("gymId") long gymId,
            @PathVariable("gradeId") long gradeId,
            Model model) {
        GradeEntity grade = findGradeInGymOrThrow(gymId, gradeId);
        model.addAttribute("gym", grade.getGym());
        model.addAttribute("grade", grade);
        return "redirect:/admin/gyms/" + gymId + "/grades/" + gradeId + "/update";
    }

    /**
     * Displays the edit form for an existing grade.
     *
     * @param gymId identifier of the parent gym
     * @param gradeId identifier of the grade
     * @param model Spring model to pass data to the view
     * @return view name for the grade edit page
     */
    @GetMapping("/{gradeId}/update")
    public String showEditForm(
            @PathVariable("gymId") long gymId,
            @PathVariable("gradeId") long gradeId,
            Model model) {
        GradeEntity grade = findGradeInGymOrThrow(gymId, gradeId);
        model.addAttribute("gym", grade.getGym());
        model.addAttribute("grade", grade);
        return "pages/admin/grades/update";
    }

    /**
     * Displays the form for creating a new grade.
     *
     * @param gymId identifier of the parent gym
     * @param model Spring model to pass data to the view
     * @return view name for the grade creation page
     */
    @GetMapping("/create")
    public String showCreateForm(@PathVariable("gymId") long gymId, Model model) {
        GymEntity gym = findGymOrThrow(gymId);
        GradeEntity grade = new GradeEntity();
        grade.setGym(gym);

        model.addAttribute("gym", gym);
        model.addAttribute("grade", grade);
        return "pages/admin/grades/create";
    }

    /**
     * Updates a grade belonging to a gym.
     *
     * @param gymId identifier of the parent gym
     * @param gradeId identifier of the grade
     * @param formGrade grade payload from the form
     * @param result validation result
     * @param model Spring model for rerendering the form if needed
     * @return redirect to the grade detail page or the edit page on validation errors
     */
    @PutMapping("/{gradeId}")
    public String updateGrade(
            @PathVariable("gymId") long gymId,
            @PathVariable("gradeId") long gradeId,
            @Valid @ModelAttribute("grade") GradeEntity formGrade,
            BindingResult result,
            Model model) {
        GradeEntity grade = findGradeInGymOrThrow(gymId, gradeId);

        if (result.hasErrors()) {
            formGrade.setId(grade.getId());
            formGrade.setGym(grade.getGym());
            model.addAttribute("gym", grade.getGym());
            return "pages/admin/grades/update";
        }

        grade.setName(formGrade.getName());
        grade.setDescription(formGrade.getDescription());
        grade.setVScale(formGrade.getVScale());
        grade.setFontScale(formGrade.getFontScale());
        gradeRepository.save(grade);
        return "redirect:/admin/gyms/" + gymId;
    }

    /**
     * Creates a new grade for a gym.
     *
     * @param gymId identifier of the parent gym
     * @param formGrade grade payload from the form
     * @param result validation result
     * @param model Spring model for rerendering the form if needed
     * @return redirect to the new grade detail page or back to the form on validation errors
     */
    @PostMapping
    public String createGrade(
            @PathVariable("gymId") long gymId,
            @Valid @ModelAttribute("grade") GradeEntity formGrade,
            BindingResult result,
            Model model) {
        GymEntity gym = findGymOrThrow(gymId);

        if (result.hasErrors()) {
            model.addAttribute("gym", gym);
            return "pages/admin/grades/create";
        }

        formGrade.setGym(gym);
        gradeRepository.save(formGrade);
        return "redirect:/admin/gyms/" + gymId;
    }

    /**
     * Deletes a grade.
     *
     * @param gymId identifier of the parent gym
     * @param gradeId identifier of the grade
     * @return redirect to the gym detail page
     */
    @DeleteMapping("/{gradeId}")
    public String deleteGrade(@PathVariable("gymId") long gymId, @PathVariable("gradeId") long gradeId) {
        GradeEntity grade = findGradeInGymOrThrow(gymId, gradeId);
        gradeRepository.delete(grade);
        return "redirect:/admin/gyms/" + gymId;
    }

    private GradeEntity findGradeInGymOrThrow(long gymId, long gradeId) {
        GradeEntity grade = gradeRepository
                .findById(gradeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found"));

        if (grade.getGym() == null || grade.getGym().getId() == null
                || !grade.getGym().getId().equals(gymId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade does not belong to gym");
        }
        return grade;
    }

    private GymEntity findGymOrThrow(long gymId) {
        return gymRepository
                .findById(gymId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));
    }
}
