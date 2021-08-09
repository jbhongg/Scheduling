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

package org.optaplanner.examples.taskassigning.persistence;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.generator.StringDataGenerator;
import org.optaplanner.examples.taskassigning.app.TaskAssigningApp;
import org.optaplanner.examples.taskassigning.domain.Machine;
import org.optaplanner.examples.taskassigning.domain.Color;
import org.optaplanner.examples.taskassigning.domain.Duedate;
import org.optaplanner.examples.taskassigning.domain.Type;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;
import org.optaplanner.examples.taskassigning.domain.TaskType;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class TaskAssigningGenerator extends LoggingMain {

    public static final int BASE_DURATION_MINIMUM = 60;
    public static final int BASE_DURATION_MAXIMUM = 420;
    public static final int BASE_DURATION_AVERAGE = BASE_DURATION_MINIMUM + BASE_DURATION_MAXIMUM / 2;
    private static final int TYPE_SET_SIZE_MINIMUM = 2;
    private static final int TYPE_SET_SIZE_MAXIMUM = 4;

    public static void main(String[] args) {
        TaskAssigningGenerator generator = new TaskAssigningGenerator();
        generator.writeTaskAssigningSolution(24, 8);
        generator.writeTaskAssigningSolution(50, 5);
        generator.writeTaskAssigningSolution(100, 5);
        generator.writeTaskAssigningSolution(500, 20);
        // For more tasks, switch to BendableLongScore to avoid overflow in the score.
    }

    private final StringDataGenerator TypeNameGenerator = new StringDataGenerator()
            .addPart(true, 0,
                    "Problem",
                    "Team",
                    "Business",
                    "Risk",
                    "Creative",
                    "Strategic",
                    "Customer",
                    "Conflict",
                    "IT",
                    "Academic")
            .addPart(true, 1,
                    "Solving",
                    "Building",
                    "Storytelling",
                    "Management",
                    "Thinking",
                    "Planning",
                    "Service",
                    "Resolution",
                    "Engineering",
                    "Research");
    private final StringDataGenerator taskTypeNameGenerator = new StringDataGenerator()
            .addPart(true, 0,
                    "Improve",
                    "Expand",
                    "Shrink",
                    "Approve",
                    "Localize",
                    "Review",
                    "Clean",
                    "Merge",
                    "Double",
                    "Optimize")
            .addPart(true, 1,
                    "Sales",
                    "Tax",
                    "VAT",
                    "Legal",
                    "Cloud",
                    "Marketing",
                    "IT",
                    "Contract",
                    "Financial",
                    "Advertisement")
            .addPart(false, 2,
                    "Software",
                    "Development",
                    "Accounting",
                    "Management",
                    "Facilities",
                    "Writing",
                    "Productization",
                    "Lobbying",
                    "Engineering",
                    "Research");
    private final StringDataGenerator MachineNameGenerator = StringDataGenerator.buildFullNames();

    protected final SolutionFileIO<TaskAssigningSolution> solutionFileIO;
    protected final File outputDir;

    protected Random random;

    public TaskAssigningGenerator() {
        solutionFileIO = new XStreamSolutionFileIO<>(TaskAssigningSolution.class);
        outputDir = new File(CommonApp.determineDataDir(TaskAssigningApp.DATA_DIR_NAME), "unsolved");
    }

    private void writeTaskAssigningSolution(int taskListSize, int MachineListSize) {
        int TypeListSize = TYPE_SET_SIZE_MAXIMUM + (int) Math.log(MachineListSize);
        int taskTypeListSize = taskListSize / 5;
        String fileName = determineFileName(taskListSize, MachineListSize);
        File outputFile = new File(outputDir, fileName + ".xml");
        TaskAssigningSolution solution = createTaskAssigningSolution(fileName,
                taskListSize, TypeListSize, MachineListSize, taskTypeListSize);
        solutionFileIO.write(solution, outputFile);
        logger.info("Saved: {}", outputFile);
    }

    private String determineFileName(int taskListSize, int MachineListSize) {
        return taskListSize + "tasks-" + MachineListSize + "Machines";
    }

    public TaskAssigningSolution createTaskAssigningSolution(String fileName, int taskListSize, int TypeListSize,
            int MachineListSize, int taskTypeListSize) {
        random = new Random(37);
        TaskAssigningSolution solution = new TaskAssigningSolution();
        solution.setId(0L);

        createTypeList(solution, TypeListSize);
        createMachineList(solution, MachineListSize);
        createTaskTypeList(solution, taskTypeListSize);
        createTaskList(solution, taskListSize);
        solution.setFrozenCutoff(0);

        BigInteger a = AbstractSolutionImporter.factorial(taskListSize + MachineListSize - 1);
        BigInteger b = AbstractSolutionImporter.factorial(MachineListSize - 1);
        BigInteger possibleSolutionSize = (a == null || b == null) ? null : a.divide(b);
        logger.info("TaskAssigningSolution {} has {} tasks, {} Types, {} Machines, {} task types with a search space of {}.",
                fileName,
                taskListSize,
                TypeListSize,
                MachineListSize,
                taskTypeListSize,
                AbstractSolutionImporter.getFlooredPossibleSolutionSize(possibleSolutionSize));
        return solution;
    }

    private void createTypeList(TaskAssigningSolution solution, int TypeListSize) {
        List<Type> TypeList = new ArrayList<>(TypeListSize);
        TypeNameGenerator.predictMaximumSizeAndReset(TypeListSize);
        for (int i = 0; i < TypeListSize; i++) {
            Type Type = new Type();
            Type.setId((long) i);
            String TypeName = TypeNameGenerator.generateNextValue();
            Type.setName(TypeName);
            logger.trace("Created Type with TypeName ({}).", TypeName);
            TypeList.add(Type);
        }
        solution.setTypeList(TypeList);
    }

    private void createMachineList(TaskAssigningSolution solution, int MachineListSize) {
        List<Type> TypeList = solution.getTypeList();
        List<Machine> MachineList = new ArrayList<>(MachineListSize);
        int TypeListIndex = 0;
        MachineNameGenerator.predictMaximumSizeAndReset(MachineListSize);
        for (int i = 0; i < MachineListSize; i++) {
            Machine Machine = new Machine();
            Machine.setId((long) i);
            String fullName = MachineNameGenerator.generateNextValue();
            Machine.setFullName(fullName);
            int TypeSetSize = TYPE_SET_SIZE_MINIMUM + random.nextInt(TYPE_SET_SIZE_MAXIMUM - TYPE_SET_SIZE_MINIMUM);
            if (TypeSetSize > TypeList.size()) {
                TypeSetSize = TypeList.size();
            }
            Set<Type> TypeSet = new LinkedHashSet<>(TypeSetSize);
            for (int j = 0; j < TypeSetSize; j++) {
                TypeSet.add(TypeList.get(TypeListIndex));
                TypeListIndex = (TypeListIndex + 1) % TypeList.size();
            }
            Machine.setTypeSet(TypeSet);
            logger.trace("Created Machine with fullName ({}).", fullName);
            MachineList.add(Machine);
        }
        solution.setMachineList(MachineList);
    }

    private void createTaskTypeList(TaskAssigningSolution solution, int taskTypeListSize) {
        List<Machine> MachineList = solution.getMachineList();
        List<TaskType> taskTypeList = new ArrayList<>(taskTypeListSize);
        Set<String> codeSet = new LinkedHashSet<>(taskTypeListSize);
        taskTypeNameGenerator.predictMaximumSizeAndReset(taskTypeListSize);
        for (int i = 0; i < taskTypeListSize; i++) {
            TaskType taskType = new TaskType();
            taskType.setId((long) i);
            String code = taskTypeNameGenerator.generateNextValue();
            if (codeSet.contains(code)) {
                int codeSuffixNumber = 1;
                while (codeSet.contains(code + codeSuffixNumber)) {
                    codeSuffixNumber++;
                }
                code = code + codeSuffixNumber;
            }
            codeSet.add(code);
            taskType.setCode(code);
            taskType.setBaseDuration(
                    BASE_DURATION_MINIMUM + random.nextInt(BASE_DURATION_MAXIMUM - BASE_DURATION_MINIMUM));
            Machine randomMachine = MachineList.get(random.nextInt(MachineList.size()));
            ArrayList<Type> randomTypeList = new ArrayList<>(randomMachine.getTypeSet());
            Collections.shuffle(randomTypeList, random);
            int requiredTypeListSize = 1 + random.nextInt(randomTypeList.size() - 1);
            taskType.setRequiredTypeList(new ArrayList<>(randomTypeList.subList(0, requiredTypeListSize)));
            logger.trace("Created taskType with title ({}).", code);
            taskTypeList.add(taskType);
        }
        solution.setTaskTypeList(taskTypeList);
    }

    private void createTaskList(TaskAssigningSolution solution, int taskListSize) {
        List<TaskType> taskTypeList = solution.getTaskTypeList();
        Color[] colors= Color.values();
        Duedate[] Duedates = Duedate.values();
        List<Task> taskList = new ArrayList<>(taskListSize);
        Map<TaskType, Integer> maxIndexInTaskTypeMap = new LinkedHashMap<>(taskTypeList.size());
        for (int i = 0; i < taskListSize; i++) {
            Task task = new Task();
            task.setId((long) i);
            TaskType taskType = taskTypeList.get(random.nextInt(taskTypeList.size()));
            task.setTaskType(taskType);
            Integer indexInTaskType = maxIndexInTaskTypeMap.get(taskType);
            if (indexInTaskType == null) {
                indexInTaskType = 1;
            } else {
                indexInTaskType++;
            }
            task.setIndexInTaskType(indexInTaskType);
            maxIndexInTaskTypeMap.put(taskType, indexInTaskType);
            task.setReadyTime(0);
            task.setvolume(random.nextInt(4)*250+250);
            task.setColor(colors[random.nextInt(colors.length)]);
            task.setDuedate(Duedates[random.nextInt(Duedates.length)]);
            taskList.add(task);
        }
        solution.setTaskList(taskList);
    }

}
