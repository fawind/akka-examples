package utils;

import com.google.common.base.Strings;

import java.util.Iterator;

public class PasswordRange implements Iterator<String> {

    private final static int PASSWORD_SIZE = 7;

    private final int startNumber;
    private final int endNumber;
    private String current;

    public PasswordRange(int startNumber, int endNumber) {
        this.startNumber = startNumber;
        this.endNumber = endNumber;
        this.current = numberToString(startNumber);
    }

    @Override
    public boolean hasNext() {
        return Integer.valueOf(current) <= endNumber;
    }

    @Override
    public String next() {
        String value = current;
        int nextNumber = Integer.valueOf(current) + 1;
        current = numberToString(nextNumber);
        return value;
    }

    private String numberToString(int number) {
        return Strings.padStart(String.valueOf(number), PASSWORD_SIZE, '0');
    }
}
