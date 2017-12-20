package remote.genes.messages;

import java.io.Serializable;
import java.util.Objects;

public class GeneTupleSubstringMessage implements Serializable {

    private final String geneA;
    private final String geneB;
    private final String longestSubstring;

    public GeneTupleSubstringMessage(String geneA, String geneB, String longestSubstring) {
        this.geneA = geneA;
        this.geneB = geneB;
        this.longestSubstring = longestSubstring;
    }

    public String getGeneA() {
        return geneA;
    }

    public String getGeneB() {
        return geneB;
    }

    public String getLongestSubstring() {
        return longestSubstring;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneTupleSubstringMessage that = (GeneTupleSubstringMessage) o;
        return Objects.equals(geneA, that.geneA) &&
                Objects.equals(geneB, that.geneB) &&
                Objects.equals(longestSubstring, that.longestSubstring);
    }

    @Override
    public int hashCode() {

        return Objects.hash(geneA, geneB, longestSubstring);
    }
}
