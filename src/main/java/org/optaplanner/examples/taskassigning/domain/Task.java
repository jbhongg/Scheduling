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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.examples.common.swingui.components.Labeled;
import org.optaplanner.examples.taskassigning.domain.solver.StartTimeUpdatingVariableListener;
import org.optaplanner.examples.taskassigning.domain.solver.TaskDifficultyComparator;

@PlanningEntity(difficultyComparatorClass = TaskDifficultyComparator.class)
@XStreamAlias("TaTask")
public class Task extends TaskOrMachine implements Labeled {

    private TaskType taskType;
    private int indexInTaskType;
    private int readyTime;
    private Duedate Duedate;
    private Color color;
    private int volume;
    @PlanningPin
    private boolean pinned;

    // Planning variables: changes during planning, between score calculations.
    @PlanningVariable(valueRangeProviderRefs = {"MachineRange", "taskRange"},
            graphType = PlanningVariableGraphType.CHAINED)
    private TaskOrMachine previousTaskOrMachine;

    // Shadow variables
    // Task nextTask inherited from superclass
    @AnchorShadowVariable(sourceVariableName = "previousTaskOrMachine")
    private Machine Machine;
    @CustomShadowVariable(variableListenerClass = StartTimeUpdatingVariableListener.class,
            // Arguable, to adhere to API specs (although this works), nextTask and Machine should also be a source,
            // because this shadow must be triggered after nextTask and Machine (but there is no need to be triggered by those)
            sources = {@PlanningVariableReference(variableName = "previousTaskOrMachine")})
    private Integer startTime; // In minutes

    public Task() {
    }

    public Task(long id, TaskType taskType, int indexInTaskType, int readyTime, Color color, Duedate Duedate, int volume) {
        super(id);
        this.taskType = taskType;
        this.indexInTaskType = indexInTaskType;
        this.readyTime = readyTime;
        this.color = color;
        this.Duedate = Duedate;
        this.volume = volume;
        pinned = false;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public int getIndexInTaskType() {
        return indexInTaskType;
    }

    public void setIndexInTaskType(int indexInTaskType) {
        this.indexInTaskType = indexInTaskType;
    }

    public int getReadyTime() {
        return readyTime;
    }

    public void setReadyTime(int readyTime) {
        this.readyTime = readyTime;
    }
    
    public int getvolume() {
        return volume;
    }

    public void setvolume(int volume) {
        this.volume = volume;
    }
    
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Duedate getDuedate() {
        return Duedate;
    }

    public void setDuedate(Duedate Duedate) {
        this.Duedate = Duedate;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public TaskOrMachine getPreviousTaskOrMachine() {
        return previousTaskOrMachine;
    }

    public void setPreviousTaskOrMachine(TaskOrMachine previousTaskOrMachine) {
        this.previousTaskOrMachine = previousTaskOrMachine;
    }

    @Override
    public Machine getMachine() {
        return Machine;
    }

    public void setMachine(Machine Machine) {
        this.Machine = Machine;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public int getMissingTypeCount() {
        if (Machine == null) {
            return 0;
        }
        int count = 0;
        for (Type Type : taskType.getRequiredTypeList()) {
            if (!Machine.getTypeSet().contains(Type)) {
                count++;
            }
        }
        return count;
    }

    /**
     * In minutes
     * @return at least 1 minute
     */
    public int getDuration() {
        return taskType.getBaseDuration()*(volume/200);
    }

    @Override
    public Integer getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime + getDuration();
    }

    public String getCode() {
        return taskType + "-" + indexInTaskType;
    }

    @Override
    public String getLabel() {
        return getCode();
    }

    public String getToolText() {
        StringBuilder toolText = new StringBuilder();
        toolText.append("<html><center><b>").append(getLabel()).append("</b><br/>")
        .append(Duedate.getLabel()).append("<br/><br/>");
        toolText.append(color.getLabel()).append("<br/><br/>" + volume + "kg<br/><br/>");
        toolText.append("Required Types:<br/>");
        for (Type Type : taskType.getRequiredTypeList()) {
            toolText.append(Type.getLabel()).append("<br/>");
        }
        toolText.append("</center></html>");
        return toolText.toString();
    }

    @Override
    public String toString() {
        return getCode();
    }

}
