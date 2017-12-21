package model;

import java.util.Objects;

import static java.lang.String.format;

public class GenePartner {

    private final Student studentA;
    private final Student studentB;
    private final String geneMatch;

    public GenePartner(Student studentA, Student studentB, String geneMatch) {
        this.studentA = studentA;
        this.studentB = studentB;
        this.geneMatch = geneMatch;
    }

    public Student getStudentA() {
        return studentA;
    }

    public Student getStudentB() {
        return studentB;
    }

    public Student getOtherStudent(Student student) {
        if (student.equals(studentA)) {
            return studentB;
        } else if (student.equals(studentB)) {
            return studentA;
        }
        throw new AssertionError(format("Invalid partner %s for student %s", this, student));
    }

    public String getGeneMatch() {
        return geneMatch;
    }

    public int getGeneMatchLength() {
        return geneMatch.length();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenePartner that = (GenePartner) o;
        return Objects.equals(studentA, that.studentA) ||
                Objects.equals(studentB, that.studentB) &&
                Objects.equals(geneMatch, that.geneMatch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentA, studentB, geneMatch);
    }

    @Override
    public String toString() {
        return "GenePartner{" +
                "studentA=" + studentA +
                ", studentB=" + studentB +
                ", geneMatch='" + geneMatch + '\'' +
                '}';
    }
}
