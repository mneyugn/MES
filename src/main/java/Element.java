import java.util.Arrays;

public class Element {
    private int[]ID;

    public Element(int n1, int n2, int n3, int n4) {
        this.ID = new int[]{n1, n2, n3, n4};
    }

    @Override
    public String toString() {
        return "Element{" +
                "ID=" + Arrays.toString(ID) +
                '}';
    }
}
