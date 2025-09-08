package com.automation.data.payment.classifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class CollectData {
    public static void main(String[] args) throws FileNotFoundException, IOException {
    	
    	/* 
    	 * Here, the program loads settings from application.properties. 
    	 * The Properties object retrieves three values: 
    	 * - a regex for employee details 
    	 * - a regex for society names 
    	 * - a CSV header definition. 
    	 * This makes the program configurable — we can modify regex rules or CSV headers without touching our code.
    	 * */
    	Properties props = new Properties();
    	try (FileInputStream fis = new FileInputStream("application.properties")) {
    	    props.load(fis);
    	}

    	String employeeRegex = props.getProperty("employee.pattern");
    	String societyRegex = props.getProperty("society.pattern");
    	String csvHeader = props.getProperty("csv.header");
    	
    	
    	/*
    	 * This part of code points to a directory and retrieves all files ending in .pdf. 
    	 * If no PDFs are found, it prints a message and exits. 
    	 * This ensures that the rest of the program only runs if there are files to process.
    	 * */
        File folder = new File("C:\\Users\\touhafi\\Downloads\\Data\\attestations");
        File[] pdfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        if (pdfFiles == null || pdfFiles.length == 0) {
            System.out.println("No PDF files found in folder.");
            return;
        }
        
        
        
        /* The regex expressions loaded from the properties file are compiled into Pattern objects. */
        Pattern employee_pattern = Pattern.compile(employeeRegex);
        Pattern society_pattern = Pattern.compile(societyRegex);

        try (PrintWriter writer = new PrintWriter("output_data.csv")) {
        	/*
        	 * This section creates a PrintWriter to generate output_data.csv. 
        	 * The first row of the CSV file is written using the header defined in the properties file. 
        	 * This ensures the output has clear column names before the extracted data is appended.
        	 * */
            writer.println(csvHeader);

            
            
            /* This is the main processing loop. 
             * Each PDF is opened with PDDocument, its text is extracted, and then split into lines. 
             * Each line is checked against the employee and society regex patterns. 
             * If a line matches an employee, the desired values are captured using regex groups and written 
             * into the CSV alongside the current society name and filename. 
             * If a line matches a society, that society name is stored and reused for all following employee entries. 
             * This ensures that employees are tied to the correct company.
             * */
            for (File file : pdfFiles) {
                try (PDDocument document = PDDocument.load(file)) {
                    PDFTextStripper pdfStripper = new PDFTextStripper();
                    String text = pdfStripper.getText(document);

                    String society_name = "";

                    for (String line : text.split("\n")) {
                        line = line.trim();
                        Matcher employee_matcher = employee_pattern.matcher(line);
                        Matcher society_matcher = society_pattern.matcher(line);

                        if (employee_matcher.matches()) {
                            String val1 = employee_matcher.group(1);
                            String val2 = employee_matcher.group(2).trim();
                            String val3 = employee_matcher.group(4).replace(" ", "");
                            writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                                    society_name, val1, val2, val3, file.getName());
                        } else if (society_matcher.matches()) {
                            society_name = society_matcher.group(1).strip();
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Skipping damaged PDF: " + file.getName());
                    continue;
                }
            }
            
            
            
        /* This outer try-with-resources ensures the PrintWriter is closed automatically. 
         * If there’s a problem writing the CSV, the program logs the stack trace. 
         * Finally, once all files are processed, a success message is printed to let the user 
         * know the data extraction is complete.
         * */
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Extraction finished. Data saved to output.csv");
    }
}
