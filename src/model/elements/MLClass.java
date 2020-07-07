/* 
 * Copyright (C) 2019 Moisis Artemiadis
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package model.elements;

import java.awt.Color;
import java.util.Objects;

/**
 *
 * @author Moisis Artemiadis
 */
public class MLClass {

    private String className;
    private int classNum;
    private Color colour;
    
    public MLClass(int classNum) {
        this("", classNum, new Color(0, 0, 0));
    }

    public MLClass(String className, int classNum) {
        this(className, classNum, new Color(0, 0, 0));
    }

    public MLClass(String className, int classNum, int r, int g, int b) {
        this(className, classNum, new Color(r, g, b));
    }

    public MLClass(String className, int classNum, float r, float g, float b) {
        this(className, classNum, new Color(r, g, b));
    }

    private MLClass(String className, int classNum, Color colour) {
        this.className = className;
        this.classNum = classNum;
        this.colour = colour;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getClassNum() {
        return classNum;
    }

    public void setClassNum(int classNum) {
        this.classNum = classNum;
    }

    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MLClass other = (MLClass) obj;
        if (this.classNum != other.classNum) {
            return false;
        }
        if (!Objects.equals(this.className, other.className)) {
            return false;
        }
        return true;
    }



    
    
    @Override
    public String toString() {
        return className + " " + classNum;
    }
}
