/*
 * Copyright (C) 2020 Μο
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package model.elements;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

/**
 *
 * @author Μο
 */
public class DatasetElement {

    private File elementResource;
    private LinkedList<BoundingBox> boxes;
    private LinkedList<MLClass> classes;

    public DatasetElement(String resourcePath) {
        this.elementResource = new File(resourcePath);
        boxes = new LinkedList();
        classes = new LinkedList();
    }

    private DatasetElement(File resourcePath) {
        this.elementResource = resourcePath;
        boxes = new LinkedList();
        classes = new LinkedList();
    }

    public void addBox(BoundingBox box) {
        this.boxes.add(box);
        if (!this.classes.contains(box.getBBoxClass())) {
            this.classes.add(box.getBBoxClass());
        }
    }

    public String getName() {
        return this.elementResource.getName().split("\\.")[0];
    }

    public String getPath() {
        return this.elementResource.getAbsolutePath();
    }

    public Iterator<BoundingBox> getBoxes() {
        return boxes.iterator();
    }

    public Iterator<MLClass> getClasses() {
        return classes.iterator();
    }

    public String toStringBoxes() {
        return boxes.toString();
    }

    public String toStringClasses() {
        return classes.toString();
    }

    public boolean containsMLClass(MLClass bBoxClass) {
        return this.classes.contains(bBoxClass);
    }

    public void sortBoxes() {
        Collections.sort(boxes);
    }

    public void clear() {
        boxes.clear();
        classes.clear();
    }

    public void setName(String name) {
        elementResource = new File(name);
    }

    /**
     * returns a new DatasetElement containing only the top k boxes of this
     * object. If not enough boxes are contained in this object null is
     * returned.
     *
     * @param k
     * @return
     * @throws CloneNotSupportedException
     */
    public DatasetElement getTopKBoxes(int k) throws CloneNotSupportedException {
        if (boxes.size() < k) {
            return null;
        }
        this.sortBoxes();
        int boxesTestedIndex = 1, kBoxesIndex = boxes.size() - 1;
        DatasetElement returnElem = new DatasetElement(this.elementResource.getPath());
        while (boxesTestedIndex <= k) {
            returnElem.addBox((BoundingBox) boxes.get(kBoxesIndex).clone());
            boxesTestedIndex++;
            kBoxesIndex--;
        }
        return returnElem;
    }

    /**
     * Returns all the boxes that are associated with this MLClass
     *
     * @param mlclass
     * @return
     */
    public LinkedList<BoundingBox> getAssociatedBoxes(MLClass mlclass) {
        LinkedList<BoundingBox> assocBoxes = new LinkedList();
        Iterator<BoundingBox> iter = this.getBoxes();
        BoundingBox currBox;
        //BoundingBox clone;
        while (iter.hasNext()) {
            currBox = iter.next();
            if (currBox.getBBoxClass().equals(mlclass)) {
                try {
                    assocBoxes.add((BoundingBox) currBox.clone());
                } catch (CloneNotSupportedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return assocBoxes;
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
        final DatasetElement other = (DatasetElement) obj;
        if (!Objects.equals(this.elementResource, other.elementResource)) {
            return false;
        }
        return true;
    }

}
