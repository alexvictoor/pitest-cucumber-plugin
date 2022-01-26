package org.pitest.cucumber;

import io.cucumber.core.backend.ObjectFactory;
import io.cucumber.core.eventbus.EventBus;
import io.cucumber.core.gherkin.Pickle;
import io.cucumber.core.options.RuntimeOptions;
import io.cucumber.core.runner.Runner;
import io.cucumber.core.runtime.RunnerSupplier;
import io.cucumber.junit.Cucumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pitest.testapi.Description;
import org.pitest.testapi.ResultCollector;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScenarioTestUnitTest {

    @Mock
    Pickle pickle;

    EventBus eventBus = mock(EventBus.class);

    ObjectFactory objectFactory = new ObjectFactory() {
        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public boolean addClass(Class<?> aClass) {
            return false;
        }

        @Override
        public <T> T getInstance(Class<T> aClass) {
            return null;
        }
    };

    Runner runner = new Runner(eventBus, emptyList(), objectFactory, RuntimeOptions.defaultOptions());

    RunnerSupplier runnerSupplier = () -> runner;

    @Mock
    private ResultCollector resultCollector;

    @BeforeEach
    public void setUp() {

        when(pickle.getLanguage()).thenReturn("English");
        when(eventBus.generateId()).thenReturn(UUID.randomUUID());
        when(eventBus.getInstant()).thenReturn(Instant.now());

    }

    @ParameterizedTest
    @ValueSource( classes = { HideFromJUnit.DeprecatedConcombre.class, HideFromJUnit.Concombre.class } )
    public void should_run_scenario_and_call_collector_when_ran(Class<?> clazz) {
        // given
        ScenarioTestUnit testUnit = new ScenarioTestUnit(new Description("", clazz), pickle, runnerSupplier, eventBus);

        // when
        testUnit.execute(resultCollector);

        // then
        verify(eventBus, times(2)).send(any());
        verify(resultCollector, times(1)).notifyStart(any());
    }

    private static class HideFromJUnit {

        @RunWith(Cucumber.class)
        private static class Concombre {
        }

        @RunWith(Cucumber.class)
        private static class DeprecatedConcombre {
        }

    }
}
