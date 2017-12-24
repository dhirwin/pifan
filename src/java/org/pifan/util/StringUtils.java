package org.pifan.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.primitives.Chars;

/**
 * 
 *
 * @author Dave Irwin (david.irwin@parsons.com)
 */
public class StringUtils {
    /**
     * Define the logger.
     */
    private static Logger logger = LoggerFactory.getLogger(StringUtils.class);

    /** A table of hex digits */
    private static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
            'F' };

    /**
     * Basic list of valid characters.
     * 
     * This is a little bit easier to understand then building a possibly complicated
     * regex expression to do the same thing.
     */
    private static final String VALID_CHAR_SEQ = " '.,-_[]()@" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "abcdefghijklmnopqrstuvwxyz" + "0123456789";
    public static final ImmutableSet<Character> VALID_CHAR_SET = stringToCharSet(VALID_CHAR_SEQ);

    /**
     * 
     */
    private StringUtils() {
    }

    /**
     * 
     * @param str
     * @param defaultInt
     * @return
     */
    public static int parseInt(String str, int defaultInt) {
        if (StringUtils.isValidInteger(str)) {
            return Integer.parseInt(str);
        } else {
            return defaultInt;
        }
    }

    /**
     * 
     * @param str
     * @param defaultDbl
     * @return
     */
    public static double parseDouble(String str, double defaultDbl) {
        if (StringUtils.isValidDouble(str)) {
            return Double.parseDouble(str);
        } else {
            return defaultDbl;
        }
    }

    /**
     * The Alphanum Algorithm is an improved sorting algorithm for strings
     * containing numbers.  Instead of sorting numbers in ASCII order like
     * a standard sort, this algorithm sorts numbers in numeric order.
     *
     * The Alphanum Algorithm is discussed at http://www.DaveKoelle.com    
     *      
     * @param deviceId1
     * @param deviceId2 
     * @return
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public static int sortAlphanumericValues(String deviceId1, String deviceId2) {
        try {
            int thisMarker = 0;
            int thatMarker = 0;
            int s1Length = deviceId1.length();
            int s2Length = deviceId2.length();

            while (thisMarker < s1Length && thatMarker < s2Length) {
                String thisChunk = getChunk(deviceId1, s1Length, thisMarker);
                thisMarker += thisChunk.length();

                String thatChunk = getChunk(deviceId2, s2Length, thatMarker);
                thatMarker += thatChunk.length();

                // If both chunks contain numeric characters, sort them numerically
                int result = 0;
                if (StringUtils.isDigit(thisChunk.charAt(0)) && StringUtils.isDigit(thatChunk.charAt(0))) {
                    // Simple chunk comparison by length.
                    int thisChunkLength = thisChunk.length();
                    result = thisChunkLength - thatChunk.length();
                    // If equal, the first different number counts
                    if (result == 0) {
                        for (int i = 0; i < thisChunkLength; i++) {
                            result = thisChunk.charAt(i) - thatChunk.charAt(i);
                            if (result != 0) {
                                return result;
                            }
                        }
                    }
                } else {
                    result = thisChunk.compareTo(thatChunk);
                }

                if (result != 0) {
                    return result;
                }
            }

            return s1Length - s2Length;
        } catch (Exception ex) {
            // just compare by strings
            return deviceId1.compareToIgnoreCase(deviceId2);
        }
    }

    /**
     * Length of string is passed in for improved efficiency (only need to calculate it once).
     * 
     * @param s
     * @param slength
     * @param marker
     * @return
     */
    private static String getChunk(String s, int slength, int marker) {
        StringBuilder chunk = new StringBuilder();
        char c = s.charAt(marker);
        chunk.append(c);
        marker++;
        if (StringUtils.isDigit(c)) {
            while (marker < slength) {
                c = s.charAt(marker);
                if (!StringUtils.isDigit(c)) {
                    break;
                }
                chunk.append(c);
                marker++;
            }
        } else {
            while (marker < slength) {
                c = s.charAt(marker);
                if (StringUtils.isDigit(c)) {
                    break;
                }
                chunk.append(c);
                marker++;
            }
        }
        return chunk.toString();
    }

    /**
     * Convert a nibble to a hex character
     * @param   nibble  the nibble to convert.
     */
    private static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }

    /**
     * Check whether the string starts with any of the list of prefixes to check,
     * optionally ignoring the case.
     * 
     * @param str
     * @param prefixesToCheck
     * @param ignoreCase
     * @return
     */
    public static final boolean startsWith(String str, List<String> prefixesToCheck, boolean ignoreCase) {
        for (String prefixToCheck : prefixesToCheck) {
            if (ignoreCase) {
                if (str.toLowerCase().startsWith(prefixToCheck.toLowerCase())) {
                    return true;
                }
            } else {
                if (str.startsWith(prefixToCheck)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 
     * @param str
     * @param prefixToCheck
     * @param ignoreCase
     * @return
     */
    public static final boolean startsWith(String str, String prefixToCheck, boolean ignoreCase) {
        return startsWith(str, Arrays.asList(prefixToCheck), ignoreCase);
    }

    /**
     * 
     * @param str
     * @param suffix
     * @param excludeTrailingWhitespace
     * @return
     */
    public static final boolean endsWith(String str, String suffix, boolean excludeTrailingWhitespace) {
        if (excludeTrailingWhitespace) {
            str = str.trim();
        }

        return str.endsWith(suffix);
    }

    /**
     * Returns the occurrences of 'fintStr' in the 'str' variable.
     * 
     * @param str
     * @param findStr
     * @return
     */
    public static final int occurencesOf(String str, String findStr) {
        int lastIndex = 0;
        int count = 0;

        while (lastIndex != -1) {
            lastIndex = str.indexOf(findStr, lastIndex);

            if (lastIndex != -1) {
                count++;
                lastIndex += findStr.length();
            }
        }

        return count;
    }

    /**
     * 
     * @param str
     * @param errIndex
     * @return
     */
    public static final String identifyErrChar(String str, int errIndex) {
        return identifyErrChar(str, errIndex, 4);
    }

    /**
     * 
     * @param str
     * @param errIndex
     * @param errContextPadding
     * @return
     */
    public static final String identifyErrChar(String str, int errIndex, int errContextPadding) {
        String littleBefore = str.substring((Math.max(0, errIndex - errContextPadding)), errIndex);
        char errChar = str.charAt(errIndex);
        String littleAfter = str.substring(errIndex + 1, Math.min(errIndex + (errContextPadding + 1), str.length()));

        return littleBefore + "-->" + errChar + "<--" + littleAfter;
    }

    /**
     * 
     * @param str
     * @return
     */
    public static final String trim(String str) {
        if (StringUtils.isValidString(str)) {
            return str.trim();
        } else {
            return "";
        }
    }

    /**
     * Trim off the leading text.
     * 
     * @param str
     * @param toTrimOff
     * @return
     */
    public static final String trimLeadingText(String str, String toTrimOff) {
        return trimLeadingText(str, toTrimOff, false);
    }

    /**
     * 
     * @param str
     * @param toTrimOff
     * @param ignoreCase
     * @return
     */
    public static final String trimLeadingText(String str, String toTrimOff, boolean ignoreCase) {
        if (StringUtils.isValidString(toTrimOff)
                && ((ignoreCase) ? str.toLowerCase().startsWith(toTrimOff.toLowerCase()) : str.startsWith(toTrimOff))) {
            str = str.substring(toTrimOff.length());
        }
        return str;
    }

    /**
     * 
     * @param stringToFormat
     * @param maxLength
     * @return
     */
    public static final String trimLeadingToLength(String stringToFormat, int maxLength) {
        if (stringToFormat == null) {
            return "";
        }
        if (stringToFormat.length() < maxLength) {
            return stringToFormat;
        }

        int currLength = stringToFormat.length();
        int startIndex = currLength - maxLength;

        return stringToFormat.substring(startIndex);
    }

    /**
     * 
     * @param txt
     * @param toTrimOff
     * @return
     */
    public static final String trimTrailingText(String str, String toTrimOff) {
        if (str.endsWith(toTrimOff)) {
            str = str.substring(0, str.length() - toTrimOff.length());
        }
        return str;
    }

    /**
     * 
     * @param stringToFormat
     * @param maxLength
     * @param includeTrailingDots
     * @return
     */
    public static final String formatToLength(String stringToFormat, int maxLength, boolean includeTrailingDots) {
        if (stringToFormat == null) {
            return "";
        } else if (stringToFormat.length() < maxLength) {
            return stringToFormat;
        }

        if (includeTrailingDots) {
            String dots = "...";
            return stringToFormat.substring(0, maxLength - dots.length()) + dots;
        } else {
            return stringToFormat.substring(0, maxLength);
        }
    }

    /**
     * 
     * @param stringToFormat
     * @param maxLength
     * @return
     */
    public static final String formatToLength(String stringToFormat, int maxLength) {
        return formatToLength(stringToFormat, maxLength, true);
    }

    /**
     * 
     * @param objToFormat
     * @param maxLength
     * @return
     */
    public static final String formatToLength(Object objToFormat, int maxLength) {
        return formatToLength((objToFormat == null ? "N/A" : objToFormat.toString()), maxLength, true);
    }

    /**
     * Format the given string by inserting the given text every maxLineLength
     * characters.
     * 
     * @param stringToFormat
     *            The string to format
     * @param stringToInsert
     *            The string to insert every maxLineLength characters
     * @param maxLineLength
     * @return The formatted string
     */
    public static final String wrapToLength(String stringToFormat, String stringToInsert, int maxLineLength) {
        if (stringToFormat == null) {
            return "";
        }

        final StringBuilder currentText = new StringBuilder(stringToFormat);
        final StringBuilder formattedText = new StringBuilder();

        for (int i = 0, counter = 0; i < currentText.length(); i++, counter++) {
            if (counter == maxLineLength) {
                // look for the next space to insert the necessary text
                int nextSpace = StringUtils.findNextSpace(currentText.substring(i));

                if (nextSpace >= 0) {
                    // a space was found
                    formattedText.append(currentText.substring(i, i + nextSpace));
                    formattedText.append(stringToInsert);

                    i = i + nextSpace;
                    counter = 0;
                } else {
                    // a space couldn't be found...simply add all of the text
                    formattedText.append(currentText.substring(i));
                    formattedText.append(stringToInsert);

                    i = i + currentText.substring(i).length();
                    counter = 0;
                }
            } else {
                formattedText.append(currentText.charAt(i));
            }
        }

        return formattedText.toString();
    }

    /**
     * This method will mask the 'stringToMask' starting text with the 'maskingChar'.
     * 
     * @param stringToMask
     * @param maxTrailingCharsToShow
     * @return
     */
    public static final String maskStartingText(String stringToMask, char maskingChar, int maxTrailingCharsToShow) {
        if (!StringUtils.isValidString(stringToMask)) {
            return stringToMask;
        }

        int length = stringToMask.length();
        if (maxTrailingCharsToShow >= length) {
            return stringToMask.replaceAll(".", String.valueOf(maskingChar));
        } else {
            int endMaskIndex = length - maxTrailingCharsToShow;
            String maskedStr = StringUtils.fillWithCharacter(endMaskIndex, maskingChar);

            String nonMaskedStr = stringToMask.substring(endMaskIndex);

            return maskedStr + nonMaskedStr;
        }
    }

    /**
     * Format the given string by inserting the given text every maxLineLength
     * characters.
     * 
     * @param stringToFormat
     *            The string to format
     * @param maxLineLength
     * @return The list of formatted strings.
     */
    public static final List<String> wrapToLength(String stringToFormat, int maxLineLength) {
        List<String> ret = new ArrayList<>();

        if (stringToFormat == null) {
            return ret;
        }

        final StringBuilder currentText = new StringBuilder(stringToFormat);
        final StringBuilder formattedText = new StringBuilder();

        for (int i = 0, counter = 0; i < currentText.length(); i++, counter++) {
            if (counter == maxLineLength) {
                // look for the next space to insert the necessary text
                int nextSpace = StringUtils.findNextSpace(currentText.substring(i));

                if (nextSpace >= 0) {
                    // a space was found
                    formattedText.append(currentText.substring(i, i + nextSpace));

                    ret.add(formattedText.toString());
                    formattedText.setLength(0);

                    i = i + nextSpace;
                    counter = 0;
                } else {
                    // a space couldn't be found...simply add all of the text
                    formattedText.append(currentText.substring(i));

                    ret.add(formattedText.toString());
                    formattedText.setLength(0);

                    i = i + currentText.substring(i).length();
                    counter = 0;
                }
            } else {
                formattedText.append(currentText.charAt(i));
            }
        }

        if (formattedText.length() > 0) {
            ret.add(formattedText.toString());
        }

        return ret;
    }

    /**
     * 
     * @param lines
     * @return
     */
    public static final int maxStringLength(List<String> lines) {
        int ret = 0;
        for (String line : lines) {
            if (line.length() > ret) {
                ret = line.length();
            }
        }

        return ret;
    }

    /**
     * Extracts everything inside of the beginning and ending delimiter.
     * 
     * @param fullStr
     * @param beginDelim
     * @param endDelim
     * @return
     */
    public static final String extractInside(String fullStr, String beginDelim, String endDelim) {
        if (fullStr == null || fullStr.trim().length() == 0) {
            return fullStr;
        }

        int beginIndex = fullStr.lastIndexOf(beginDelim);
        int endIndex = fullStr.lastIndexOf(endDelim);

        if (beginIndex == -1 && endIndex == -1) {
            return fullStr;
        }

        return fullStr.substring(beginIndex + 1, endIndex);
    }

    /**
     * Extracts everything outside of the beginning and ending delimiter.
     * 
     * @param fullStr
     * @param beginDelim
     * @param endDelim
     * @return
     */
    public static final String extractOutside(String fullStr, String beginDelim, String endDelim) {
        if (fullStr == null || fullStr.trim().length() == 0) {
            return fullStr;
        }

        int beginIndex = fullStr.lastIndexOf(beginDelim);
        int endIndex = fullStr.lastIndexOf(endDelim);

        if (beginIndex == -1 && endIndex == -1) {
            return fullStr;
        }

        StringBuilder buf = new StringBuilder();

        buf.append(fullStr.substring(0, beginIndex));
        if (endIndex < (fullStr.length())) {
            buf.append(fullStr.substring(endIndex + 1, fullStr.length()));
        }

        return buf.toString().trim();
    }

    /**
     * 
     * @param line
     * @param delimiter
     * @return
     */
    public static final String[] splitIgnoringQuoted(String line, String delimiter) {
        String otherThanQuote = " [^\"] ";
        String quotedString = String.format(" \" %s* \" ", otherThanQuote);
        String regex = String.format("(?x) " + // enable comments, ignore white spaces
                delimiter + "              " + // match the delimiter
                "(?=                       " + // start positive look ahead
                "  (                       " + //   start group 1
                "    %s*                   " + //     match 'otherThanQuote' zero or more times
                "    %s                    " + //     match 'quotedString'
                "  )*                      " + //   end group 1 and repeat it zero or more times
                "  %s*                     " + //   match 'otherThanQuote'
                "  $                       " + // match the end of the string
                ")                         ", // stop positive look ahead
                otherThanQuote, quotedString, otherThanQuote);

        // yikes...this is probably not the right way to remove a quote
        List<String> ret = new ArrayList<>();
        for (String str : line.split(regex)) {
            if (str.startsWith("\"")) {
                str = str.substring(1, str.length());
            }
            if (str.endsWith("\"")) {
                str = str.substring(0, str.length() - 1);
            }

            ret.add(str);
        }

        return ret.toArray(new String[0]);
    }

    /**
     * 
     * @param fullStr
     * @param delimiter
     * @return
     */
    public static final String lastToken(String fullStr, String delimiter) {
        try {
            if (fullStr.contains(delimiter)) {
                return fullStr.substring(fullStr.lastIndexOf(delimiter) + 1);
            } else {
                return fullStr;
            }
        } catch (Exception e) {
            // if anything fails we simply return the original full string
            return fullStr;
        }
    }

    /**
     * Get the first letter of the string.
     * 
     * @param str
     * @return
     */
    public static final String firstLetter(String str) {
        if (str == null || str.trim().length() == 0) {
            return "";
        } else {
            return Character.toString(str.charAt(0));
        }
    }

    /**
     * Get the last letter of the string.
     * 
     * @param str
     * @return
     */
    public static final String lastLetter(String str) {
        if (str == null || str.trim().length() == 0) {
            return "";
        } else {
            return Character.toString(str.charAt(str.trim().length() - 1));
        }
    }

    /**
     * 
     * @param str
     * @return
     */
    public static final boolean isValidString(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 
     * @param str
     * @param notEqualTo
     * @return
     */
    public static final boolean isValidString(String str, Collection<String> notEqualTo) {
        if (str == null || str.trim().length() == 0) {
            return false;
        } else {
            return !notEqualTo.contains(str);
        }
    }

    /**
     * 
     * @param str
     * @return
     */
    public static final boolean isValidInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * 
     * @param str
     * @return
     */
    public static final boolean isValidDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * 
     * @param validChars
     * @return
     */
    private static ImmutableSet<Character> stringToCharSet(String validChars) {
        return ImmutableSet.copyOf(Chars.asList(validChars.toCharArray()));
    }

    /**
     * Check if the string contains any invalid characters.
     * 
     * @param str
     * @return
     */
    public static final InvalidCharacters containsInvalidCharacters(String str, ImmutableSet<Character> validCharSet) {
        return new InvalidCharacters(Sets.difference(stringToCharSet(str), validCharSet).immutableCopy());
    }

    /**
     * 
     */
    public static class InvalidCharacters {
        private final ImmutableSet<Character> diffs;

        private InvalidCharacters(ImmutableSet<Character> diffs) {
            super();

            this.diffs = diffs;
        }

        public final boolean contains() {
            return !this.diffs.isEmpty();
        }

        public final ImmutableSet<Character> getDiffs() {
            return this.diffs;
        }
    }

    /**
     * 
     * @param str
     * @param replacementStr
     * @return
     */
    public static final String replaceAllInvalidCharacters(String str, ImmutableSet<Character> diffs,
            Character replacement) {
        for (Character character : diffs) {
            str = str.replace(character.charValue(), replacement);
        }

        return str;
    }

    /**
     * 
     * @param chars
     * @return
     */
    public static String printChars(Iterable<Character> chars) {
        return Joiner.on(", ").join(chars);
    }

    /**
     * 
     * @param strs
     * @return
     */
    public static final String firstNonNull(String[] strs) {
        for (String str : strs) {
            if (str != null) {
                return str;
            }
        }

        return null;
    }

    /**
     * 
     * @param fullStr
     * @param wordToReplace
     * @param textToReplace
     * @return
     */
    public static final String replaceAllWords(String fullStr, String wordToReplace, String textToReplace) {
        if (isValidInteger(wordToReplace)) {
            // we're dealing with a number...case doesn't matter
            return StringUtils.replaceAll(fullStr, wordToReplace, textToReplace, false);
        } else {
            // we're dealing with a word
            return StringUtils.replaceAll(fullStr, " " + wordToReplace + " ", " " + textToReplace + " ", true);
        }
    }

    /**
     * 
     * @param fullStr
     * @param strToReplace
     * @param replacementStr
     * @param ignoreCase
     * @return
     */
    public static final String replaceAll(String fullStr, String strToReplace, String replacementStr,
            boolean ignoreCase) {
        if (ignoreCase) {
            // embed the case insensitive flag in the regex
            return fullStr.replaceAll("(?i)" + strToReplace, replacementStr);
        } else {
            return fullStr.replaceAll(strToReplace, replacementStr);
        }
    }

    /**
     * 
     * @param string
     * @param toReplace
     * @param replacement
     * @return
     */
    public static final String replaceLast(String string, String toReplace, String replacement) {
        int lastIndex = string.lastIndexOf(toReplace);
        if (lastIndex < 0) {
            return string;
        }

        String tail = string.substring(lastIndex).replaceFirst("[" + toReplace + "]", replacement);
        return string.substring(0, lastIndex) + tail;
    }

    /**
     * 
     * @param str
     * @return
     */
    public static final String lowercaseFirstLetter(String str) {
        if (str == null) {
            return "";
        }

        return (str.length() > 0) ? Character.toLowerCase(str.charAt(0)) + str.substring(1) : str;
    }

    /**
     * Find the next space in the given string.
     * 
     * @param str The string to search in for the next space
     * @return The index of the first space found, -1 of no space is found
     */
    public static final int findNextSpace(String str) {
        if (str == null) {
            return -1;
        }

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ' ') {
                return i;
            }
        }

        return -1;
    }

    /**
     * Capitalizes just the first word in the string.
     * 
     * @param str
     * @return
     */
    public static final String capitalizeFirstLetter(String str) {
        if (str == null) {
            return "";
        }

        // first make everything lower case
        str = str.toLowerCase();

        // then just capitalize the first letter
        return (str.length() > 0) ? Character.toUpperCase(str.charAt(0)) + str.substring(1) : str;
    }

    /**
     * This method will capitalize all of the individual words in a sentence.
     * 
     * @param str
     * @param ignoreList
     * @return
     */
    public static final String capitalizeWords(String str) {
        if (!StringUtils.isValidString(str)) {
            return "";
        }

        // trim everything
        str = str.trim();

        // first make everything lower case
        str = str.toLowerCase();

        // capitalize the first letter
        str = (str.length() > 0) ? Character.toUpperCase(str.charAt(0)) + str.substring(1) : str;

        // try to capitalize any remaining words
        boolean shouldCap = false;
        for (int i = 1; i < str.length(); i++) {
            if (str.charAt(i) == ' ') {
                shouldCap = true;
            } else {
                if (shouldCap) {
                    str = str.substring(0, i) + Character.toUpperCase(str.charAt(i)) + str.substring(i + 1);

                    shouldCap = false;
                }
            }
        }

        return str;
    }

    /**
     * 
     * @param str
     * @param caps
     * @return
     */
    public static String capitalize(String str, boolean caps) {
        if (str == null) {
            return "";
        }

        return caps ? str.toUpperCase() : str;
    }

    /**
     * 
     * @param msg
     * @param indentSize
     * @return
     */
    public static final String indentText(String msg, int indentSize) {
        if (msg == null) {
            return "";
        }

        char[] array = new char[indentSize + msg.length()];
        int j = 0;
        for (int i = 0; i < array.length; i++) {
            if (i < indentSize) {
                array[i] = ' ';
            } else {
                array[i] = msg.charAt(j++);
            }
        }
        return new String(array);
    }

    /**
     * @param msgToPad
     * @param totalWidth
     * @return
     */
    public static final String padText(String msgToPad, int totalWidth) {
        if (msgToPad == null) {
            return "";
        }

        int vLength = msgToPad.length();
        if (vLength > totalWidth) {
            // we don't pad if the length is already too long
            return msgToPad;
        }

        int toPad = totalWidth - vLength;

        return msgToPad + StringUtils.fillWithCharacter(toPad, ' ');
    }

    /**
     * Pad the given text and then append the trailing text to the end of the message.
     * The trailing text is not included in the padding calculation.
     * 
     * @param msgToPad
     * @param totalWidth
     * @param trailingText
     * @return
     */
    public static final String padText(String msgToPad, int totalWidth, String trailingText) {
        StringBuilder buf = new StringBuilder();
        return buf.append(padText(msgToPad, totalWidth)).append(trailingText).toString();
    }

    /**
     * Prepend the number of given spaces, then pad the given text and then append the
     * trailing text to the end of the message. The preSpaces are included in the padding
     * calculation, the trailing text is not included in the padding calculation.
     * 
     * @param msgToPad
     * @param preSpaces
     * @param totalWidth
     * @param trailingText
     * @return
     */
    public static final String padText(String msgToPad, int preSpaces, int totalWidth, String trailingText) {
        StringBuilder buf = new StringBuilder();
        return buf.append(padText(StringUtils.fillWithCharacter(preSpaces, ' ') + msgToPad, totalWidth))
                .append(trailingText).toString();
    }

    /**
     * @param length
     * @param charToFill
     * @return
     */
    public static final String fillWithCharacter(int length, char charToFill) {
        if (length > 0) {
            char[] array = new char[length];
            Arrays.fill(array, charToFill);
            return new String(array);
        }

        return "";
    }

    /**
     * 
     * @param fullWidth
     * @param left
     * @param center
     * @param right
     * @return
     */
    public static final String fillWithCharacterLeftJustified(int fullWidth, String left, String center, String right) {
        // make sure that the full width is an even # of characters
        if (fullWidth % 2 != 0) {
            fullWidth++;
        }

        int numChars = left.length() + center.length() + right.length();

        if (numChars % 2 == 0) {
            // even # of characters
        } else {
            // odd # of characters...add a space to the center text
            center = center + " ";
            numChars++;
        }

        int whiteSpace = fullWidth - numChars;

        StringBuilder ret = new StringBuilder();
        char[] blanks = new char[whiteSpace];
        Arrays.fill(blanks, ' ');

        ret.append(left);
        ret.append(center);
        ret.append(new String(blanks));
        ret.append(right);

        return ret.toString();
    }

    /**
     * @param fullWidth
     * @param left
     * @param center
     * @param right
     * @return
     */
    public static final String fillWithCharacterCenterJustified(int fullWidth, String left, String center,
            String right) {
        // make sure that the full width is an even # of characters
        if (fullWidth % 2 != 0) {
            fullWidth++;
        }

        int numChars = left.length() + center.length() + right.length();

        if (numChars % 2 == 0) {
            // even # of characters
        } else {
            // odd # of characters...add a space to the center text
            center = center + " ";
            numChars++;
        }

        int whiteSpace = (fullWidth - numChars) / 2;

        StringBuilder ret = new StringBuilder();
        char[] blanks = new char[whiteSpace];
        Arrays.fill(blanks, ' ');

        ret.append(left);
        ret.append(new String(blanks));
        ret.append(center);
        ret.append(new String(blanks));
        ret.append(right);

        return ret.toString();
    }

    /**
     * Pre-pend a period to the <tt>keySuffix</tt> if it doesn't already start
     * with one.
     * 
     * This method will return a blank string if the <tt>keySuffix</tt> object
     * is null.
     * 
     * @param keySuffix
     * @return
     */
    public static final String prePendPeriod(String keySuffix) {
        if (keySuffix == null) {
            logger.warn("Can't pre-pend a period to a null keySuffix...returning blank string");
            return "";
        }

        keySuffix = keySuffix.trim();

        if (!keySuffix.startsWith(".")) {
            keySuffix = "." + keySuffix;
        }

        return keySuffix;
    }

    /**
     * Converts unicodes to encoded &#92;uxxxx and escapes
     * special characters with a preceding slash
     * 
     * @param theString
     * @param escapeSpace
     * @return
     */
    public static final String saveConvert(String theString, boolean escapeSpace) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuilder sb = new StringBuilder(bufLen);

        for (int x = 0; x < len; x++) {
            char aChar = theString.charAt(x);
            // Handle common case first, selecting largest block that
            // avoids the specials below
            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    sb.append('\\');
                    sb.append('\\');
                    continue;
                }
                sb.append(aChar);
                continue;
            }
            switch (aChar) {
            case ' ':
                if (x == 0 || escapeSpace) {
                    sb.append('\\');
                }
                sb.append(' ');
                break;
            case '\t':
                sb.append('\\');
                sb.append('t');
                break;
            case '\n':
                sb.append('\\');
                sb.append('n');
                break;
            case '\r':
                sb.append('\\');
                sb.append('r');
                break;
            case '\f':
                sb.append('\\');
                sb.append('f');
                break;
            case '=': // Fall through
            case ':': // Fall through
            case '#': // Fall through
            case '!':
                sb.append('\\');
                sb.append(aChar);
                break;
            default:
                if ((aChar < 0x0020) || (aChar > 0x007e)) {
                    sb.append('\\');
                    sb.append('u');
                    sb.append(toHex((aChar >> 12) & 0xF));
                    sb.append(toHex((aChar >> 8) & 0xF));
                    sb.append(toHex((aChar >> 4) & 0xF));
                    sb.append(toHex(aChar & 0xF));
                } else {
                    sb.append(aChar);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 
     * @param searchStr
     * @param strs
     * @return
     */
    public static final boolean containsIgnoreCase(String searchStr, String[] strs) {
        for (String str : strs) {
            if (str.equalsIgnoreCase(searchStr)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether two strings are equal, ignoring case.
     * 
     * @param str1
     * @param str2
     * @return
     */
    public static final boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null) {
            return false;
        } else if (str2 == null) {
            return false;
        } else {
            return str1.equalsIgnoreCase(str2);
        }
    }

    /**
     * Checks whether the given character is an ASCII digit.
     * 
     * @param ch
     * @return
     */
    public static boolean isDigit(char ch) {
        return ch >= 48 && ch <= 57;
    }

    /**
     * Index of first found digit. This method will return -1 if no digits are found.
     *
     * @param str the str
     * @return the int
     */
    public static int indexOfDigits(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Whether the string contains at least one digit.
     * 
     * @param str
     * @return
     */
    public static boolean containsAtLeastOneDigit(String str) {
        return indexOfDigits(str) >= 0;
    }

    /**
     * Index of the first found letter. This method will return -1 if no letters are found.
     * 
     * @param str
     * @return
     */
    public static int indexOfChar(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (Character.isLetter(str.charAt(i))) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Whether the string contains at least one character.
     * 
     * @param str
     * @return
     */
    public static boolean containsAtLeastOneChar(String str) {
        return indexOfChar(str) >= 0;
    }

    /**
     * 
     * @param searchStr
     * @param strs
     * @return
     */
    public static final boolean contains(String searchStr, String[] strs) {
        for (String str : strs) {
            if (str.equals(searchStr)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 
     * @param str
     * @return
     */
    public static final List<String> toList(String str) {
        return toList(str, ",");
    }

    /**
     * 
     * @param str
     * @param delimiter
     * @return
     */
    public static final List<String> toList(String str, String delimiter) {
        if (!StringUtils.isValidString(str)) {
            return new ArrayList<>();
        }

        str = str.trim();
        String[] parts = str.split(delimiter);
        return Arrays.asList(parts);
    }

    /**
     * 
     * @param lines
     * @param delimiter
     * @return
     */
    public static final Map<String, String> toMap(List<String> lines, String delimiter) {
        Map<String, String> ret = new HashMap<>();

        for (String line : lines) {
            if (!StringUtils.isValidString(line)) {
                continue;
            }

            String[] parts = line.split(delimiter, 2);

            String key = "";
            String val = "";
            if (parts.length > 0) {
                key = parts[0];
            }
            if (parts.length > 1) {
                val = parts[1];
            }

            ret.put(key, val);
        }

        return ret;
    }

    /**
     * 
     * @param lines
     * @param delimiter
     * @return
     */
    public static final Map<String, String> toMap(String[] lines, String delimiter) {
        return toMap(Arrays.asList(lines), delimiter);
    }

    /**
     * Convert an Object into its string representation.
     * <p>
     * If the object is null this method will simply return null.
     * 
     * @param obj
     * @return
     */
    public static final String toString(Object obj) {
        if (obj == null) {
            return null;
        } else {
            return obj.toString();
        }
    }
}