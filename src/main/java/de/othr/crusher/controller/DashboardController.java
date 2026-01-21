package de.othr.crusher.controller;

import de.othr.crusher.dto.EventOccurrence;
import de.othr.crusher.dto.UserStatistics;
import de.othr.crusher.model.*;
import de.othr.crusher.repository.BoulderCommentRepository;
import de.othr.crusher.service.CrowdLevelService;
import de.othr.crusher.service.CrowdLevelService.CrowdLevel;
import de.othr.crusher.service.StatisticsService;
import de.othr.crusher.service.WeatherService;
import de.othr.crusher.service.WeatherService.WeatherInfo;
import de.othr.crusher.repository.BoulderRatingRepository;
import de.othr.crusher.repository.BoulderRepository;
import de.othr.crusher.repository.EventRepository;
import de.othr.crusher.repository.GoRepository;
import de.othr.crusher.repository.GradeRepository;
import de.othr.crusher.repository.GymCommentRepository;
import de.othr.crusher.repository.GymRatingRepository;
import de.othr.crusher.repository.GymRepository;
import de.othr.crusher.repository.NoticeRepository;
import de.othr.crusher.repository.ProjectRepository;
import de.othr.crusher.repository.SectorRepository;
import de.othr.crusher.repository.SessionRepository;
import de.othr.crusher.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.temporal.TemporalAdjusters;

/**
 * Controller for managing the dashboard and boulder views.
 * Provides endpoints for viewing the dashboard and browsing boulders.
 */
@Controller
public class DashboardController {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final BoulderRepository boulderRepository;
    private final GymRepository gymRepository;
    private final SectorRepository sectorRepository;
    private final GradeRepository gradeRepository;
    private final ProjectRepository projectRepository;
    private final GoRepository goRepository;
    private final BoulderRatingRepository ratingRepository;
    private final BoulderCommentRepository commentRepository;
    private final GymRatingRepository gymRatingRepository;
    private final GymCommentRepository gymCommentRepository;
    private final NoticeRepository noticeRepository;
    private final EventRepository eventRepository;
    private final WeatherService weatherService;
    private final CrowdLevelService crowdLevelService;
    private final StatisticsService statisticsService;

    public DashboardController(
            SessionRepository sessionRepository,
            UserRepository userRepository,
            BoulderRepository boulderRepository,
            GymRepository gymRepository,
            SectorRepository sectorRepository,
            GradeRepository gradeRepository,
            ProjectRepository projectRepository,
            GoRepository goRepository,
            BoulderRatingRepository ratingRepository,
            BoulderCommentRepository commentRepository,
            GymRatingRepository gymRatingRepository,
            GymCommentRepository gymCommentRepository,
            NoticeRepository noticeRepository,
            EventRepository eventRepository,
            WeatherService weatherService,
            CrowdLevelService crowdLevelService,
            StatisticsService statisticsService) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.boulderRepository = boulderRepository;
        this.gymRepository = gymRepository;
        this.sectorRepository = sectorRepository;
        this.gradeRepository = gradeRepository;
        this.projectRepository = projectRepository;
        this.goRepository = goRepository;
        this.ratingRepository = ratingRepository;
        this.commentRepository = commentRepository;
        this.gymRatingRepository = gymRatingRepository;
        this.gymCommentRepository = gymCommentRepository;
        this.noticeRepository = noticeRepository;
        this.eventRepository = eventRepository;
        this.weatherService = weatherService;
        this.crowdLevelService = crowdLevelService;
        this.statisticsService = statisticsService;
    }

    /**
     * Displays the dashboard for the current user.
     *
     * @param principal the authenticated user
     * @param model Spring model to pass data to the view
     * @return view name for the dashboard page
     */
    @GetMapping("/dashboard")
    @Transactional(readOnly = true)
    public String showDashboard(Principal principal, Model model) {
        UserEntity user = findUserByPrincipal(principal);

        // Get sessions for active session and last gym logic
        List<SessionEntity> sessions = sessionRepository.findByUserIdOrderByStartedAtDesc(user.getId());

        // Find the last session (most recent)
        SessionEntity lastSession = sessions.stream()
                .filter(session -> session.getGym() != null)
                .findFirst()
                .orElse(null);

        // Find the most recently used gym
        GymEntity lastGym = lastSession != null ? lastSession.getGym() : null;

        // Find active session (if any)
        SessionEntity activeSession = sessions.stream()
                .filter(session -> session.getEndedAt() == null)
                .findFirst()
                .orElse(null);

        // Get last 3 notices from the last gym
        List<NoticeEntity> lastGymNotices = List.of();
        if (lastGym != null && !lastGym.isDeleted()) {
            List<NoticeEntity> allNotices = noticeRepository.findByGymIdAndDeletedFalseOrderByCreationDateDesc(lastGym.getId());
            lastGymNotices = allNotices.stream()
                    .limit(3)
                    .toList();
        }

        // Get next 3 events from the last gym
        List<EventOccurrence> lastGymEvents = List.of();
        if (lastGym != null && !lastGym.isDeleted()) {
            List<EventEntity> allEvents = eventRepository.findByGymIdAndDeletedFalse(lastGym.getId());
            lastGymEvents = buildUpcomingEvents(allEvents).stream()
                    .limit(3)
                    .toList();
        }

        // Fetch project data
        List<ProjectEntity> projects = projectRepository.findByUserId(user.getId());
        List<ProjectEntity> recentProjects = projects.stream()
                .limit(5)
                .toList();
        long projectCount = projects.size();

        // Fetch recent project activity (last 7 days)
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        long recentProjectAttempts = projects.stream()
                .flatMap(p -> goRepository.findByBoulderIdOrderByTimestampDesc(p.getBoulder().getId()).stream())
                .filter(go -> go.getTimestamp().isAfter(weekAgo))
                .count();

        // Fetch user statistics
        UserStatistics stats = statisticsService.getUserStatistics(user.getId());

        // Calculate success rate
        double successRate = stats.getTotalAttempts() > 0 
                ? (stats.getFinishedCount() * 100.0 / stats.getTotalAttempts()) 
                : 0.0;

        // Fetch session stats
        long sessionCount = sessions.size();
        long lastSessionGoCount = lastSession != null ? goRepository.findBySessionIdOrderByTimestampDesc(lastSession.getId()).size() : 0;

        // Fetch gym-related data
        WeatherInfo lastGymWeather = lastGym != null && !lastGym.isDeleted() ? weatherService.getWeatherForCity(lastGym.getCity()) : null;
        long lastGymBoulderCount = lastGym != null && !lastGym.isDeleted() ? boulderRepository.findBySectorGymIdAndDeletedFalse(lastGym.getId()).size() : 0;
        Integer lastGymUserRating = lastGym != null 
                ? gymRatingRepository.findByUserIdAndGymId(user.getId(), lastGym.getId())
                    .map(r -> r.getRating())
                    .orElse(null) 
                : null;
        Double lastGymAverageRating = lastGym != null
                ? gymRatingRepository.findByGymId(lastGym.getId()).stream()
                    .mapToInt(GymRatingEntity::getRating)
                    .average()
                    .orElse(0.0)
                : null;

        // Add all attributes to model
        model.addAttribute("user", user);
        model.addAttribute("currentDateTime", LocalDateTime.now());
        model.addAttribute("lastGym", lastGym);
        model.addAttribute("lastSession", lastSession);
        model.addAttribute("activeSession", activeSession);
        model.addAttribute("lastGymNotices", lastGymNotices);
        model.addAttribute("lastGymEvents", lastGymEvents);
        
        // Project data
        model.addAttribute("projects", recentProjects);
        model.addAttribute("projectCount", projectCount);
        model.addAttribute("recentProjectAttempts", recentProjectAttempts);
        
        // Statistics data
        model.addAttribute("stats", stats);
        model.addAttribute("successRate", successRate);
        
        // Session data
        model.addAttribute("sessionCount", sessionCount);
        model.addAttribute("lastSessionGoCount", lastSessionGoCount);
        
        // Gym data
        model.addAttribute("lastGymWeather", lastGymWeather);
        model.addAttribute("lastGymBoulderCount", lastGymBoulderCount);
        model.addAttribute("lastGymUserRating", lastGymUserRating);
        model.addAttribute("lastGymAverageRating", lastGymAverageRating);

        return "pages/dashboard";
    }

    /**
     * Displays all boulders with optional filtering by gym, sector, and grades.
     *
     * @param gymId optional gym ID to filter by
     * @param sectorId optional sector ID to filter by
     * @param gradeIds optional list of grade IDs to filter by
     * @param projectOnly whether to show only boulders marked as projects by the current user
     * @param principal the authenticated user
     * @param model Spring model to pass data to the view
     * @return view name for the boulders page
     */
    @GetMapping("/boulders")
    @Transactional(readOnly = true)
    public String showAllBoulders(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "gymId", required = false) Long gymId,
            @RequestParam(value = "sectorId", required = false) Long sectorId,
            @RequestParam(value = "gradeIds", required = false) List<Long> gradeIds,
            @RequestParam(value = "projectOnly", required = false, defaultValue = "false") boolean projectOnly,
            Principal principal,
            Model model) {
        UserEntity user = findUserByPrincipal(principal);

        // Convert 1-based page to 0-based for Spring's PageRequest
        Pageable pageable = PageRequest.of(page - 1, 20);

        // Fetch all gyms for the dropdown
        List<GymEntity> gyms = gymRepository.findByDeletedFalse();

        // Fetch sectors and grades for the selected gym
        List<SectorEntity> sectors = List.of();
        List<GradeEntity> grades = List.of();
        if (gymId != null) {
            sectors = sectorRepository.findByGymIdAndDeletedFalse(gymId);
            grades = gradeRepository.findByGymIdAndDeletedFalse(gymId);
        } else {
            sectors = sectorRepository.findByDeletedFalse();
            grades = gradeRepository.findByDeletedFalse();
        }

        // Load project marks for current user (once) to drive UI and optional filtering
        Set<Long> projectBoulderIds = projectRepository.findByUserId(user.getId()).stream()
                .map(project -> project.getBoulder() != null ? project.getBoulder().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Long> projectBoulderIdList = projectBoulderIds.stream().toList();
        boolean hasGradeFilter = gradeIds != null && !gradeIds.isEmpty();

        // Filter boulders based on selected criteria
        Page<BoulderEntity> bouldersPage;
        if (projectOnly) {
            if (projectBoulderIdList.isEmpty()) {
                bouldersPage = Page.empty(pageable);
            } else if (sectorId != null) {
                if (hasGradeFilter) {
                    bouldersPage = boulderRepository
                            .findBySectorIdAndGradeIdInAndIdInAndDeletedFalse(
                                    sectorId,
                                    gradeIds,
                                    projectBoulderIdList,
                                    pageable);
                } else {
                    bouldersPage = boulderRepository.findBySectorIdAndIdInAndDeletedFalse(
                            sectorId,
                            projectBoulderIdList,
                            pageable);
                }
            } else if (gymId != null) {
                if (hasGradeFilter) {
                    bouldersPage = boulderRepository
                            .findBySectorGymIdAndGradeIdInAndIdInAndDeletedFalse(
                                    gymId,
                                    gradeIds,
                                    projectBoulderIdList,
                                    pageable);
                } else {
                    bouldersPage = boulderRepository.findBySectorGymIdAndIdInAndDeletedFalse(
                            gymId,
                            projectBoulderIdList,
                            pageable);
                }
            } else {
                bouldersPage = boulderRepository.findByIdInAndDeletedFalse(
                        projectBoulderIdList,
                        pageable);
            }
        } else if (sectorId != null) {
            // Filter by specific sector
            if (hasGradeFilter) {
                bouldersPage = boulderRepository.findBySectorIdAndGradeIdInAndDeletedFalse(
                        sectorId,
                        gradeIds,
                        pageable);
            } else {
                bouldersPage = boulderRepository.findBySectorIdAndDeletedFalse(sectorId, pageable);
            }
        } else if (gymId != null) {
            // Filter by gym
            if (hasGradeFilter) {
                bouldersPage = boulderRepository.findBySectorGymIdAndGradeIdInAndDeletedFalse(
                        gymId,
                        gradeIds,
                        pageable);
            } else {
                bouldersPage = boulderRepository.findBySectorGymIdAndDeletedFalse(gymId, pageable);
            }
        } else {
            // No filters - show all boulders
            bouldersPage = boulderRepository.findByDeletedFalse(pageable);
        }

        List<BoulderEntity> boulders = bouldersPage.getContent();

        // Load ratings for current user
        Map<Long, Integer> boulderRatings = ratingRepository.findByUserId(user.getId()).stream()
                .collect(Collectors.toMap(
                        rating -> rating.getBoulder().getId(),
                        rating -> rating.getRating()
                ));

        // Add attributes to model
        model.addAttribute("boulders", boulders);
        model.addAttribute("bouldersPage", bouldersPage);
        model.addAttribute("gyms", gyms);
        model.addAttribute("sectors", sectors);
        model.addAttribute("grades", grades);
        model.addAttribute("selectedGymId", gymId);
        model.addAttribute("selectedSectorId", sectorId);
        model.addAttribute("selectedGradeIds", gradeIds != null ? gradeIds : List.of());
        model.addAttribute("projectBoulderIds", projectBoulderIds);
        model.addAttribute("projectOnly", projectOnly);
        model.addAttribute("boulderRatings", boulderRatings);

        return "pages/boulders/all";
    }

    /**
     * Displays details for a specific boulder.
     *
     * @param id boulder ID
     * @param principal the authenticated user
     * @param model Spring model to pass data to the view
     * @return view name for the boulder detail page
     */
    @GetMapping("/boulders/{id}")
    @Transactional(readOnly = true)
    public String showBoulder(@PathVariable("id") Long id, Principal principal, Model model) {
        UserEntity user = findUserByPrincipal(principal);
        BoulderEntity boulder = boulderRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Boulder not found"));

        // Load project status for the current user
        Set<Long> projectBoulderIds = projectRepository.findByUserId(user.getId()).stream()
                .map(project -> project.getBoulder() != null ? project.getBoulder().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Get all goes for this boulder
        var allGoes = goRepository.findByBoulderIdOrderByTimestampDesc(boulder.getId());

        // Count total tries (all goes)
        long totalTries = allGoes.size();

        // Count total ascents (finished goes) for this boulder
        long ascentsCount = allGoes.stream()
                .filter(go -> go.getResult() != null && go.getResult() == GoResult.FINISHED)
                .count();

        // Get current user's rating for this boulder
        Integer currentRating = ratingRepository.findByUserIdAndBoulderId(user.getId(), boulder.getId())
                .map(rating -> rating.getRating())
                .orElse(0);

        // Calculate average rating for this boulder
        List<BoulderRatingEntity> allRatings = ratingRepository.findByBoulderId(boulder.getId());
        Double averageRating = allRatings.isEmpty() ? null :
                allRatings.stream()
                        .mapToInt(BoulderRatingEntity::getRating)
                        .average()
                        .orElse(0.0);

        // Get all comments for this boulder
        List<BoulderCommentEntity> comments = commentRepository.findByBoulderIdOrderByCreatedAtDesc(boulder.getId());

        model.addAttribute("boulder", boulder);
        model.addAttribute("isProject", projectBoulderIds.contains(boulder.getId()));
        model.addAttribute("ascentsCount", ascentsCount);
        model.addAttribute("totalTries", totalTries);
        model.addAttribute("currentRating", currentRating);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("comments", comments);

        return "pages/boulders/detail";
    }

    /**
     * Displays all gyms with user ratings.
     *
     * @param principal the authenticated user
     * @param model Spring model to pass data to the view
     * @return view name for the gyms page
     */
    @GetMapping("/gyms")
    @Transactional(readOnly = true)
    public String showAllGyms(Principal principal, Model model) {
        UserEntity user = findUserByPrincipal(principal);

        // Fetch all gyms
        List<GymEntity> gyms = gymRepository.findByDeletedFalse();

        // Load ratings for current user
        Map<Long, Integer> gymRatings = gymRatingRepository.findByUserId(user.getId()).stream()
                .collect(Collectors.toMap(
                        rating -> rating.getGym().getId(),
                        rating -> rating.getRating()
                ));

        model.addAttribute("gyms", gyms);
        model.addAttribute("gymRatings", gymRatings);

        return "pages/gyms/all";
    }

    /**
     * Displays details for a specific gym.
     *
     * @param id gym ID
     * @param principal the authenticated user
     * @param model Spring model to pass data to the view
     * @return view name for the gym detail page
     */
    @GetMapping("/gyms/{id}")
    @Transactional(readOnly = true)
    public String showGym(@PathVariable("id") Long id, Principal principal, Model model) {
        UserEntity user = findUserByPrincipal(principal);
        GymEntity gym = gymRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));

        // Get current user's rating for this gym
        Integer currentRating = gymRatingRepository.findByUserIdAndGymId(user.getId(), gym.getId())
                .map(rating -> rating.getRating())
                .orElse(0);

        // Calculate average rating for this gym
        List<GymRatingEntity> allRatings = gymRatingRepository.findByGymId(gym.getId());
        Double averageRating = allRatings.isEmpty() ? null :
                allRatings.stream()
                        .mapToInt(GymRatingEntity::getRating)
                        .average()
                        .orElse(0.0);

        // Get all comments for this gym
        List<GymCommentEntity> comments = gymCommentRepository.findByGymIdOrderByCreatedAtDesc(gym.getId());

        // Get all notices for this gym
        List<NoticeEntity> notices = noticeRepository.findByGymIdAndDeletedFalseOrderByCreationDateDesc(gym.getId());

        // Get upcoming events for this gym
        List<EventOccurrence> events = buildUpcomingEvents(eventRepository.findByGymIdAndDeletedFalse(gym.getId()));

        // Get weather for gym's city
        WeatherInfo weather = weatherService.getWeatherForCity(gym.getCity());

        // Count total boulders in this gym
        long boulderCount = boulderRepository.findBySectorGymIdAndDeletedFalse(gym.getId()).size();

        model.addAttribute("gym", gym);
        model.addAttribute("currentRating", currentRating);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("comments", comments);
        model.addAttribute("notices", notices);
        model.addAttribute("events", events);
        model.addAttribute("weather", weather);
        model.addAttribute("boulderCount", boulderCount);

        return "pages/gyms/detail";
    }

    /**
     * Helper method to find the current user from the security principal.
     *
     * @param principal the authenticated user principal
     * @return the UserEntity for the current user
     * @throws ResponseStatusException if the user is not found
     */
    private UserEntity findUserByPrincipal(Principal principal) {
        return userRepository.findByName(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private List<EventOccurrence> buildUpcomingEvents(List<EventEntity> events) {
        LocalDate today = LocalDate.now();
        return events.stream()
                .map(event -> toOccurrence(event, today))
                .flatMap(Optional::stream)
                .filter(occurrence -> !occurrence.date().isBefore(today))
                .sorted(Comparator.comparing(EventOccurrence::date))
                .toList();
    }

    private Optional<EventOccurrence> toOccurrence(EventEntity event, LocalDate today) {
        if (event.isPeriodic()) {
            LocalDate startDate = resolveRecurringStart(event);
            EventFrequency frequency = event.getFrequency();
            if (startDate == null || frequency == null) {
                return Optional.empty();
            }
            LocalDate nextDate = nextRecurringDate(startDate, frequency, today);
            return Optional.of(new EventOccurrence(event, nextDate));
        }

        LocalDate date = event.getDate();
        if (date == null) {
            return Optional.empty();
        }

        return Optional.of(new EventOccurrence(event, date));
    }

    private LocalDate resolveRecurringStart(EventEntity event) {
        if (event.getCreatedAt() == null) {
            return null;
        }

        LocalDate startDate = event.getCreatedAt().toLocalDate();
        DayOfWeek weekday = event.getWeekday();
        if (weekday != null) {
            startDate = startDate.with(TemporalAdjusters.nextOrSame(weekday));
        }
        return startDate;
    }

    private LocalDate nextRecurringDate(LocalDate startDate, EventFrequency frequency, LocalDate today) {
        if (!startDate.isBefore(today)) {
            return startDate;
        }

        return switch (frequency) {
            case WEEKLY -> advanceByWeeks(startDate, today, 1);
            case BI_WEEKLY -> advanceByWeeks(startDate, today, 2);
            case MONTHLY -> advanceByMonths(startDate, today, 1);
        };
    }

    private LocalDate advanceByWeeks(LocalDate startDate, LocalDate today, int stepWeeks) {
        LocalDate next = startDate;
        while (next.isBefore(today)) {
            next = next.plusWeeks(stepWeeks);
        }
        return next;
    }

    private LocalDate advanceByMonths(LocalDate startDate, LocalDate today, int stepMonths) {
        LocalDate next = startDate;
        while (next.isBefore(today)) {
            next = next.plusMonths(stepMonths);
        }
        return next;
    }
}
