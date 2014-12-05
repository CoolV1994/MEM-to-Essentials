package CoolV1994;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright (c) by Vinnie (CoolV1994)
 */
public class Convert {
    public static final File csvFile = new File("Minecraft Economy Manager.csv");
    public static final File ymlFile = new File("worth.yml");
    public static final StringBuilder yamlTemp = new StringBuilder();
    public static final String lineSep = System.getProperty("line.separator");

    public static void main(String[] args) {
        if (csvFile.exists()) {
            convertToYaml();
            fixWithRegex();
        } else {
            System.out.println("Error: \"Minecraft Economy Manager.csv\" not found!");
        }
    }

    public static void appendLn(String text) {
        yamlTemp.append(text).append(lineSep);
    }

    public static void writeItem(String id, String name, String price) {
        System.out.println("Item [id=" + id + ", name=" + name + ", price=$" + price + "]");
        if (id.contains(";")) {
            String[] idWithType = id.split(";");
            if (idWithType[1].equals("1")) {
                appendLn("    # Array");
                appendLn("    " + idWithType[0] + ":");
            }
            appendLn("        # " + name);
            appendLn("        '" + idWithType[1] + "': " + price);
        } else {
            appendLn("    # " + name);
            appendLn("    " + id + ": " + price);
        }
    }

    public static void convertToYaml() {
        try {
            FileReader reader = new FileReader(csvFile);
            appendLn("worth:");

            for (CSVRecord record : CSVFormat.EXCEL.parse(reader)) {
                if (record.get(0).isEmpty())
                    continue;
                if (record.get(0).equals("ID"))
                    continue;

                if (record.get(3).isEmpty())
                    writeItem(record.get(0), record.get(1), "''");
                else
                    writeItem(record.get(0), record.get(1), record.get(3).replace("$", "").replace(",", "").replace("#DIV/0!", "''"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fixWithRegex() {
        Pattern replace = Pattern.compile(": (.*)"+lineSep+"    # Array"+lineSep+"    (.*)");
        if (!ymlFile.delete()) {
            System.out.println("Error deleting previous worth.yml file.");
        }

        try {
            PrintWriter writer = new PrintWriter(ymlFile, "UTF-8");
            writer.println("# Created with MEM to Essentials Worth Generator.");
            writer.println("# Copyright (c) CoolV1994.");
            writer.println("# ");
            Matcher matcher = replace.matcher(yamlTemp.toString());
            writer.println(matcher.replaceAll(":" + lineSep + "        '0': $1"));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
