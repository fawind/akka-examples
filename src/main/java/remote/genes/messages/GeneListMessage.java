package remote.genes.messages;

import com.google.common.collect.ImmutableSet;

import java.io.Serializable;
import java.util.Objects;

public class GeneListMessage implements Serializable {

    private final ImmutableSet<String> genes;

    public GeneListMessage(ImmutableSet<String> genes) {
        this.genes = genes;
    }

    public ImmutableSet<String> getGenes() {
        return genes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneListMessage that = (GeneListMessage) o;
        return Objects.equals(genes, that.genes);
    }

    @Override
    public int hashCode() {

        return Objects.hash(genes);
    }
}
