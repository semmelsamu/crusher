package de.othr.crusher.service;

import de.othr.crusher.model.GradeEntity;
import de.othr.crusher.model.GymEntity;
import de.othr.crusher.repository.GradeRepository;
import de.othr.crusher.repository.GymRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service for managing grades within a gym context.
 * <p>
 * Provides operations to retrieve, create, update, and delete grades scoped to a
 * specific gym, ensuring relationships remain consistent.
 * </p>
 */
@Service
public class GradeService {

    private final GradeRepository gradeRepository;
    private final GymRepository gymRepository;

    public GradeService(GradeRepository gradeRepository, GymRepository gymRepository) {
        this.gradeRepository = gradeRepository;
        this.gymRepository = gymRepository;
    }

    /**
     * Returns all grades for the given gym.
     *
     * @param gymId identifier of the gym
     * @return list of grades belonging to the gym
     */
    public List<GradeEntity> findAllForGym(long gymId) {
        assertGymExists(gymId);
        return gradeRepository.findByGymId(gymId);
    }

    /**
     * Returns a single grade ensuring it belongs to the given gym.
     *
     * @param gymId identifier of the gym
     * @param gradeId identifier of the grade
     * @return grade entity
     */
    public GradeEntity findGradeInGym(long gymId, long gradeId) {
        GradeEntity grade = gradeRepository
                .findById(gradeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found"));

        if (grade.getGym() == null || grade.getGym().getId() == null
                || !grade.getGym().getId().equals(gymId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade does not belong to gym");
        }
        return grade;
    }

    /**
     * Creates a new grade within a gym.
     *
     * @param gymId identifier of the gym
     * @param grade grade payload to save
     * @return saved grade
     */
    public GradeEntity createGrade(long gymId, GradeEntity grade) {
        GymEntity gym = findGymOrThrow(gymId);
        grade.setGym(gym);
        return gradeRepository.save(grade);
    }

    /**
     * Updates grade attributes while keeping the gym association intact.
     *
     * @param gymId identifier of the gym
     * @param gradeId identifier of the grade
     * @param formGrade payload containing the new grade values
     * @return updated grade
     */
    public GradeEntity updateGrade(long gymId, long gradeId, GradeEntity formGrade) {
        GradeEntity grade = findGradeInGym(gymId, gradeId);
        grade.setName(formGrade.getName());
        grade.setDescription(formGrade.getDescription());
        grade.setVScale(formGrade.getVScale());
        grade.setFontScale(formGrade.getFontScale());
        return gradeRepository.save(grade);
    }

    /**
     * Deletes a grade belonging to a gym.
     *
     * @param gymId identifier of the gym
     * @param gradeId identifier of the grade
     */
    public void deleteGrade(long gymId, long gradeId) {
        GradeEntity grade = findGradeInGym(gymId, gradeId);
        gradeRepository.delete(grade);
    }

    private GymEntity findGymOrThrow(long gymId) {
        return gymRepository
                .findById(gymId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));
    }

    private void assertGymExists(long gymId) {
        if (!gymRepository.existsById(gymId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found");
        }
    }
}
