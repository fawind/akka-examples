package remote.genes.messages;

import java.io.Serializable;
import java.util.Objects;

public class GeneTupleMessage implements Serializable {

    private final String geneA;
    private final String geneB;

    public GeneTupleMessage(String geneA, String geneB) {
        this.geneA = geneA;
        this.geneB = geneB;
    }

    public String getGeneA() {
        return geneA;
    }

    public String getGeneB() {
        return geneB;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneTupleMessage that = (GeneTupleMessage) o;
        return Objects.equals(geneA, that.geneA) &&
                Objects.equals(geneB, that.geneB);
    }

    @Override
    public int hashCode() {

        return Objects.hash(geneA, geneB);
    }
}
