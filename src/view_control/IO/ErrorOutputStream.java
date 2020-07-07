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
package view_control.IO;


import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author Moisis Artemiadis
 */
public class ErrorOutputStream extends OutputStream {

    private final JTextArea OUTPUT_AREA;

    public ErrorOutputStream(final JTextArea textArea) {
        this.OUTPUT_AREA = textArea;
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    @Override
    public void write(byte[] b) throws IOException {
        for (int i = 0; i < b.length; i++) {
            this.write(b[i]);
        }
    }

    public void write(byte[] b, int off, int len) throws IOException {
        for (int i = off; i < off + len - 1; i++) {
            this.write(b[i]);
        }
    }

    @Override
    public void write(int b) throws IOException {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (b == '\r') {
                    OUTPUT_AREA.append("\n\r");
                    return;
                }
                OUTPUT_AREA.append(String.valueOf((char) b));
            }
        });
    }
}
