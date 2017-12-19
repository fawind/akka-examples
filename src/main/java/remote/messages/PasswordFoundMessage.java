package remote.messages;

import java.io.Serializable;
import java.util.Objects;

public class PasswordFoundMessage implements Serializable {

    private final String password;

    public PasswordFoundMessage(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordFoundMessage that = (PasswordFoundMessage) o;
        return Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {

        return Objects.hash(password);
    }

    @Override
    public String toString() {
        return "PasswordFoundMessage{" +
                "password='" + password + '\'' +
                '}';
    }
}
