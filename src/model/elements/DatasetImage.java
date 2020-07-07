/*
 * Copyright (C) 2019 Moisis Artemiadis
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

import java.awt.Point;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;

/**
 *
 * @author Moisis Artemiadis
 */
public class DatasetImage {

    private ImageIcon image;
    private LinkedList<BoundingBox> boundingBoxes;

    public DatasetImage() {
    }
    

    public DatasetImage(ImageIcon image) {
        this.image = image;
        this.boundingBoxes = new LinkedList<>();
    }

    public DatasetImage(ImageIcon image, int x1, int y1, int x2, int y2, MLClass boxClass, float score) {
        this(image, new Point(x1, y1), new Point(x2, y2), boxClass, score);
    }

    private DatasetImage(ImageIcon image, Point topLeft, Point bottomRight, MLClass boxClass, float score) {
        this.image = image;
        this.boundingBoxes = new LinkedList<>();
        this.boundingBoxes.add(new BoundingBox(topLeft, bottomRight, boxClass, score));
    }

    public void setImage(ImageIcon image) {
        this.image = image;
    }

    public void addBBox(int x1, int y1, int x2, int y2, MLClass boxClass, float score) {
        this.addBBox(new Point(x1, y1), new Point(x2, y2), boxClass, score);
    }

    public void addBBox(Point topLeft, Point bottomRight, MLClass boxClass, float score) {
        if (image == null) {
            throw new IllegalArgumentException("Image is not set.");
        }
        this.boundingBoxes.add(new BoundingBox(topLeft, bottomRight, boxClass, score));
    }

    public ImageIcon getImage() {
        return image;
    }

    public ListIterator<BoundingBox> getBBoxIterator() {
        return boundingBoxes.listIterator();
    }

    @Override
    public String toString() {
        String imageS[] = image.toString().split(Pattern.quote("\\"));
        String finalString = "Image: " + imageS[imageS.length - 1];
        ListIterator<BoundingBox> bboxIter = boundingBoxes.listIterator();
        finalString += "\n" + "Labels:";
        while (bboxIter.hasNext()) {
            finalString += "\n" + bboxIter.next().toString();
        }
        return finalString;
    }
}
