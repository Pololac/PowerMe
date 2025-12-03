package com.powerme.utils;

public class Sanitizer {
    /**
     * Sanitize les inputs du user en remplaçant les caractères de nouvelle ligne (\n)
     * et de retour chariot (\r) par des underscores (_).
     *
     * @param input input du user
     * @return L'input sanitizé
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        // Remplacer les nouvelles lignes et les retours chariot par des underscores
        return input.replaceAll("[\n\r]", "_");
    }
}
