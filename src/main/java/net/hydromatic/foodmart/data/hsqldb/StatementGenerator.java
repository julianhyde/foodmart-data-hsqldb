/*
 * Licensed to Julian Hyde under one or more contributor license
 * agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership. Julian Hyde
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hydromatic.foodmart.data.hsqldb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterator that generates INSERT statements from a single CSV file. Returns
 * false from hasNext() when the CSV file is exhausted.
 */
class StatementGenerator implements Iterator<String> {
  private final String tableName;
  private final BufferedReader reader;
  private final List<String> columnNames;
  private final List<Integer> columnTypes;
  private String nextStatement;

  StatementGenerator(
      Class<?> resourceClass, String tableName, List<Integer> columnTypes) {
    this.tableName = tableName;
    this.columnTypes = columnTypes;
    String csvPath = "/csv/" + tableName.toLowerCase() + ".csv";
    try {
      InputStream is = resourceClass.getResourceAsStream(csvPath);
      if (is == null) {
        throw new RuntimeException("CSV file not found: " + csvPath);
      }
      this.reader = new BufferedReader(new InputStreamReader(is));

      // Read header line
      String headerLine = reader.readLine();
      this.columnNames = parseCsvLine(headerLine);

      // Read first data line
      advance();
    } catch (IOException e) {
      throw new RuntimeException("Failed to read CSV file: " + csvPath, e);
    }
  }

  @Override
  public boolean hasNext() {
    return nextStatement != null;
  }

  @Override
  public String next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    String result = nextStatement;
    advance();
    return result;
  }

  private void advance() {
    nextStatement = null;
    try {
      String line;
      while ((line = reader.readLine()) != null) {
        if (!line.trim().isEmpty()) {
          nextStatement = generateInsertStatement(line);
          return;
        }
      }
      // EOF reached, close reader
      reader.close();
    } catch (IOException e) {
      closeReader();
      throw new RuntimeException("Error reading CSV file", e);
    }
  }

  /** Generates a single INSERT statement from a CSV line. */
  private String generateInsertStatement(String line) {
    final StringBuilder b = new StringBuilder("INSERT INTO \"");
    b.append(tableName).append("\" VALUES(");

    final List<String> values = parseCsvLine(line);
    for (int i = 0; i < values.size(); i++) {
      if (i > 0) {
        b.append(",");
      }
      String value = values.get(i);
      int type = columnTypes.get(i);
      if (value.isEmpty()) {
        b.append("NULL");
      } else {
        switch (type) {
          case Types.VARCHAR:
          case Types.CHAR:
          case Types.LONGVARCHAR:
          case Types.DATE:
          case Types.TIMESTAMP:
            b.append("'").append(value.replace("'", "''")).append("'");
            break;
          case Types.BOOLEAN:
            b.append(value.equalsIgnoreCase("TRUE") ? "TRUE" : "FALSE");
            break;
          default:
            // Numeric types
            b.append(value);
            break;
        }
      }
    }
    b.append(")");
    return b.toString();
  }

  private void closeReader() {
    try {
      reader.close();
    } catch (IOException e) {
      // Ignore close errors
    }
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  /** Parses a CSV line, handling quoted values. */
  private static List<String> parseCsvLine(String line) {
    final List<String> values = new ArrayList<>();
    final StringBuilder current = new StringBuilder();
    boolean inQuotes = false;

    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);

      if (c == '"') {
        inQuotes = !inQuotes;
      } else if (c == ',' && !inQuotes) {
        values.add(current.toString());
        current.setLength(0);
      } else {
        current.append(c);
      }
    }
    values.add(current.toString());

    return values;
  }
}

// End StatementGenerator.java
