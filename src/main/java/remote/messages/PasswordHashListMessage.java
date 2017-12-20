package remote.messages;

import com.google.common.collect.ImmutableSet;

import java.io.Serializable;
import java.util.Objects;

public class PasswordHashListMessage implements Serializable {

    private final ImmutableSet<String> passwordHashes;

    public PasswordHashListMessage(ImmutableSet<String> passwordHashes) {
        this.passwordHashes = passwordHashes;
    }

    public ImmutableSet<String> getPasswordHashes() {
        return passwordHashes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordHashListMessage that = (PasswordHashListMessage) o;
        return Objects.equals(passwordHashes, that.passwordHashes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passwordHashes);
    }
}
