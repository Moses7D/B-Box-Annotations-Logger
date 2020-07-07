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
package view_control;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import javax.swing.JLabel;

/**
 *
 * @author Moisis Artemiadis
 */
public class BBoxJLabel extends JLabel {

    private int mouseX;
    private int mouseY;
    private boolean drawGuides;

    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private boolean clickStart;
    private boolean clickEnd;
    private LinkedList<Rectangle> squares;

    public BBoxJLabel() {
        drawGuides = false;
        clickStart = false;
        clickEnd = true;
        squares = new LinkedList<>();

    }

    public void mouseMoved(MouseEvent e) {
        createSquare(e.getX(), e.getY());
        moveGuides(e.getX(), e.getY());
    }

    public void mousePressed(MouseEvent e) {
        clickStart = !clickStart;
        clickEnd = !clickEnd;
        if (!clickStart && clickEnd) {
            addReactangle(x1, y1, x2, y2);
        }
        x1 = e.getX();
        y1 = e.getY();
    }

    public void mouseEntered() {
        drawGuides = true;
    }

    public void mouseExited() {
        drawGuides = false;
        repaint();
    }

    private synchronized void moveGuides(int x, int y) {
        if ((mouseX != x) || (mouseY != y)) {
            //repaint();
            mouseX = x;
            mouseY = y;
            repaint();
        }
    }

    private void drawGuides(Graphics g) {
        g.setColor(new Color(0, 0, 0, 255));
        g.drawLine(mouseX, mouseY, mouseX + this.getWidth(), mouseY);
        g.drawLine(mouseX, mouseY, mouseX, mouseY + this.getHeight());
        g.drawLine(mouseX, mouseY, mouseX - this.getWidth(), mouseY);
        g.drawLine(mouseX, mouseY, mouseX, mouseY - this.getHeight());
    }

    private synchronized void createSquare(int x, int y) {
        if (clickStart) {
            x2 = x;
            y2 = y;
            repaint(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
        }
        //System.out.println("CREATE======\nx1: " + x1 + ", y1: " + y1 + ", width: " + squareW + ", height: " + squareH);
        //System.out.println("x2: " + x + ", y2: " + y + ", width: " + squareW + ", height: " + squareH);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (clickStart && !clickEnd) {
            //System.out.println(hasClicked);
            g.setColor(new Color(1.0F, 1.0F, 1.0F, 0.0F));
            g.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
            g.setColor(new Color(0, 0, 0, 255));
            g.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
            //System.out.println("PAINT======\nx1: " + x1 + ", y1: " + y1 + ", width: " + squareW + ", height: " + squareH);
        }
        if (drawGuides) {
            drawGuides(g);
        }
        for (int i = 0; i < squares.size(); i++) {
            g.setColor(new Color(1.0F, 1.0F, 1.0F, 0.0F));
            g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height);
            g.setColor(new Color(0, 0, 0, 255));
            g.drawRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height);
        }
    }

    public synchronized Rectangle addReactangle(int x1, int y1, int x2, int y2) {
        Rectangle r = new Rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
        squares.add(r);
        return r;
    }

    public synchronized void removeReactangle(int[] i) {
        for (int j = 0; j < i.length - 1; j++) {
            squares.remove(i[j]);
        }
    }

    public synchronized void removeReactangle(Rectangle r) {
        squares.remove(r);
    }

    public boolean squaresIsEmpty() {
        return squares.isEmpty();
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public Rectangle getLastSq() {
        if (!squares.isEmpty()) {
            return squares.getLast();
        } else {
            return null;
        }
    }

    public String getLastSqtoString() {
        if (!squares.isEmpty()) {
            return "(" + squares.getLast().x + "," + squares.getLast().y + ")->("
                    + (squares.getLast().x + squares.getLast().width) + ","
                    + (squares.getLast().y + squares.getLast().height) + ")";
        } else {
            return "___";
        }
    }
}
