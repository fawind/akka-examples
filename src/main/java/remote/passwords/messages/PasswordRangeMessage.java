package remote.passwords.messages;

import com.google.common.collect.ImmutableSet;

import java.io.Serializable;
import java.util.Objects;

public class PasswordRangeMessage implements Serializable {

    private final int startNumber;
    private final int endNumber;
    private final ImmutableSet<String> passwordHashes;

    public PasswordRangeMessage(int startNumber, int endNumber, ImmutableSet<String> passwordHashes) {
        this.startNumber = startNumber;
        this.endNumber = endNumber;
        this.passwordHashes = passwordHashes;
    }

    public int getStartNumber() {
        return startNumber;
    }

    public int getEndNumber() {
        return endNumber;
    }

    public ImmutableSet<String> getPasswordHashes() {
        return passwordHashes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordRangeMessage that = (PasswordRangeMessage) o;
        return startNumber == that.startNumber &&
                endNumber == that.endNumber &&
                Objects.equals(passwordHashes, that.passwordHashes);
    }

    @Override
    public int hashCode() {

        return Objects.hash(startNumber, endNumber, passwordHashes);
    }

    @Override
    public String toString() {
        return "PasswordRangeMessage{" +
                "startNumber=" + startNumber +
                ", endNumber=" + endNumber +
                '}';
    }
}
