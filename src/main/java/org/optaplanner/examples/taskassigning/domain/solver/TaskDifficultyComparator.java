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

package org.optaplanner.examples.taskassigning.domain.solver;

import java.io.Serializable;
import java.util.Comparator;

import org.optaplanner.examples.taskassigning.domain.Task;

public class TaskDifficultyComparator implements Comparator<Task>,
        Serializable {

    private static final Comparator<Task> COMPARATOR =
            Comparator.comparing(Task::getDuedate)
            		.thenComparing(Task::getColor)
                    .thenComparingInt(task -> task.getTaskType().getRequiredTypeList().size())
                    .thenComparingInt(task -> task.getTaskType().getBaseDuration())
                    .thenComparingInt(task -> task.getTaskType().getvolume())
                    .thenComparingLong(Task::getId);

    @Override
    public int compare(Task a, Task b) {
        return COMPARATOR.compare(a, b);
    }
}
