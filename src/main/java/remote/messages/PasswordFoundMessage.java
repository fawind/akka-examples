package remote.messages;

import java.io.Serializable;
import java.util.Objects;

public class PasswordFoundMessage implements Serializable {

    private final String password;
    private final String passwordHash;

    public PasswordFoundMessage(String password, String passwordHash) {
        this.password = password;
        this.passwordHash = passwordHash;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordFoundMessage that = (PasswordFoundMessage) o;
        return Objects.equals(password, that.password) &&
                Objects.equals(passwordHash, that.passwordHash);
    }

    @Override
    public int hashCode() {

        return Objects.hash(password, passwordHash);
    }

    @Override
    public String toString() {
        return "PasswordFoundMessage{" +
                "password='" + password + '\'' +
                '}';
    }
}
