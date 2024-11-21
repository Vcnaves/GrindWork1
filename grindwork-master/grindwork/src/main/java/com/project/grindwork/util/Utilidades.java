package com.project.grindwork.util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Utilidades {

  public static Date converterStringToDate(String data) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate localDate = LocalDate.parse(data, formatter);
    return java.sql.Date.valueOf(localDate);
  }

  public static Timestamp converterStringToTimestamp(String data) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
      "dd/MM/yyyy HH:mm"
    );
    LocalDateTime localDateTime = LocalDateTime.parse(data, formatter);
    return Timestamp.valueOf(localDateTime);
  }

  public static String capitalizeWords(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }

    String[] words = input.split(" ");
    StringBuilder capitalizedWords = new StringBuilder();

    for (String word : words) {
      if (word.length() > 0) {
        String capitalizedWord =
          word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
        capitalizedWords.append(capitalizedWord).append(" ");
      }
    }

    return capitalizedWords.toString().trim();
  }
}
