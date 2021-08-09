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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.examples.common.swingui.components.Labeled;

@XStreamAlias("TaMachine")
public class Machine extends TaskOrMachine implements Labeled {

    private String fullName;

    private Set<Type> TypeSet;
    
    private int volume;

    public Machine() {
    }

    public Machine(long id, String fullName, int volume) {
        super(id);
        this.fullName = fullName;
        this.volume = volume;
        TypeSet = new LinkedHashSet<>();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public int getvolume() {
        return volume;
    }

    public void setvolume(int volume) {
        this.volume = volume;
    }

    public Set<Type> getTypeSet() {
        return TypeSet;
    }

    public void setTypeSet(Set<Type> TypeSet) {
        this.TypeSet = TypeSet;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public Machine getMachine() {
        return this;
    }

    @Override
    public Integer getEndTime() {
        return 0;
    }

    @Override
    public String getLabel() {
        return fullName;
    }

    public String getToolText() {
        StringBuilder toolText = new StringBuilder();
        toolText.append("<html><center><b>").append(fullName).append("</b><br/><br/>");
        toolText.append("Types:<br/>");
        for (Type Type : TypeSet) {
            toolText.append(Type.getLabel()).append("<br/>" );
        }
        toolText.append(volume + "kg<br/></center></html>");
        return toolText.toString();
    }

    @Override
    public String toString() {
        return fullName;
    }

}
