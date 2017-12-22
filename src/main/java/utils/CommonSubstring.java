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

    public static String getLongestCommonSubstringDP(String stringA, String stringB) {
        int m = stringA.length();
        int n = stringB.length();
        int longestSuffix[][] = new int[m + 1][n + 1];
        int maxLength = 0;
        String longestSubstring = "";

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0 || j == 0) {
                    longestSuffix[i][j] = 0;
                }
                else {
                    if (stringA.charAt(i - 1) == stringB.charAt(j - 1)) {
                        longestSuffix[i][j] = longestSuffix[i - 1][j - 1] + 1;
                        if (longestSuffix[i][j] > maxLength) {
                            maxLength = longestSuffix[i][j];
                            longestSubstring = stringA.substring(i - maxLength + 1, i + 1);
                        }
                    } else {
                        longestSuffix[i][j] = 0;
                    }
                }
            }
        }

        return longestSubstring;
    }
}
