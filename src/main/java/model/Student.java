package model;

public class Student {

    private final int id;
    private final String name;
    private final String passwordHash;
    private final String gene;

    public Student(int id, String name, String passwordHash, String gene) {
        this.id = id;
        this.name = name;
        this.passwordHash = passwordHash;
        this.gene = gene;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getGene() {
        return gene;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id == student.id &&
                java.util.Objects.equals(name, student.name) &&
                java.util.Objects.equals(passwordHash, student.passwordHash) &&
                java.util.Objects.equals(gene, student.gene);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name, passwordHash, gene);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
