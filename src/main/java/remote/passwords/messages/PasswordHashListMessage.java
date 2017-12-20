package remote.passwords.messages;

import com.google.common.collect.ImmutableSet;

import java.io.Serializable;
import java.util.Objects;

public class PasswordHashListMessage implements Serializable {

    private final ImmutableSet<String> passwordHashes;
    private final int maxPasswordLength;

    public PasswordHashListMessage(ImmutableSet<String> passwordHashes, int maxPasswordLength) {
        this.passwordHashes = passwordHashes;
        this.maxPasswordLength = maxPasswordLength;
    }

    public ImmutableSet<String> getPasswordHashes() {
        return passwordHashes;
    }

    public int getMaxPasswordLength() {
        return maxPasswordLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordHashListMessage that = (PasswordHashListMessage) o;
        return maxPasswordLength == that.maxPasswordLength &&
                Objects.equals(passwordHashes, that.passwordHashes);
    }

    @Override
    public int hashCode() {

        return Objects.hash(passwordHashes, maxPasswordLength);
    }
}
