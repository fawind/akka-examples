package utils;

public class CommonSubstring {

    public static String getLongestCommonSubstring(String stringA, String stringB) {
        int startIndex = 0;
        int maxLength = 0;
        for (int i = 0; i < stringA.length(); i++) {
            for (int j = 0; j < stringB.length(); j++) {
                int x = 0;
                while (stringA.charAt(i + x) == stringB.charAt(j + x)) {
                    x++;
                    if (i + x >= stringA.length() || j + x >= stringB.length()) {
                        break;
                    }
                }
                if (x > maxLength) {
                    maxLength = x;
                    startIndex = i;
                }
            }
        }
        return stringA.substring(startIndex, startIndex + maxLength);
    }
}
