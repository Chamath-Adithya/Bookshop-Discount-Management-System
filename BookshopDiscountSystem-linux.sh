#!/bin/bash

# Bookshop Discount Management System Linux Executable
# This script runs the JavaFX application

# Get the directory of this script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Path to the JAR file (assume it's in the same directory as this script)
JAR_FILE="${SCRIPT_DIR}/target/BookshopDiscountSystem-1.0-SNAPSHOT.jar"

# Check if JAR file exists
if [ ! -f "$JAR_FILE" ]; then
    echo "Error: JAR file not found at $JAR_FILE"
    echo "Please make sure the project is built and the JAR file exists."
    exit 1
fi

# Run the Java application
java -jar "$JAR_FILE"

# Check if java command succeeded
if [ $? -ne 0 ]; then
    echo "Failed to run the application. Please ensure JavaFX and Java 11+ are installed."
    exit 1
fi
