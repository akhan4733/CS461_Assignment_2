package genetics.math;

import java.util.List;
import java.util.SplittableRandom;

public class Picker<T extends ProbableSelection> {

    private int k;
    private final List<Double> selections;
    private final List<T> values;
    private final static SplittableRandom random = new SplittableRandom();

    public Picker(List<T> values) {
        k =0;
        this.values = values;
        selections = values.stream()
                .map(ProbableSelection::calculateLikelyHood)
                .toList();
    }

    public T pick(int failSafe) {
        for (int i = k; i < selections.size(); i++) {
            if (random.nextDouble(1) <= selections.get(i)) {
                k++;
                return values.get(i);
            }
        }
        return values.get(failSafe);
    }

    public void reset() {
        k = 0;
    }
}
