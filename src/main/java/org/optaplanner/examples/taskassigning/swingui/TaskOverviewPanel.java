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

package org.optaplanner.examples.taskassigning.swingui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.components.LabeledComboBoxRenderer;
import org.optaplanner.examples.taskassigning.domain.Machine;
import org.optaplanner.examples.taskassigning.domain.Type;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;
import org.optaplanner.examples.taskassigning.domain.TaskOrMachine;
import org.optaplanner.swing.impl.SwingUtils;
import org.optaplanner.swing.impl.TangoColorFactory;

public class TaskOverviewPanel extends JPanel implements Scrollable {

    public static final int HEADER_ROW_HEIGHT = 50;
    public static final int HEADER_COLUMN_WIDTH = 150;
    public static final int ROW_HEIGHT = 50;
    public static final int TIME_COLUMN_WIDTH = 60;

    private final TaskAssigningPanel taskAssigningPanel;
    private final ImageIcon[] DuedateIcons;

    private TangoColorFactory TypeColorFactory;

    private int consumedDuration = 0;

    public TaskOverviewPanel(TaskAssigningPanel taskAssigningPanel) {
        this.taskAssigningPanel = taskAssigningPanel;
        DuedateIcons = new ImageIcon[] {
                new ImageIcon(getClass().getResource("DuedateMinor.png")),
                new ImageIcon(getClass().getResource("DuedateMajor.png")),
                new ImageIcon(getClass().getResource("DuedateCritical.png"))
        };
        setLayout(null);
        setMinimumSize(new Dimension(HEADER_COLUMN_WIDTH * 2, ROW_HEIGHT * 8));
    }

    public void resetPanel(TaskAssigningSolution taskAssigningSolution) {
        removeAll();
        TypeColorFactory = new TangoColorFactory();
        List<Machine> MachineList = taskAssigningSolution.getMachineList();
        Map<Machine, Integer> MachineIndexMap = new HashMap<>(MachineList.size());
        int MachineIndex = 0;
        for (Machine Machine : MachineList) {
            JLabel MachineLabel = new JLabel(Machine.getLabel(), new TaskOrMachineIcon(Machine), SwingConstants.LEFT);
            MachineLabel.setOpaque(true);
            MachineLabel.setToolTipText(Machine.getToolText());
            MachineLabel.setLocation(0, HEADER_ROW_HEIGHT + MachineIndex * ROW_HEIGHT);
            MachineLabel.setSize(HEADER_COLUMN_WIDTH, ROW_HEIGHT);
            MachineLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            add(MachineLabel);
            MachineIndexMap.put(Machine, MachineIndex);
            MachineIndex++;
        }
        int panelWidth = HEADER_COLUMN_WIDTH;
        int unassignedIndex = MachineList.size();
        for (Task task : taskAssigningSolution.getTaskList()) {
            JButton taskButton = createTaskButton(task);
            int x;
            int y;
            if (task.getMachine() != null) {
                x = HEADER_COLUMN_WIDTH + task.getStartTime();
                y = HEADER_ROW_HEIGHT + MachineIndexMap.get(task.getMachine()) * ROW_HEIGHT;
            } else {
                x = HEADER_COLUMN_WIDTH + task.getReadyTime();
                y = HEADER_ROW_HEIGHT + unassignedIndex * ROW_HEIGHT;
                unassignedIndex++;
            }
            if (x + taskButton.getWidth() > panelWidth) {
                panelWidth = x + taskButton.getWidth();
            }
            taskButton.setLocation(x, y);
            add(taskButton);
        }
        for (int x = HEADER_COLUMN_WIDTH; x < panelWidth; x += TIME_COLUMN_WIDTH) {
            // Use 10 hours per day
            int minutes = (x - HEADER_COLUMN_WIDTH) % (24 * 60);
            // Start at 8:00
            int hours = 0 + (minutes / 60);
            minutes %= 60;
            JLabel timeLabel = new JLabel((hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes);
            timeLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            timeLabel.setLocation(x, 0);
            timeLabel.setSize(TIME_COLUMN_WIDTH, ROW_HEIGHT);
            add(timeLabel);
        }
        if ((panelWidth - HEADER_COLUMN_WIDTH) % TIME_COLUMN_WIDTH != 0) {
            panelWidth = panelWidth - ((panelWidth - HEADER_COLUMN_WIDTH) % TIME_COLUMN_WIDTH) + TIME_COLUMN_WIDTH;
        }

        Dimension size = new Dimension(panelWidth, HEADER_ROW_HEIGHT + unassignedIndex * ROW_HEIGHT);
        setSize(size);
        setPreferredSize(size);
        repaint();
    }

    public void setConsumedDuration(int consumedDuration) {
        this.consumedDuration = consumedDuration;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(TangoColorFactory.ALUMINIUM_2);
        int lineX = HEADER_COLUMN_WIDTH + consumedDuration;
        g.fillRect(HEADER_COLUMN_WIDTH, 0, lineX, getHeight());
        g.setColor(Color.WHITE);
        g.fillRect(lineX, 0, getWidth(), getHeight());
    }

    private JButton createTaskButton(Task task) {
        JButton taskButton =  SwingUtils.makeSmallButton(new JButton(new TaskAction(task)));
        taskButton.setBackground(task.isPinned() ? TangoColorFactory.ALUMINIUM_3 : TangoColorFactory.ALUMINIUM_1);
        taskButton.setHorizontalTextPosition(SwingConstants.CENTER);
        taskButton.setVerticalTextPosition(SwingConstants.TOP);
        taskButton.setSize(task.getDuration(), ROW_HEIGHT);
        return taskButton;
    }

    private class TaskAction extends AbstractAction {

        private final Task task;

        public TaskAction(Task task) {
            super(task.getCode(), new TaskOrMachineIcon(task));
            this.task = task;
            // Tooltip
            putValue(SHORT_DESCRIPTION, task.getToolText());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel listFieldsPanel = new JPanel(new GridLayout(2, 1));
            List<TaskOrMachine> taskOrMachineList = new ArrayList<>();
            taskOrMachineList.addAll(taskAssigningPanel.getSolution().getMachineList());
            taskOrMachineList.addAll(taskAssigningPanel.getSolution().getTaskList());
            // Add 1 to array size to add null, which makes the entity unassigned
            JComboBox TaskOrMachineListField = new JComboBox(
                    taskOrMachineList.toArray(new Object[taskOrMachineList.size() + 1]));
            LabeledComboBoxRenderer.applyToComboBox(TaskOrMachineListField);
            TaskOrMachineListField.setSelectedItem(task.getPreviousTaskOrMachine());
            listFieldsPanel.add(TaskOrMachineListField);
            int result = JOptionPane.showConfirmDialog(TaskOverviewPanel.this.getRootPane(),
                    listFieldsPanel, "Select previous task or Machine for " + task.getLabel(),
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                TaskOrMachine toTaskOrMachine = (TaskOrMachine) TaskOrMachineListField.getSelectedItem();
                taskAssigningPanel.getSolutionBusiness().doChangeMove(task, "previousTaskOrMachine", toTaskOrMachine);
                taskAssigningPanel.getSolverAndPersistenceFrame().resetScreen();
            }
        }

    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return SolutionPanel.PREFERRED_SCROLLABLE_VIEWPORT_SIZE;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        if (getParent() instanceof JViewport) {
            return (((JViewport) getParent()).getWidth() > getPreferredSize().width);
        }
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        if (getParent() instanceof JViewport) {
            return (((JViewport) getParent()).getHeight() > getPreferredSize().height);
        }
        return false;
    }

    private class TaskOrMachineIcon implements Icon {

        private static final int TYPE_ICON_WIDTH = 8;
        private static final int TYPE_ICON_HEIGHT = 16;

        private final ImageIcon DuedateIcon;
        private final List<Color> TypeColorList;
//        private final ImageIcon affinityIcon;

        private TaskOrMachineIcon(Task task) {
        	DuedateIcon = DuedateIcons[task.getDuedate().ordinal()];
            List<Type> TypeList = task.getTaskType().getRequiredTypeList();
            TypeColorList = new ArrayList<>(TypeList.size());
            for (Type Type : TypeList) {
                TypeColorList.add(TypeColorFactory.pickColor(Type));
            }
//            affinityIcon = affinityIcons[task.getAffinity().ordinal()];
        }

        private TaskOrMachineIcon(Machine Machine) {
        	DuedateIcon = null;
            Set<Type> TypeSet = Machine.getTypeSet();
            TypeColorList = new ArrayList<>(TypeSet.size());
            for (Type Type : TypeSet) {
                TypeColorList.add(TypeColorFactory.pickColor(Type));
            }
        }

        @Override
        public int getIconWidth() {
            int width = 0;
            if (DuedateIcon != null) {
                width += DuedateIcon.getIconWidth();
            }
            width += TypeColorList.size() * TYPE_ICON_WIDTH;
            return width;
        }

        @Override
        public int getIconHeight() {
            int height = TYPE_ICON_HEIGHT;
            if (DuedateIcon != null && DuedateIcon.getIconHeight() > height) {
                height = DuedateIcon.getIconHeight();
            }
            return height;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            int innerX = x;
            if (DuedateIcon != null) {
            	DuedateIcon.paintIcon(c, g, innerX, y);
                innerX += DuedateIcon.getIconWidth();
            }
            for (Color TypeColor : TypeColorList) {
                g.setColor(TypeColor);
                g.fillRect(innerX + 1, y + 1, TYPE_ICON_WIDTH - 2, TYPE_ICON_HEIGHT - 2);
                g.setColor(TangoColorFactory.ALUMINIUM_5);
                g.drawRect(innerX + 1, y + 1, TYPE_ICON_WIDTH - 2, TYPE_ICON_HEIGHT - 2);
                innerX += TYPE_ICON_WIDTH;
            }
        }

    }

}
