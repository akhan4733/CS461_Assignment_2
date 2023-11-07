package genetics.math;

import java.util.List;

public class SoftMax {
    public static List<Double> calculateSoftMax(List<Double> inputs) {
        double totalExponentiation = calculateTotal(inputs);
        return inputs.stream()
                .map(input -> Math.exp(input) / totalExponentiation)
                .toList();
    }

    private static double calculateTotal(List<Double> inputs) {
        return inputs.stream()
                .mapToDouble(Math::exp)
                .sum();
    }
}
