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
package model.functions;

import model.elements.MLClass;
import model.elements.DatasetImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;

/**
 *
 * @author Moisis Artemiadis
 */
public class ProjectManager {

    private String projectName;
    private String projectPath;
    private String csvFileName;
    private String seperator;
    private ImageLoader imageLoader;
    private ClassNamesManager classesManager;
    private DatasetManager dataset;

    public ProjectManager(String projectPath) {
        this("", projectPath, "classes.csv", ",");
    }

    public ProjectManager(String projectPath, String csvFileName) {
        this("", projectPath, csvFileName, ",");
    }

    public ProjectManager(String projectName, String projectPath, String csvFileName, String seperator) {
        this.projectName = projectName;
        this.projectPath = projectPath;
        this.seperator = seperator;
        this.csvFileName = csvFileName;
        this.imageLoader = new ImageLoader();
        this.classesManager = new ClassNamesManager();
        this.dataset = new DatasetManager();
    }

    public void setProjectPath(String projectPath) {

        this.projectPath = projectPath;
        this.imageLoader = new ImageLoader();
        this.classesManager = new ClassNamesManager();
        this.dataset = new DatasetManager();
    }

    private synchronized ListIterator<ImageIcon> getImageIterator() {
        return imageLoader.imageIterator();
    }

    public synchronized ListIterator<MLClass> getClassIterator() {
        return classesManager.classesIterator();
    }

    private synchronized ListIterator<DatasetImage> getDatasetIterator() {
        return dataset.datasetIterator();
    }

    public DatasetImage nextElement() {
        return dataset.nextElement();
    }

    public DatasetImage previousElement() {
        return dataset.previousElement();
    }

    private class DatasetManager {

        private ListIterator<ImageIcon> imageIter;
        private ListIterator<MLClass> classIter;
        private LinkedList<DatasetImage> dataset;
        private ListIterator<DatasetImage> datasetIter;
        private MLClass lastClass;

        DatasetManager() {
            imageIter = getImageIterator();
            classIter = getClassIterator();
            dataset = new LinkedList<>();
            createDataset();
            datasetIter = dataset.listIterator();
        }

        private void createDataset() {
            String[] splitArr = null;
            String splitFin;
            MLClass classNum;
            boolean found = false;
            MLClass fileCLass;
            MLClass currClass;
            File txtFile;
            while (imageIter.hasNext()) {
                ImageIcon currIm = imageIter.next();
                dataset.add(new DatasetImage(currIm));
                splitArr = currIm.getDescription().split(Pattern.quote("\\"));
                splitArr = splitArr[splitArr.length - 1].split(Pattern.quote("."));
                splitFin = splitArr[0];
                txtFile = new File(projectPath + "\\labels\\" + splitFin + ".txt");
                if (txtFile.exists()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(txtFile));) {
                        while ((splitFin = br.readLine()) != null) {
                            fileCLass = new MLClass(Integer.parseInt(splitFin));
                            while (classIter.hasNext() && !found) {
                                currClass = classIter.next();
                                if (currClass.equals(fileCLass)) {
                                    splitArr = br.readLine().split(" ");
                                    dataset.getLast().addBBox(Integer.parseInt(splitArr[0]),
                                            Integer.parseInt(splitArr[1]),
                                            Integer.parseInt(splitArr[2]),
                                            Integer.parseInt(splitArr[3]),
                                            currClass,
                                            1.0f);
                                    found = true;
                                }
                            }
                            while (classIter.hasPrevious() && !found) {
                                currClass = classIter.previous();
                                if (currClass.equals(fileCLass)) {
                                    splitArr = br.readLine().split(" ");
                                    dataset.getLast().addBBox(Integer.parseInt(splitArr[0]),
                                            Integer.parseInt(splitArr[1]),
                                            Integer.parseInt(splitArr[2]),
                                            Integer.parseInt(splitArr[3]),
                                            currClass,
                                            1.0f);
                                    found = true;
                                }
                            }
                            found = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private ListIterator<DatasetImage> datasetIterator() {
            return dataset.listIterator();
        }

        private DatasetImage nextElement() {
            if (datasetIter.hasNext()) {
                return datasetIter.next();
            } else {
                return null;
            }
        }

        private DatasetImage previousElement() {
            if (datasetIter.hasPrevious()) {
                return datasetIter.previous();
            } else {
                return null;
            }
        }

        @Override
        public String toString() {
            ListIterator<DatasetImage> outputIter = dataset.listIterator();
            String finalS = "Dataset:";
            while (outputIter.hasNext()) {
                finalS += '\n' + outputIter.next().toString();
            }
            finalS += "\n----------------";
            return finalS;
        }
    }

    private class ImageLoader {

        private LinkedList<ImageIcon> images;

        ImageLoader() {
            loadImages();
        }

        private void loadImages() {
            images = new LinkedList<>();
            File folder = new File(projectPath);
            if (folder.isDirectory()) {
                String[] imageFileNames = folder.list((d, s) -> {
                    return s.matches(".*" + Pattern.quote(".") + "[aAbBeEfFgGiIjJnNpPtTwW]*");
                });
                for (String imageFileName : imageFileNames) {
                    images.add(new ImageIcon(folder.getPath() + "\\" + imageFileName));
                }
            } else {
                throw new IllegalArgumentException("Pathname does not lead to folder.");
            }
        }

        private ListIterator<ImageIcon> imageIterator() {
            return images.listIterator();
        }

        @Override
        public String toString() {
            ListIterator<ImageIcon> outputIter = images.listIterator();
            String[] splitS;
            String finalS = "Project Images:";
            while (outputIter.hasNext()) {
                splitS = outputIter.next().getDescription().split(Pattern.quote("\\"));
                finalS += '\n' + splitS[splitS.length - 1];
            }
            finalS += "\n---------------";
            return finalS;
        }

    }

    private class ClassNamesManager {

        private PrintWriter fileWriter;
        private LinkedList<MLClass> classesList;
        private BufferedReader br;

        ClassNamesManager() {
            br = null;
            classesList = new LinkedList<>();
            loadClassNames();
        }

        private synchronized void loadClassNames() {
            MLClass mlClass;
            try {
                br = new BufferedReader(new FileReader(projectPath + "\\" + csvFileName));
                String line;
                String[] classesSplit;
                while ((line = br.readLine()) != null) {
                    classesSplit = line.split(seperator);
                    if (classesSplit.length == 2) {
                        mlClass = new MLClass(classesSplit[0],
                                Integer.parseInt(classesSplit[1]));
                    } else {
                        mlClass = new MLClass(classesSplit[0],
                                Integer.parseInt(classesSplit[1]),
                                Integer.parseInt(classesSplit[2]),
                                Integer.parseInt(classesSplit[3]),
                                Integer.parseInt(classesSplit[4]));
                    }
                    this.classesList.add(mlClass);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private ListIterator<MLClass> classesIterator() {
            return classesList.listIterator();
        }

        private void addClass(MLClass mlClass) {
            classesList.add(mlClass);
        }

        @Override
        public String toString() {
            ListIterator<MLClass> outputIter = classesList.listIterator();
            String finalS = "Project Classes:";
            while (outputIter.hasNext()) {
                finalS += '\n' + outputIter.next().toString();
            }
            finalS += "\n----------------";
            return finalS;
        }
    }

    private class LabelsWriter {

    }

    @Override
    public String toString() {
        return "Project: " + projectName
                + imageLoader.toString()
                + '\n' + classesManager.toString()
                + '\n' + dataset.toString();
    }
}
