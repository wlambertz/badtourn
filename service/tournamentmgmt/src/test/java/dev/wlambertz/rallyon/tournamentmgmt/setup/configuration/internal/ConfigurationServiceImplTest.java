package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.CreateDraftUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationServiceImplTest {

    @Mock
    private CreateDraftUseCase createDraftUseCase;

    private ConfigurationServiceImpl configurationService;

    @BeforeEach
    void setUp() {
        configurationService = new ConfigurationServiceImpl(createDraftUseCase);
    }

    @Test
    void createDraftDelegatesToUseCase() {
        long organizerId = 10L;
        String name = "RallyOn Invitational";
        Visibility visibility = Visibility.PUBLIC;
        long actingUserId = 99L;

        Tournament expected = Tournament.builder().id(42L).build();
        when(createDraftUseCase.execute(organizerId, name, visibility, actingUserId)).thenReturn(expected);

        Tournament result = configurationService.createDraft(organizerId, name, visibility, actingUserId);

        assertSame(expected, result);
        verify(createDraftUseCase).execute(organizerId, name, visibility, actingUserId);
    }

    @Test
    void createDraftRejectsNullName() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> configurationService.createDraft(1L, null, Visibility.PUBLIC, 2L)
        );

        verifyNoInteractions(createDraftUseCase);
        assertEquals("Tournament name must not be null", exception.getMessage());
    }

    @Test
    void createDraftRejectsNullVisibility() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> configurationService.createDraft(1L, "Name", null, 2L)
        );

        verifyNoInteractions(createDraftUseCase);
        assertEquals("Visibility must not be null", exception.getMessage());
    }
}
