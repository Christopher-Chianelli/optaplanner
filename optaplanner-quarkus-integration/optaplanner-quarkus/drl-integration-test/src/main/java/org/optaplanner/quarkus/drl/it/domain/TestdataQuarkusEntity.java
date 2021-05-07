/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.quarkus.drl.it.domain;

import org.apache.commons.lang3.ObjectUtils;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class TestdataQuarkusEntity {
    @PlanningVariable(valueRangeProviderRefs = "leftValueRange")
    public String leftValue;

    @PlanningVariable(valueRangeProviderRefs = "rightValueRange")
    public String rightValue;

    public String getLeftValue() {
        return leftValue;
    }

    public String getRightValue() {
        return rightValue;
    }

    public void setLeftValue(String leftValue) {
        this.leftValue = leftValue;
    }

    public void setRightValue(String rightValue) {
        this.rightValue = rightValue;
    }

    public String getFullValue() {
        return ObjectUtils.defaultIfNull(leftValue, "") + ObjectUtils.defaultIfNull(rightValue, "");
    }

    @Override
    public String toString() {
        return "TestdataQuarkusEntity(" + "leftValue=" + leftValue + ";rightValue=" + rightValue + ";)";
    }
}
