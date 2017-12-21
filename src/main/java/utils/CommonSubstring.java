package utils;

public class CommonSubstring {

    public static String getLongestCommonSubstring(String stringA, String stringB) {
        int startIndex = 0;
        int maxLength = 0;
        for (int i = 0; i < stringA.length(); i++) {
            for (int j = 0; j < stringB.length(); j++) {
                int offset = 0;
                while (stringA.charAt(i + offset) == stringB.charAt(j + offset)) {
                    offset++;
                    if (i + offset >= stringA.length() || j + offset >= stringB.length()) {
                        break;
                    }
                }
                if (offset > maxLength) {
                    maxLength = offset;
                    startIndex = i;
                }
            }
        }
        return stringA.substring(startIndex, startIndex + maxLength);
    }
}
