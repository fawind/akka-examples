package remote.messages;

import java.io.Serializable;
import java.util.Objects;

public class PasswordValidationMessage implements Serializable {
    private final int startNumber;
    private final int endNumber;
    private final String passwordHash;

    public PasswordValidationMessage(int startNumber, int endNumber, String passwordHash) {
        this.startNumber = startNumber;
        this.endNumber = endNumber;
        this.passwordHash = passwordHash;
    }

    public int getStartNumber() {
        return startNumber;
    }

    public int getEndNumber() {
        return endNumber;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordValidationMessage that = (PasswordValidationMessage) o;
        return startNumber == that.startNumber &&
                endNumber == that.endNumber &&
                Objects.equals(passwordHash, that.passwordHash);
    }

    @Override
    public int hashCode() {

        return Objects.hash(startNumber, endNumber, passwordHash);
    }

    @Override
    public String toString() {
        return "PasswordValidationMessage{" +
                "startNumber=" + startNumber +
                ", endNumber=" + endNumber +
                '}';
    }
}
