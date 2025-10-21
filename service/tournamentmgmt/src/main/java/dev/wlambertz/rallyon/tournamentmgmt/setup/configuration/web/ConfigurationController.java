package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.web;

import dev.wlambertz.rallyon.iam.keycloak.spring.AuthenticatedPrincipalProvider;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.*;
import dev.wlambertz.rallyon.tournamentmgmt.setup.phases.api.Phase;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.CourtAllocationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.MatchDurationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.ScoringRules;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.SeedingPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.TieBreakRules;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@RestController
@RequestMapping("/api/tournamentmgmt/config")
public class ConfigurationController {

    private final ConfigurationService configurationService;
    private final AuthenticatedPrincipalProvider principalProvider;

    public ConfigurationController(
            ConfigurationService configurationService,
            AuthenticatedPrincipalProvider principalProvider
    ) {
        this.configurationService = configurationService;
        this.principalProvider = principalProvider;
    }

    private long actingUserId() {
        return principalProvider.requirePrincipal()
                .userId()
                .orElseThrow(() -> new IllegalStateException("Token missing numeric rallyon_user_id claim."));
    }

    // Lifecycle
    @PostMapping("/drafts")
    public ResponseEntity<Tournament> createDraft(
            @RequestParam("organizerId") long organizerId,
            @RequestBody CreateDraftRequest request) {
        Tournament created = configurationService.createDraft(
                organizerId, request.name(), request.visibility(), actingUserId()
        );
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{tournamentId}/draft")
    public Tournament updateDraft(
            @PathVariable long tournamentId,
            @RequestBody Tournament draftChanges,
            @RequestHeader("If-Match") long version) {
        return configurationService.updateDraft(tournamentId, draftChanges, version, actingUserId());
    }

    @PostMapping("/{tournamentId}/publish")
    public Tournament publish(
            @PathVariable long tournamentId,
            @RequestHeader("If-Match") long version) {
        return configurationService.publish(tournamentId, version, actingUserId());
    }

    @PostMapping("/{tournamentId}/registration/open")
    public Tournament openRegistration(
            @PathVariable long tournamentId,
            @RequestHeader("If-Match") long version) {
        return configurationService.openRegistration(tournamentId, version, actingUserId());
    }

    @PostMapping("/{tournamentId}/registration/close")
    public Tournament closeRegistration(
            @PathVariable long tournamentId,
            @RequestHeader("If-Match") long version) {
        return configurationService.closeRegistration(tournamentId, version, actingUserId());
    }

    @PostMapping("/{tournamentId}/lock")
    public Tournament lockConfiguration(
            @PathVariable long tournamentId,
            @RequestHeader("If-Match") long version) {
        return configurationService.lockConfiguration(tournamentId, version, actingUserId());
    }

    @PostMapping("/{tournamentId}/start")
    public Tournament start(
            @PathVariable long tournamentId,
            @RequestHeader("If-Match") long version) {
        return configurationService.start(tournamentId, version, actingUserId());
    }

    @PostMapping("/{tournamentId}/complete")
    public Tournament complete(
            @PathVariable long tournamentId,
            @RequestHeader("If-Match") long version) {
        return configurationService.complete(tournamentId, version, actingUserId());
    }

    @PostMapping("/{tournamentId}/cancel")
    public Tournament cancel(
            @PathVariable long tournamentId,
            @RequestParam("reason") String reason,
            @RequestHeader("If-Match") long version) {
        return configurationService.cancel(tournamentId, version, reason, actingUserId());
    }

    // Core configuration setters
    @PutMapping("/{tournamentId}/basics")
    public Tournament setBasics(
            @PathVariable long tournamentId,
            @RequestBody SetBasicsRequest request,
            @RequestHeader("If-Match") long version) {
        return configurationService.setBasics(
                tournamentId,
                request.name(),
                request.description(),
                request.locale(),
                request.visibility(),
                version,
                actingUserId()
        );
    }

    @PutMapping("/{tournamentId}/schedule")
    public Tournament setSchedule(
            @PathVariable long tournamentId,
            @RequestBody SetScheduleRequest request,
            @RequestHeader("If-Match") long version) {
        return configurationService.setSchedule(
                tournamentId,
                request.schedule(),
                request.registrationWindows(),
                version,
                actingUserId()
        );
    }

    @PutMapping("/{tournamentId}/venue")
    public Tournament setVenueAndCourts(
            @PathVariable long tournamentId,
            @RequestBody SetVenueAndCourtsRequest request,
            @RequestHeader("If-Match") long version) {
        return configurationService.setVenueAndCourts(
                tournamentId,
                request.venue(),
                request.courts(),
                version,
                actingUserId()
        );
    }

    @PutMapping("/{tournamentId}/disciplines")
    public Tournament setDisciplines(
            @PathVariable long tournamentId,
            @RequestBody SetDisciplinesRequest request,
            @RequestHeader("If-Match") long version) {
        return configurationService.setDisciplines(
                tournamentId,
                request.disciplines(),
                version,
                actingUserId()
        );
    }

    @PutMapping("/{tournamentId}/capacity")
    public Tournament setCapacity(
            @PathVariable long tournamentId,
            @RequestBody Capacity capacity,
            @RequestHeader("If-Match") long version) {
        return configurationService.setCapacity(tournamentId, capacity, version, actingUserId());
    }

    @PutMapping("/{tournamentId}/policies")
    public Tournament setPolicies(
            @PathVariable long tournamentId,
            @RequestBody SetPoliciesRequest request,
            @RequestHeader("If-Match") long version) {
        return configurationService.setPolicies(
                tournamentId,
                request.registrationPolicy(),
                request.schedulingPolicy(),
                request.courtAllocationPolicy(),
                version,
                actingUserId()
        );
    }

    @PutMapping("/{tournamentId}/rules")
    public Tournament setRules(
            @PathVariable long tournamentId,
            @RequestBody SetRulesRequest request,
            @RequestHeader("If-Match") long version) {
        return configurationService.setRules(
                tournamentId,
                request.scoringRules(),
                request.tieBreakRules(),
                request.matchDurationPolicy(),
                request.seedingPolicy(),
                version,
                actingUserId()
        );
    }

    // Roster
    @PutMapping("/{tournamentId}/participants")
    public Tournament setParticipantsRoster(
            @PathVariable long tournamentId,
            @RequestBody ParticipantsRoster roster,
            @RequestHeader("If-Match") long version) {
        return configurationService.setParticipantsRoster(tournamentId, roster, version, actingUserId());
    }

    @PutMapping("/{tournamentId}/participants/brackets/{bracketId}")
    public Tournament setBracketRoster(
            @PathVariable long tournamentId,
            @PathVariable String bracketId,
            @RequestBody ParticipantsRoster roster,
            @RequestHeader("If-Match") long version) {
        return configurationService.setBracketRoster(
                tournamentId,
                BracketId.of(bracketId),
                roster,
                version,
                actingUserId()
        );
    }

    @PostMapping("/{tournamentId}/participants")
    public Tournament addParticipant(
            @PathVariable long tournamentId,
            @RequestBody AddParticipantRequest request,
            @RequestHeader("If-Match") long version) {
        return configurationService.addParticipant(
                tournamentId,
                request.playerId(),
                request.teamId(),
                request.disciplineId(),
                BracketId.of(request.bracketId()),
                version,
                actingUserId()
        );
    }

    @DeleteMapping("/{tournamentId}/participants")
    public Tournament removeParticipant(
            @PathVariable long tournamentId,
            @RequestBody RemoveParticipantRequest request,
            @RequestHeader("If-Match") long version) {
        return configurationService.removeParticipant(
                tournamentId,
                request.playerId(),
                request.teamId(),
                request.disciplineId(),
                BracketId.of(request.bracketId()),
                version,
                actingUserId()
        );
    }

    // Phases & validation
    @PutMapping("/{tournamentId}/phases")
    public Tournament definePhases(
            @PathVariable long tournamentId,
            @RequestBody List<Phase> phases,
            @RequestHeader("If-Match") long version) {
        return configurationService.definePhases(tournamentId, phases, version, actingUserId());
    }

    @PostMapping("/{tournamentId}/validate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void validateConfiguration(@PathVariable long tournamentId) {
        configurationService.validateConfiguration(tournamentId);
    }

    // Queries
    @GetMapping("/{tournamentId}")
    public Tournament get(@PathVariable long tournamentId) {
        return configurationService.get(tournamentId);
    }

    @GetMapping("/organizers/{organizerId}/tournaments")
    public List<Tournament> listByOrganizer(
            @PathVariable long organizerId,
            @RequestParam(value = "statuses", required = false) Set<TournamentStatus> statuses,
            @RequestParam(value = "visibility", required = false) Visibility visibilityFilter
    ) {
        return configurationService.listByOrganizer(organizerId, statuses, visibilityFilter);
    }

    @GetMapping("/public")
    public List<Tournament> findPublic(
            @RequestParam(value = "q", required = false) String search,
            @RequestParam(value = "locale", required = false) Locale locale,
            @RequestParam(value = "from", required = false) Instant from,
            @RequestParam(value = "to", required = false) Instant to
    ) {
        return configurationService.findPublic(search, locale, from, to);
    }

    // Simple request DTOs to keep the controller lean
    public record CreateDraftRequest(String name, Visibility visibility) {}

    public record SetBasicsRequest(
            String name,
            String description,
            Locale locale,
            Visibility visibility
    ) {}

    public record SetScheduleRequest(
            TimeWindow schedule,
            List<TimeWindow> registrationWindows
    ) {}

    public record SetVenueAndCourtsRequest(
            Venue venue,
            List<Court> courts
    ) {}

    public record SetDisciplinesRequest(
            List<DisciplineConfig> disciplines
    ) {}

    public record SetPoliciesRequest(
            RegistrationPolicy registrationPolicy,
            SchedulingPolicy schedulingPolicy,
            CourtAllocationPolicy courtAllocationPolicy
    ) {}

    public record SetRulesRequest(
            ScoringRules scoringRules,
            TieBreakRules tieBreakRules,
            MatchDurationPolicy matchDurationPolicy,
            SeedingPolicy seedingPolicy
    ) {}

    public record AddParticipantRequest(Long playerId, Long teamId, Long disciplineId, String bracketId) {}

    public record RemoveParticipantRequest(Long playerId, Long teamId, Long disciplineId, String bracketId) {}
}

