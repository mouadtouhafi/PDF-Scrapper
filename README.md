# 🛡️ PDF Data Extractor

[![Java](https://img.shields.io/badge/Java-17-orange?logo=java)](https://www.java.com/)
[![Apache PDFBox](https://img.shields.io/badge/Apache%20PDFBox-2.0-blue?logo=apache)](https://pdfbox.apache.org/)
[![CSV](https://img.shields.io/badge/CSV-output-lightgrey)]()

## Overview
This Java application extracts structured data from PDF files and saves it into a CSV file. It supports configurable regex patterns and CSV headers through a properties file, making it easy to adapt to different PDF formats.

## Features
- 📄 Automatically scans a folder for PDF files.
- 📝 Extracts data information using regex patterns defined in `application.properties`.
- 🏢 Associates each row data with their respective company name.
- 💾 Outputs results into a clean CSV file with configurable headers.
- ⚠️ Skips damaged PDFs and logs errors without stopping the process.

## Prerequisites
- Java 17 or higher
- Apache PDFBox 2.0 or higher
- A folder containing PDF files
