/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.solver.termination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.solver.termination.TerminationCompositionStyle;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;

class TerminationFactoryTest {

    @Test
    void spentLimit() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setSpentLimit(Duration.ofMillis(5L)
                .plusSeconds(4L)
                .plusMinutes(3L)
                .plusHours(2L)
                .plusDays(1L));
        Termination termination =
                TerminationFactory.create(terminationConfig).buildTermination(mock(HeuristicConfigPolicy.class));
        assertThat(termination)
                .isInstanceOf(TimeMillisSpentTermination.class);
        assertThat(((TimeMillisSpentTermination) termination).getTimeMillisSpentLimit()).isEqualTo(93784005L);
    }

    @Test
    void spentLimitWithoutJavaTime() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setMillisecondsSpentLimit(5L);
        terminationConfig.setSecondsSpentLimit(4L);
        terminationConfig.setMinutesSpentLimit(3L);
        terminationConfig.setHoursSpentLimit(2L);
        terminationConfig.setDaysSpentLimit(1L);
        Termination termination =
                TerminationFactory.create(terminationConfig).buildTermination(mock(HeuristicConfigPolicy.class));
        assertThat(termination)
                .isInstanceOf(TimeMillisSpentTermination.class);
        assertThat(((TimeMillisSpentTermination) termination).getTimeMillisSpentLimit()).isEqualTo(93784005L);
    }

    @Test
    void unimprovedSpentLimit() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setUnimprovedSpentLimit(Duration.ofMillis(5L)
                .plusSeconds(4L)
                .plusMinutes(3L)
                .plusHours(2L)
                .plusDays(1L));
        Termination termination =
                TerminationFactory.create(terminationConfig).buildTermination(mock(HeuristicConfigPolicy.class));
        assertThat(termination)
                .isInstanceOf(UnimprovedTimeMillisSpentTermination.class);
        assertThat(((UnimprovedTimeMillisSpentTermination) termination).getUnimprovedTimeMillisSpentLimit())
                .isEqualTo(93784005L);
    }

    @Test
    void unimprovedSpentLimitWithoutJavaTime() {
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setUnimprovedMillisecondsSpentLimit(5L);
        terminationConfig.setUnimprovedSecondsSpentLimit(4L);
        terminationConfig.setUnimprovedMinutesSpentLimit(3L);
        terminationConfig.setUnimprovedHoursSpentLimit(2L);
        terminationConfig.setUnimprovedDaysSpentLimit(1L);
        Termination termination =
                TerminationFactory.create(terminationConfig).buildTermination(mock(HeuristicConfigPolicy.class));
        assertThat(termination)
                .isInstanceOf(UnimprovedTimeMillisSpentTermination.class);
        assertThat(((UnimprovedTimeMillisSpentTermination) termination).getUnimprovedTimeMillisSpentLimit())
                .isEqualTo(93784005L);
    }

    @Test
    void buildTerminationFromList_single() {
        Termination termination = new TimeMillisSpentTermination(1000L);
        assertThat(TerminationFactory.create(new TerminationConfig())
                .buildTerminationFromList(Collections.singletonList(termination))).isSameAs(termination);
    }

    @Test
    void buildTerminationFromList_withAndComposition() {
        Termination timeBasedTermination = new TimeMillisSpentTermination(1000L);
        Termination stepCountTermination = new StepCountTermination(5);
        List<Termination> terminationList = new ArrayList<>();
        terminationList.add(timeBasedTermination);
        terminationList.add(stepCountTermination);

        Termination resultingTermination = TerminationFactory.create(new TerminationConfig().withTerminationCompositionStyle(
                TerminationCompositionStyle.AND))
                .buildTerminationFromList(terminationList);
        assertThat(resultingTermination).isExactlyInstanceOf(AndCompositeTermination.class);
        AndCompositeTermination andCompositeTermination = (AndCompositeTermination) resultingTermination;
        assertThat(andCompositeTermination.terminationList).containsExactly(timeBasedTermination, stepCountTermination);
    }

    @Test
    void buildTerminationFromList_withDefaultComposition() {
        Termination timeBasedTermination = new TimeMillisSpentTermination(1000L);
        Termination stepCountTermination = new StepCountTermination(5);
        List<Termination> terminationList = new ArrayList<>();
        terminationList.add(timeBasedTermination);
        terminationList.add(stepCountTermination);

        Termination resultingTermination = TerminationFactory.create(new TerminationConfig())
                .buildTerminationFromList(terminationList);
        assertThat(resultingTermination).isExactlyInstanceOf(OrCompositeTermination.class);
        OrCompositeTermination andCompositeTermination = (OrCompositeTermination) resultingTermination;
        assertThat(andCompositeTermination.terminationList).containsExactly(timeBasedTermination, stepCountTermination);
    }

    @Test
    void buildInnerTermination() {
        TerminationConfig innerTerminationConfig = new TerminationConfig().withSecondsSpentLimit(1L);
        TerminationConfig outerTerminationConfig = new TerminationConfig()
                .withTerminationConfigList(Collections.singletonList(innerTerminationConfig));
        List<Termination> terminationList =
                TerminationFactory.create(outerTerminationConfig).buildInnerTermination(mock(HeuristicConfigPolicy.class));
        assertThat(terminationList)
                .hasSize(1)
                .hasOnlyElementsOfType(TimeMillisSpentTermination.class);
        TimeMillisSpentTermination timeMillisSpentTermination = (TimeMillisSpentTermination) terminationList.get(0);
        assertThat(timeMillisSpentTermination.getTimeMillisSpentLimit()).isEqualTo(1000L);
    }

    @Test
    void buildTimeBasedTermination_withScoreDifferenceThreshold() {
        HeuristicConfigPolicy heuristicConfigPolicy = mock(HeuristicConfigPolicy.class);
        when(heuristicConfigPolicy.getScoreDefinition()).thenReturn(new HardSoftScoreDefinition());

        TerminationConfig terminationConfig = new TerminationConfig()
                .withMillisecondsSpentLimit(1000L)
                .withUnimprovedMillisecondsSpentLimit(1000L)
                .withUnimprovedScoreDifferenceThreshold("1hard/0soft");
        List<Termination> terminationList =
                TerminationFactory.create(terminationConfig).buildTimeBasedTermination(heuristicConfigPolicy);
        assertThat(terminationList).hasOnlyElementsOfTypes(TimeMillisSpentTermination.class,
                UnimprovedTimeMillisSpentScoreDifferenceThresholdTermination.class);
    }

    @Test
    void buildTimeBasedTermination_withoutScoreDifferenceThreshold() {
        TerminationConfig terminationConfig = new TerminationConfig()
                .withMillisecondsSpentLimit(1000L)
                .withUnimprovedMillisecondsSpentLimit(1000L);
        List<Termination> terminationList =
                TerminationFactory.create(terminationConfig).buildTimeBasedTermination(mock(HeuristicConfigPolicy.class));
        assertThat(terminationList).hasOnlyElementsOfTypes(TimeMillisSpentTermination.class,
                UnimprovedTimeMillisSpentTermination.class);
    }

    @Test
    void scoreDifferenceThreshold_mustBeUsedWithUnimprovedTimeSpent() {
        HeuristicConfigPolicy heuristicConfigPolicy = mock(HeuristicConfigPolicy.class);

        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.withUnimprovedScoreDifferenceThreshold("1hard/0soft");

        TerminationFactory terminationFactory = TerminationFactory.create(terminationConfig);
        assertThatIllegalStateException()
                .isThrownBy(() -> terminationFactory.buildTimeBasedTermination(heuristicConfigPolicy))
                .withMessageContaining("can only be used if an unimproved*SpentLimit");
    }
}
