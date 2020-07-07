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

/**
 *
 * @author Moisis Artemiadis
 */
public class BoundingBox implements Comparable<BoundingBox>, Cloneable {

    private Point bottomLeftPoint;
    private Point topRightPoint;
    private MLClass bBoxClass;
    private float score;

    public BoundingBox() {
    }

    public BoundingBox(int x1, int y1, int x2, int y2, MLClass bBoxClass, float score) {
        this(new Point(x1, y1), new Point(x2, y2), bBoxClass, score);
    }

    protected BoundingBox(Point bottomLeftPoint, Point topRightPoint, MLClass bBoxClass, float score) {
        this.bottomLeftPoint = bottomLeftPoint;
        this.topRightPoint = topRightPoint;
        this.bBoxClass = bBoxClass;
        this.score = score;
    }

    public Point getBottomLeftPoint() {
        return bottomLeftPoint;
    }

    public Point getTopRightPoint() {
        return topRightPoint;
    }

    public MLClass getBBoxClass() {
        return bBoxClass;
    }

    public float getScore() {
        return score;
    }

    public void setBottomLeftPoint(Point bottomLeftPoint) {
        this.bottomLeftPoint = bottomLeftPoint;
    }

    public void setTopRightPoint(Point topRightPoint) {
        this.topRightPoint = topRightPoint;
    }

    public void setbBoxClass(MLClass bBoxClass) {
        this.bBoxClass = bBoxClass;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getArea() {
        return (float) (topRightPoint.x - bottomLeftPoint.x + 1) * (topRightPoint.y - bottomLeftPoint.y + 1);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BoundingBox clone = (BoundingBox) super.clone();
        clone.setBottomLeftPoint((Point) clone.getBottomLeftPoint().clone());
        clone.setTopRightPoint((Point) clone.getTopRightPoint().clone());
        return clone;
    }

    @Override
    public String toString() {
        return "[(" + bottomLeftPoint.x + ',' + bottomLeftPoint.y + ')'
                + '(' + topRightPoint.x + ',' + topRightPoint.y + ")]-> "
                + bBoxClass + "(" + score + ")";
    }

    @Override
    public int compareTo(BoundingBox bb) {
        if (this.score > bb.getScore()) {
            return 1;
        } else if (this.score < bb.getScore()) {
            return -1;
        } else {
            return 0;
        }
    }

}
