/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.taskassigning.domain;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.persistence.xstream.api.score.buildin.bendable.BendableScoreXStreamConverter;

@PlanningSolution
@XStreamAlias("TaTaskAssigningSolution")
public class TaskAssigningSolution extends AbstractPersistable {

    @ProblemFactCollectionProperty
    private List<Type> TypeList;
    @ProblemFactCollectionProperty
    private List<TaskType> taskTypeList;
    @ValueRangeProvider(id = "MachineRange")
    @ProblemFactCollectionProperty
    private List<Machine> MachineList;

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "taskRange")
    private List<Task> taskList;

    @XStreamConverter(BendableScoreXStreamConverter.class)
    @PlanningScore(bendableHardLevelsSize = 1, bendableSoftLevelsSize = 4)
    private BendableScore score;

    /** Relates to {@link Task#getStartTime()}. */
    private int frozenCutoff; // In minutes

    public TaskAssigningSolution() {
    }

    public TaskAssigningSolution(long id, List<Type> TypeList, List<TaskType> taskTypeList,
            List<Machine> MachineList, List<Task> taskList) {
        super(id);
        this.TypeList = TypeList;
        this.taskTypeList = taskTypeList;
        this.MachineList = MachineList;
        this.taskList = taskList;
    }

    public List<Type> getTypeList() {
        return TypeList;
    }

    public void setTypeList(List<Type> TypeList) {
        this.TypeList = TypeList;
    }

    public List<TaskType> getTaskTypeList() {
        return taskTypeList;
    }

    public void setTaskTypeList(List<TaskType> taskTypeList) {
        this.taskTypeList = taskTypeList;
    }

    public List<Machine> getMachineList() {
        return MachineList;
    }

    public void setMachineList(List<Machine> MachineList) {
        this.MachineList = MachineList;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public BendableScore getScore() {
        return score;
    }

    public void setScore(BendableScore score) {
        this.score = score;
    }

    public int getFrozenCutoff() {
        return frozenCutoff;
    }

    public void setFrozenCutoff(int frozenCutoff) {
        this.frozenCutoff = frozenCutoff;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
