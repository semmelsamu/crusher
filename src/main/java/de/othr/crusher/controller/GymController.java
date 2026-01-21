package de.othr.crusher.controller;

import de.othr.crusher.model.GymEntity;
import de.othr.crusher.repository.EventRepository;
import de.othr.crusher.repository.GradeRepository;
import de.othr.crusher.repository.GymRepository;
import de.othr.crusher.repository.NoticeRepository;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for managing gyms in the admin area. Provides endpoints for listing, viewing,
 * creating, editing and deleting gyms.
 */
@Controller
@RequestMapping("/admin/gyms")
public class GymController {

  private final GymRepository gymRepository;
  private final GradeRepository gradeRepository;
  private final NoticeRepository noticeRepository;
  private final EventRepository eventRepository;

  /**
   * Creates a new GymController with the given repository.
   *
   * @param gymRepository repository for accessing gym data
   * @param gradeRepository repository for accessing grade data
   * @param noticeRepository repository for accessing notice data
   * @param eventRepository repository for accessing event data
   */
  public GymController(
      GymRepository gymRepository,
      GradeRepository gradeRepository,
      NoticeRepository noticeRepository,
      EventRepository eventRepository) {
    this.gymRepository = gymRepository;
    this.gradeRepository = gradeRepository;
    this.noticeRepository = noticeRepository;
    this.eventRepository = eventRepository;
  }

  /**
   * Displays a list of all gyms.
   *
   * @param model Spring model to pass data to the view
   * @return view name for the gyms overview page
   */
  @GetMapping
  public String showAllGyms(Model model) {
    model.addAttribute("gyms", gymRepository.findByDeletedFalse());
    return "pages/admin/gyms/all";
  }

  /**
   * Displays details for a specific gym based on the given ID.
   *
   * @param id gym ID
   * @param model Spring model to pass data to the view
   * @return view name for the gym detail page
   */
  @GetMapping("/{id}")
  public String showGymForId(@PathVariable("id") long id, Model model) {
    GymEntity gym =
        gymRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));

    model.addAttribute("gym", gym);
    model.addAttribute("grades", gradeRepository.findByGymIdAndDeletedFalse(id));
    model.addAttribute(
        "notices", noticeRepository.findByGymIdAndDeletedFalseOrderByCreationDateDesc(id));
    model.addAttribute("events", eventRepository.findByGymIdAndDeletedFalse(id));

    return "pages/admin/gyms/detail";
  }

  /**
   * Displays the form for creating a new gym.
   *
   * @param model Spring model to pass data to the view
   * @return view name for the gym creation page
   */
  @GetMapping("/create")
  public String showCreateForm(Model model) {
    model.addAttribute("gym", new GymEntity());
    return "pages/admin/gyms/create";
  }

  /**
   * Displays the edit form for an existing gym.
   *
   * @param id gym ID
   * @param model Spring model to pass data to the view
   * @return view name for the gym edit page
   */
  @GetMapping("/{id}/update")
  public String showEditForm(@PathVariable("id") long id, Model model) {
    GymEntity gym = findGymOrThrow(id);
    model.addAttribute("gym", gym);
    return "pages/admin/gyms/update";
  }

  /**
   * Handles the creation of a new gym. Validates input and either redisplays the form with errors
   * or saves the new gym.
   *
   * @param gym gym object submitted from the form
   * @param result validation result
   * @param redirectAttributes attributes for flash messages on redirect
   * @param model Spring model for re-rendering the form if needed
   * @return redirect to the gym list or the form view if errors occur
   */
  @PostMapping
  public String createGym(
      @Valid @ModelAttribute("gym") GymEntity gym,
      BindingResult result,
      RedirectAttributes redirectAttributes,
      Model model) {
    if (result.hasErrors()) {
      return "pages/admin/gyms/create";
    }

    gymRepository.save(gym);

    // Add success message for toast notification
    redirectAttributes.addFlashAttribute(
        "toast",
        Map.of(
            "type", "success",
            "message", "Gym created successfully!"));

    return "redirect:/admin/gyms";
  }

  /**
   * Updates an existing gym. Validates input and either redisplays the form with errors or saves
   * the changes.
   *
   * @param id gym ID
   * @param formGym gym object submitted from the form
   * @param result validation result
   * @param redirectAttributes attributes for flash messages on redirect
   * @param model Spring model for re-rendering the form if needed
   * @return redirect to the gym detail page or the form view if errors occur
   */
  @PutMapping("/{id}")
  public String updateGym(
      @PathVariable("id") long id,
      @Valid @ModelAttribute("gym") GymEntity formGym,
      BindingResult result,
      RedirectAttributes redirectAttributes,
      Model model) {
    GymEntity gym = findGymOrThrow(id);

    if (result.hasErrors()) {
      formGym.setId(gym.getId());
      model.addAttribute("gym", formGym);
      return "pages/admin/gyms/update";
    }

    gym.setName(formGym.getName());
    gym.setStreet(formGym.getStreet());
    gym.setCity(formGym.getCity());
    gym.setEmail(formGym.getEmail());
    gym.setLatitude(formGym.getLatitude());
    gym.setLongitude(formGym.getLongitude());
    gym.setCrowdLevelUrl(formGym.getCrowdLevelUrl());
    gymRepository.save(gym);

    // Add success message for toast notification
    redirectAttributes.addFlashAttribute(
        "toast",
        Map.of(
            "type", "success",
            "message", "Gym updated successfully!"));

    return "redirect:/admin/gyms/" + id;
  }

  /**
   * Soft-deletes a gym by setting its deleted flag.
   *
   * @param id gym ID
   * @param redirectAttributes attributes for flash messages on redirect
   * @return redirect to the gym list
   */
  @DeleteMapping("/{id}")
  public String deleteGym(@PathVariable("id") long id, RedirectAttributes redirectAttributes) {
    GymEntity gym = findGymOrThrow(id);
    gym.setDeleted(true);
    gymRepository.save(gym);

    // Add success message for toast notification
    redirectAttributes.addFlashAttribute(
        "toast",
        Map.of(
            "type", "success",
            "message", "Gym deleted successfully!"));

    return "redirect:/admin/gyms";
  }

  private GymEntity findGymOrThrow(long gymId) {
    return gymRepository
        .findByIdAndDeletedFalse(gymId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));
  }
}
