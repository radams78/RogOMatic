package tryout;

public interface Tryout {
    default int addOne(int x) {
        return x + 1;
    }
}
