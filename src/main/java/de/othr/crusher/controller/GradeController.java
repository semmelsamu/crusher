package de.othr.crusher.controller;

import de.othr.crusher.model.GradeEntity;
import de.othr.crusher.repository.GradeRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

    public GradeController(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
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
        return "pages/admin/grade";
    }

    /**
     * Displays the edit form for an existing grade.
     *
     * @param gymId identifier of the parent gym
     * @param gradeId identifier of the grade
     * @param model Spring model to pass data to the view
     * @return view name for the grade edit page
     */
    @GetMapping("/{gradeId}/edit")
    public String showEditForm(
            @PathVariable("gymId") long gymId,
            @PathVariable("gradeId") long gradeId,
            Model model) {
        GradeEntity grade = findGradeInGymOrThrow(gymId, gradeId);
        model.addAttribute("gym", grade.getGym());
        model.addAttribute("grade", grade);
        return "pages/admin/grade-edit";
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
        if (result.hasErrors()) {
            GradeEntity grade = findGradeInGymOrThrow(gymId, gradeId);
            grade.setName(formGrade.getName());
            grade.setDescription(formGrade.getDescription());
            grade.setVScale(formGrade.getVScale());
            grade.setFontScale(formGrade.getFontScale());

            model.addAttribute("gym", grade.getGym());
            model.addAttribute("grade", grade);
            return "pages/admin/grade-edit";
        }

        GradeEntity grade = findGradeInGymOrThrow(gymId, gradeId);
        grade.setName(formGrade.getName());
        grade.setDescription(formGrade.getDescription());
        grade.setVScale(formGrade.getVScale());
        grade.setFontScale(formGrade.getFontScale());
        gradeRepository.save(grade);
        return "redirect:/admin/gyms/" + gymId + "/grades/" + gradeId;
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
}
