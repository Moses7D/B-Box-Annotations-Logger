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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;

/**
 *
 * @author Moisis Artemiadis
 */
public class Utilities {

    public static char SEPERATOR = ',';
    public static String CONCAT = "";
    public static final String NAMES[] = {"",
        "YOLO",
        "YOLOv3-320",
        "YOLOv3-416",
        "YOLOv3-608",
        "YOLOv3-TINY",
        "YOLOv3-SPP"};
    // 1: 3000.951416, 3000.951416 avg loss, 0.000000 rate, 31.106069 seconds, 64 images
    // (next mAP calculation at 1414 iterations)
    // New best mAP! 
    //class_id = 0, name = Xtipiti, ap = 61.32%   	 (TP = 7, FP = 7) 
    // mean_average_precision (mAP@0.5) = 0.809347 
    // for conf_thresh = 0.25, precision = 0.55, recall = 0.27, F1-score = 0.36 
    private static final String PATTERNS[] = {"^\\s*\\d+:.*$",
        "^\\s*\\(next\\s*mAP\\s*calculation\\s*at\\s*\\d*\\s*iterations\\)\\s*$",
        "^\\s*New best mAP!.*$",
        "^\\s*class_id\\s=\\s.*$",
        "^\\s*mean_.*$",
        "^\\s*for\\s*conf_thresh\\s*=\\s*.*$",
        "^\\s*IoU\\s*threshold\\s*=\\s*\\d*\\s*%,.*$"};

    /**
     *
     * @param startNum
     * @param path
     */
    public static void imageNumberings(int startNum, String path) {
        try {
            File folder = new File(path);
            File[] files = folder.listFiles();
            File newFile;
            int counter = startNum;
            String zeros;
            for (int i = 0; i < files.length; i++) {
                if (counter < 10) {
                    zeros = "000";
                } else if (counter < 100) {
                    zeros = "00";
                } else if (counter < 1000) {
                    zeros = "0";
                } else {
                    zeros = "";
                }
                newFile = new File(path + "\\" + zeros + Integer.toString(counter) + ".jpg");
                if (files[i].isFile()) {
                    if (!files[i].renameTo(newFile)) {
                        System.out.println("Could not rename file " + files[i].getName() + " to " + Integer.toString(counter));
                    }
                    counter++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param logPath
     * @param outputPath
     * @param patternInd
     * @param filter
     * @param namesInd
     */
    public static void logParser(String logPath, String outputPath, int patternInd, boolean filter, int namesInd) {
        try {
            //outputPath.replaceAll(logPath, nameConcat);
            if (namesInd < 0 || namesInd >= NAMES.length) {
                namesInd = 0;
            }
            File logFile = new File(logPath);
            File outFile = new File(outputPath);
            BufferedReader logBR = new BufferedReader(new FileReader(logFile));
            System.out.println("==Reading: " + logFile.getAbsolutePath() + "  ==");
            int counter = 0;
            if (patternInd == 0) {
                counter = matchAVGLoss(logBR, outFile, filter, namesInd);
            } else if (patternInd == 1) {
                counter = matchMAP(logBR, outFile, filter, namesInd);
            } else if (patternInd == 2) {
                if (filter) {
                    counter = matchPerClassMetricsExcel(logBR, outFile, namesInd);
                } else {
                    counter = matchPerClassMetrics(logBR, outFile, namesInd);
                }

            }
            System.out.println("==Finished Reading, " + counter + " lines printed==");
            logBR.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //match AVG loss lines, match mAP lines
    /**
     *
     * @param logBR
     * @param outFile
     * @param filter100
     * @param namesInd
     * @return
     * @throws IOException
     */
    protected static int matchAVGLoss(BufferedReader logBR, File outFile, boolean filter100, int namesInd) throws IOException {
        if (outFile.exists()) {
            outFile.delete();
        }
        outFile.mkdirs();
        outFile = new File(outFile, NAMES[namesInd]
                + " Iterations AVG Loss_" + ((filter100) ? "P100" : "auto") + CONCAT + ".csv");
        String nextP = "";
        String lineS[];
        String elS[];
        int linesWritten = 1, linesRead = 0;
        try (PrintStream outPS = new PrintStream(outFile)) {
            outPS.append("Iteration" + SEPERATOR + "AVG Loss\n");
            while ((nextP = logBR.readLine()) != null) {
                linesRead++;
                if (nextP.matches(PATTERNS[0])) {
                    lineS = nextP.split("\\s");
                    elS = lineS[1].split(":");
                    if (filter100) {
                        if ((Integer.parseInt(elS[0]) % 100) == 0) {
                            outPS.append(elS[0] + SEPERATOR
                                    + lineS[3] + "\n");
                            linesWritten++;
                        }
                    } else {
                        outPS.append(elS[0] + SEPERATOR
                                + lineS[3] + "\n");
                        linesWritten++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Last line read: **" + nextP + "**");
            System.out.println("Total lines read: " + linesRead);
            System.out.println("Total lines written: " + linesWritten);
        }
        return linesWritten;
    }

    /**
     * <p>
     * <b>Line:</b><i> (next mAP calculation at 2714 iterations) </i>
     * <br>
     * Array__Length: 7 String__Splitter: \s
     * <br>
     *
     * <br>
     * (next
     * <br>
     * mAP
     * <br>
     * calculation
     * <br>
     * at
     * <br>
     * 1414
     * <br>
     * iterations)
     * <br>
     * </p>
     * <br>
     * <p>
     * <b>Line:</b><i> 1414: 0.784680, 0.851108 avg loss, 0.001000 rate,
     * 30.961694 seconds, 90496 images</i>
     * <br>
     * Array__Length: 12 String__Splitter: \s
     * <br>
     *
     * <br>
     * 1414:
     * <br>
     * 0.784680,
     * <br>
     * 0.851108
     * <br>
     * avg
     * <br>
     * loss,
     * <br>
     * 0.001000
     * <br>
     * rate,
     * <br>
     * 30.961694
     * <br>
     * seconds,
     * <br>
     * 90496
     * <br>
     * images
     * <br>
     * </p>
     * <br>
     * <p>
     * <b>Line:</b><i> for conf_thresh = 0.25, precision = 0.55, recall = 0.27,
     * F1-score = 0.36 </i>
     * <br>
     * Array__Length: 14 String__Splitter: \s
     * <br>
     *
     * <br>
     * for
     * <br>
     * conf_thresh
     * <br>
     * =
     * <br>
     * 0.25,
     * <br>
     * precision
     * <br>
     * =
     * <br>
     * 0.55,
     * <br>
     * recall
     * <br>
     * =
     * <br>
     * 0.27,
     * <br>
     * F1-score
     * <br>
     * =
     * <br>
     * 0.36
     * <br>
     * </p>
     * <br>
     * <p>
     * <b>Line:</b><i> for conf_thresh = 0.25, TP = 286, FP = 230, FN = 781,
     * average IoU = 40.65 % </i>
     * <br>
     * Array__Length: 19 String__Splitter: \s
     * <br>
     *
     * <br>
     * for
     * <br>
     * conf_thresh
     * <br>
     * =
     * <br>
     * 0.25,
     * <br>
     * TP
     * <br>
     * =
     * <br>
     * 286,
     * <br>
     * FP
     * <br>
     * =
     * <br>
     * 230,
     * <br>
     * FN
     * <br>
     * =
     * <br>
     * 781,
     * <br>
     * average
     * <br>
     * IoU
     * <br>
     * =
     * <br>
     * 40.65
     * <br>
     * %
     * <br>
     * </p>
     * <br>
     * <p>
     * <b>Line:</b><i> IoU threshold = 50 %, used Area-Under-Curve for each
     * unique Recall </i>
     * <br>
     * Array__Length: 12 String__Splitter: \s
     * <br>
     *
     * <br>
     * IoU
     * <br>
     * threshold
     * <br>
     * =
     * <br>
     * 50
     * <br>
     * %,
     * <br>
     * used
     * <br>
     * Area-Under-Curve
     * <br>
     * for
     * <br>
     * each
     * <br>
     * unique
     * <br>
     * Recall
     * <br>
     * </p>
     * <br>
     * <p>
     * <b>Line:</b><i> mean average precision (mAP@0.50) = 0.476393, or 47.64 %
     * </i>
     * <br>
     * Array__Length: 10 String__Splitter: \s
     * <br>
     *
     * <br>
     * mean
     * <br>
     * average
     * <br>
     * precision
     * <br>
     * (mAP@0.50)
     * <br>
     * =
     * <br>
     * 0.476393,
     * <br>
     * or
     * <br>
     * 47.64
     * <br>
     * %
     * <br>
     * </p>
     * <br>
     *
     * @param logBR
     * @param outFile
     * @param mAPPercent
     * @param namesInd
     * @return
     * @throws IOException
     */
    protected static int matchMAP(BufferedReader logBR, File outFile, boolean mAPPercent, int namesInd) throws IOException {
        if (outFile.exists()) {
            outFile.delete();
        }
        outFile.mkdirs();
        outFile = new File(outFile, NAMES[namesInd]
                + " Iterations mAP_" + ((mAPPercent) ? "%" : "D") + CONCAT + ".csv");

        String nextP = "";
        String lineS[] = {};
        String output[] = new String[13];
        int i, linesWritten = 1, linesRead = 0;
        boolean isSaved;
        try (PrintStream outPS = new PrintStream(outFile)) {
            outPS.append("Iteration" + SEPERATOR
                    + "AVG Loss" + SEPERATOR
                    + "Confidence Threshold" + SEPERATOR
                    + "IoU Threshold" + SEPERATOR
                    + "Precision" + SEPERATOR
                    + "Recall" + SEPERATOR
                    + "F1-Score" + SEPERATOR
                    + "TP" + SEPERATOR
                    + "FP" + SEPERATOR
                    + "FN" + SEPERATOR
                    + "Average IoU" + SEPERATOR
                    + "mean Average Precision" + SEPERATOR
                    + "saved\n");
            while ((nextP = logBR.readLine()) != null) {
                linesRead++;
                //Find next " (next mAP calculation at " line
                if (nextP.matches(PATTERNS[1])) {
                    //Keep * (next mAP calculation at 1414 iterations) * line's iteration num
                    lineS = nextP.split("\\s");
                    i = Integer.parseInt(lineS[5]);
                    //* 1414: 0.784680, 0.851108 avg loss, * line
                    do {
                        nextP = logBR.readLine();
                        linesRead++;
                    } while (!nextP.matches(PATTERNS[0]));
                    lineS = nextP.split("\\s");
                    if (i == Integer.parseInt(lineS[1].split(":")[0])) {
                        output[0] = lineS[1].split(":")[0] + SEPERATOR;
                        output[1] = lineS[3] + SEPERATOR;
                        //Skip lines till you meet * for conf_thresh = 0.25, precision = 0.55, recall = 0.27, F1-score = 0.36 * line
                        do {
                            nextP = logBR.readLine();
                            linesRead++;
                        } while (!nextP.matches(PATTERNS[5]));
                        lineS = nextP.split("\\s");
                        output[2] = lineS[4].split(",")[0] + SEPERATOR;
                        output[4] = lineS[7].split(",")[0] + SEPERATOR;
                        output[5] = lineS[10].split(",")[0] + SEPERATOR;
                        output[6] = lineS[13].split(",")[0] + SEPERATOR;
                        //Skip lines till you meet *for conf_thresh = 0.25, TP = 286, FP = 230, FN = 781, average IoU = 40.65 % * line
                        do {
                            nextP = logBR.readLine();
                            linesRead++;
                        } while (!nextP.matches(PATTERNS[5]));
                        lineS = nextP.split("\\s");
                        output[7] = lineS[7].split(",")[0] + SEPERATOR;
                        output[8] = lineS[10].split(",")[0] + SEPERATOR;
                        output[9] = lineS[13].split(",")[0] + SEPERATOR;
                        output[10] = lineS[17] + '%' + SEPERATOR;
                        //Skip lines till you meet * IoU threshold = 50 %, used Area-Under-Curve for each unique Recall * line
                        do {
                            nextP = logBR.readLine();
                            linesRead++;
                        } while (!nextP.matches(PATTERNS[6]));
                        lineS = nextP.split("\\s");
                        output[3] = lineS[4] + SEPERATOR;
                        //*mean average precision (mAP@0.50) = 0.476393, or 47.64 % * line
                        nextP = logBR.readLine();
                        linesRead++;
                        lineS = nextP.split("\\s");
                        if (mAPPercent) {
                            output[11] = lineS[8] + "%" + SEPERATOR;
                        } else {
                            output[11] = lineS[6].split(",")[0] + SEPERATOR;
                        }
                        //Skip lines till you meet * mean_average_precision (mAP@0.5) = 0.809347* line
                        do {
                            nextP = logBR.readLine();
                            linesRead++;
                        } while (nextP != null && !nextP.matches(PATTERNS[4]));
                        //*New best mAP!* line
                        nextP = logBR.readLine();
                        linesRead++;
                        isSaved = nextP.matches(PATTERNS[2]);
                        if (isSaved) {
                            output[12] = String.valueOf(isSaved) + "\n";
                            nextP = output[0];
                            for (i = 1; i < output.length; i++) {
                                nextP += output[i];
                            }
                            outPS.append(nextP);
                            linesWritten++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Last line read: **" + nextP + "**");
            System.out.println("Total lines read: " + linesRead);
            System.out.println("Total lines written: " + linesWritten);
        }
        return linesWritten;
    }

    /**
     *
     * <p>
     * <b>Line:</b><i> (next mAP calculation at 2714 iterations) </i>
     * <br>
     * Array__Length: 7 String__Splitter: \s
     * <br>
     *
     * <br>
     * (next
     * <br>
     * mAP
     * <br>
     * calculation
     * <br>
     * at
     * <br>
     * 1414
     * <br>
     * iterations)
     * <br>
     * </p>
     * <br>
     * <p>
     * <b>Line:</b><i> 1414: 0.784680, 0.851108 avg loss, 0.001000 rate,
     * 30.961694 seconds, 90496 images</i>
     * <br>
     * Array__Length: 12 String__Splitter: \s
     * <br>
     *
     * <br>
     * 1414:
     * <br>
     * 0.784680,
     * <br>
     * 0.851108
     * <br>
     * avg
     * <br>
     * loss,
     * <br>
     * 0.001000
     * <br>
     * rate,
     * <br>
     * 30.961694
     * <br>
     * seconds,
     * <br>
     * 90496
     * <br>
     * images
     * <br>
     * </p>
     * <br>
     * <p>
     * <b>Line:</b><i>class_id = 0, name = Xtipiti, ap = 61.32% (TP = 7, FP = 7)
     * </i>
     * <br>
     * Array__Length: 19 String__Splitter: \s
     * <br>
     * class_id
     * <br>
     * =
     * <br>
     * 0,
     * <br>
     * name
     * <br>
     * =
     * <br>
     * Xtipiti,
     * <br>
     * ap
     * <br>
     * =
     * <br>
     * 61.32%
     * <br>
     *
     * <br>
     *
     * <br>
     *
     * <br>
     *
     * <br>
     * (TP
     * <br>
     * =
     * <br>
     * 7,
     * <br>
     * FP
     * <br>
     * =
     * <br>
     * 7)
     * <br>
     * </p>
     * <p>
     * <b>Line:</b><i>class_id = 0, name = Xtipiti, ap = 61.32% (TP = 7, FP = 7)
     * </i>
     * <br>
     * Array__Length: 4 String__Splitter: ,
     * <br>
     * class_id = 0
     * <br>
     * name = Xtipiti
     * <br>
     * ap = 61.32% (TP = 7
     * <br>
     * FP = 7)
     * <br>
     * </p>
     * <br>
     * <p>
     * <b>Line:</b><i>ap = 61.32% (TP = 7</i>
     * <br>
     * Array__Length: 6 String__Splitter: \s
     * <br>
     * ap
     * <br>
     * =
     * <br>
     * 61.32%
     * <br>
     * (TP
     * <br>
     * =
     * <br>
     * 7
     * <br>
     * </p>
     * <br>
     *
     * @param logBR
     * @param outFile
     * @param makeExcel
     * @param namesInd
     * @return
     * @throws java.io.IOException
     */
    protected static int matchPerClassMetrics(BufferedReader logBR, File outFile, int namesInd) throws IOException {
        if (outFile.exists()) {
            outFile.delete();
        }
        outFile.mkdirs();
        PrintStream outPS;
        String nextP = "";
        String lineS[] = {};
        String[] toPrint = new String[84];
        int iterationN, i, linesWritten = 1, linesRead = 0;
        try {
            while ((nextP = logBR.readLine()) != null) {
                linesRead++;
                //Find next " (next mAP calculation at " line
                if (nextP.matches(PATTERNS[1])) {
                    //Keep * (next mAP calculation at 1414 iterations) * line's iteration num
                    lineS = nextP.split("\\s");
                    iterationN = Integer.parseInt(lineS[5]);
                    //* 1414: 0.784680, 0.851108 avg loss, * line
                    nextP = logBR.readLine();
                    linesRead++;
                    while (!nextP.matches(PATTERNS[0])) {
                        nextP = logBR.readLine();
                        linesRead++;
                    }
                    lineS = nextP.split("\\s");
                    if (iterationN == Integer.parseInt(lineS[1].split(":")[0])) {
                        //Skip lines till you meet *class_id = 0, name = Xtipiti, ap = 61.32%   	 (TP = 7, FP = 7) * line
                        do {
                            nextP = logBR.readLine();
                            linesRead++;
                        } while (nextP != null && !nextP.matches(PATTERNS[3]));
                        //read *class_id = 0, name = Xtipiti, ap = 61.32%   	 (TP = 7, FP = 7) * line
                        i = 0;
                        while (nextP != null && nextP.matches(PATTERNS[3])) {
                            if (i >= toPrint.length) {
                                System.out.println("Fatal Error");
                                return -1;
                            }
                            lineS = nextP.split(",");
                            toPrint[i] = lineS[0].split("=")[1].trim() + SEPERATOR
                                    + lineS[1].split("=")[1].trim() + SEPERATOR
                                    + lineS[2].split("=")[1].split("\\s")[1] + SEPERATOR
                                    + lineS[2].split("=")[2].trim() + SEPERATOR
                                    + lineS[3].split("\\s")[3].split("\\)")[0] + "\n";
                            linesWritten++;
                            nextP = logBR.readLine();
                            linesRead++;
                            i++;
                        }
                        //Skip lines till you meet * mean_average_precision (mAP@0.5) = 0.809347* line
                        do {
                            nextP = logBR.readLine();
                            linesRead++;
                        } while (nextP != null && !nextP.matches(PATTERNS[4]));
                        //*New best mAP!* line
                        nextP = logBR.readLine();
                        linesRead++;
                        if (nextP.matches(PATTERNS[2])) {
                            outPS = new PrintStream(new File(outFile, NAMES[namesInd]
                                    + " Iterations Classes_" + iterationN + CONCAT + ".csv"));
                            outPS.append("Class ID" + SEPERATOR
                                    + "Class Name" + SEPERATOR
                                    + "ap" + SEPERATOR
                                    + "TP" + SEPERATOR
                                    + "FP\n");
                            for (i = 0; i < toPrint.length; i++) {
                                outPS.append(toPrint[i]);
                            }
                            outPS.close();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Last line read: **" + nextP + "**");
            System.out.println("Total lines read: " + linesRead);
            System.out.println("Total lines written: " + linesWritten);
        }
        return linesWritten;
    }

    protected static int matchPerClassMetricsExcel(BufferedReader logBR, File outFile, int namesInd) throws IOException {
        if (outFile.exists()) {
            outFile.delete();
        }
        outFile.mkdirs();
        WritableWorkbook workbook = Workbook.createWorkbook(new File(outFile, NAMES[namesInd]
                + " Iterations Classes_" + CONCAT + ".xls"));
        WritableSheet excelSheet;
        Label label;
        Number number;
        String nextP = "";
        String lineS[] = {};
        String[][] toPrint = new String[85][5];
        toPrint[0][0] = "Class ID";
        toPrint[0][1] = "Class Name";
        toPrint[0][2] = "ap";
        toPrint[0][3] = "TP";
        toPrint[0][4] = "FP";
        int iterationN, i, j, sheetInd = 0, linesWritten = 1, linesRead = 0;
        try {
            while ((nextP = logBR.readLine()) != null) {
                linesRead++;
                //Find next " (next mAP calculation at " line
                if (nextP.matches(PATTERNS[1])) {
                    //Keep * (next mAP calculation at 1414 iterations) * line's iteration num
                    lineS = nextP.split("\\s");
                    iterationN = Integer.parseInt(lineS[5]);
                    //* 1414: 0.784680, 0.851108 avg loss, * line
                    nextP = logBR.readLine();
                    linesRead++;
                    while (!nextP.matches(PATTERNS[0])) {
                        nextP = logBR.readLine();
                        linesRead++;
                    }
                    lineS = nextP.split("\\s");
                    if (iterationN == Integer.parseInt(lineS[1].split(":")[0])) {
                        //Skip lines till you meet *class_id = 0, name = Xtipiti, ap = 61.32%   	 (TP = 7, FP = 7) * line
                        do {
                            nextP = logBR.readLine();
                            linesRead++;
                        } while (nextP != null && !nextP.matches(PATTERNS[3]));
                        //read *class_id = 0, name = Xtipiti, ap = 61.32%   	 (TP = 7, FP = 7) * line
                        i = 1;
                        while (nextP != null && nextP.matches(PATTERNS[3])) {
                            if (i >= toPrint.length) {
                                System.out.println("Fatal Error");
                                return -1;
                            }
                            lineS = nextP.split(",");
                            toPrint[i][0] = lineS[0].split("=")[1].trim();
                            toPrint[i][1] = lineS[1].split("=")[1].trim();
                            toPrint[i][2] = lineS[2].split("=")[1].split("\\s")[1];
                            toPrint[i][3] = lineS[2].split("=")[2].trim();
                            toPrint[i][4] = lineS[3].split("\\s")[3].split("\\)")[0];
                            linesWritten++;
                            nextP = logBR.readLine();
                            linesRead++;
                            i++;
                        }
                        //Skip lines till you meet * mean_average_precision (mAP@0.5) = 0.809347* line
                        do {
                            nextP = logBR.readLine();
                            linesRead++;
                        } while (nextP != null && !nextP.matches(PATTERNS[4]));
                        //*New best mAP!* line
                        nextP = logBR.readLine();
                        linesRead++;
                        if (nextP.matches(PATTERNS[2])) {
                            excelSheet = workbook.createSheet(Integer.toString(iterationN), sheetInd);
                            for(i=0; i < toPrint.length; i++) {
                                for(j=0; j < toPrint[i].length; j++) {
                                    if(isNumeric(toPrint[i][j])) {
                                        number = new Number(j,i,Integer.parseInt(toPrint[i][j]));
                                        excelSheet.addCell(number);
                                    } else {
                                        label = new Label(j,i,toPrint[i][j]);
                                        excelSheet.addCell(label);
                                    }
                                }
                                linesWritten++;
                            }
                            sheetInd++;
                        }
                    }
                }
            }
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Last line read: **" + nextP + "**");
            System.out.println("Total lines read: " + linesRead);
            System.out.println("Total lines written: " + linesWritten);
        }
        return linesWritten;
    }

    public static boolean isNumeric(final String str) {
        if (str == null || str.length() == 0) {
            return false;
        }

        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;

    }
}
