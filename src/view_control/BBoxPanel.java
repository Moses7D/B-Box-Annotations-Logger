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
package view_control;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.LinkedList;
import javax.swing.JLayeredPane;

/**
 *
 * @author Moisis Artemiadis 154434
 */
public class BBoxPanel extends JLayeredPane {

    private int mouseX;
    private int mouseY;
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private boolean clickStart;
    private boolean clickEnd;
    private LinkedList<Rectangle> squares;

    public BBoxPanel() {
        clickStart = false;
        clickEnd = true;
        squares = new LinkedList<>();
        //setBorder(BorderFactory.createLineBorder(Color.black));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                clickStart = !clickStart;
                clickEnd = !clickEnd;
                if (!clickStart && clickEnd) {
                    squares.add(new Rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1)));
                }
                x1 = e.getX();
                y1 = e.getY();
                //System.out.println("start: " + clickStart + ", end: " + clickEnd);
                //if(!clickStart && clickEnd) squares.add(new Rectangle(squareX, squareY, squareW, squareH));
            }
        });

        /*addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                moveGuides(e.getX(), e.getY());
            }
        });*/
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                //System.out.println("X: "+e.getX());
                //System.out.println("Y: "+e.getY());
                createSquare(e.getX(), e.getY());
                moveGuides(e.getX(), e.getY());
            }
        });

    }

    private void moveGuides(int x, int y) {
        if ((mouseX != x) || (mouseY != y)) {
            //repaint();
            mouseX = x;
            mouseY = y;
            repaint();
        }
    }

    private void createSquare(int x, int y) {
        if (clickStart) {
            x2=x;
            y2=y;
            repaint(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
        }
        //System.out.println("CREATE======\nx1: " + x1 + ", y1: " + y1 + ", width: " + squareW + ", height: " + squareH);
        //System.out.println("x2: " + x + ", y2: " + y + ", width: " + squareW + ", height: " + squareH);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (clickStart && !clickEnd ) {
            //System.out.println(hasClicked);
            g.setColor(new Color(1.0F, 1.0F, 1.0F, 0.0F));
            g.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
            g.setColor(new Color(0, 0, 0, 255));
            g.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
            //System.out.println("PAINT======\nx1: " + x1 + ", y1: " + y1 + ", width: " + squareW + ", height: " + squareH);
        }
        g.setColor(new Color(0, 0, 0, 255));
        g.drawLine(mouseX, mouseY, mouseX + this.getWidth(), mouseY);
        g.drawLine(mouseX, mouseY, mouseX, mouseY + this.getHeight());
        g.drawLine(mouseX, mouseY, mouseX - this.getWidth(), mouseY);
        g.drawLine(mouseX, mouseY, mouseX, mouseY - this.getHeight());
        for (int i = 0; i < squares.size(); i++) {
            g.setColor(new Color(1.0F, 1.0F, 1.0F, 0.0F));
            g.fillRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height);
            g.setColor(new Color(0, 0, 0, 255));
            g.drawRect(squares.get(i).x, squares.get(i).y, squares.get(i).width, squares.get(i).height);
        }
    }

    public boolean squaresIsEmpty(){
        return squares.isEmpty();
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }
    
    
    public String getLastSqtoString() {
        if(!squares.isEmpty())
        return "(" + squares.getLast().x + "," + squares.getLast().y + ")->(" +
                (squares.getLast().x+squares.getLast().width-1) + "," +
                (squares.getLast().y+squares.getLast().height-1) +")";
        else return "___";
    }
    
    
}
