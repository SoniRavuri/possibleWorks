import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;

public class SecretPolynomialSolver {
    public static void main(String[] args) {
        // Define file paths for test cases
        String[] testFiles = {"testcase1.json", "testcase2.json"};

        // Process each test case file
        for (int i = 0; i < testFiles.length; i++) {
            try {
                System.out.println("Processing: " + testFiles[i]);
                String json = new String(Files.readAllBytes(Paths.get(testFiles[i]))); // Read file content
                solveForSecret(json);
            } catch (Exception e) {
                System.out.println("Error reading file: " + testFiles[i]);
                e.printStackTrace();
            }
            System.out.println("----------------------------");
        }
    }

    private static void solveForSecret(String json) {
        int k = extractKeyValue(json, "\"k\"");

        List<BigInteger> xValues = new ArrayList<>();
        List<BigInteger> yValues = new ArrayList<>();

        Pattern pattern = Pattern.compile("\"(\\d+)\"\\s*:\\s*\\{\\s*\"base\"\\s*:\\s*\"(\\d+)\"\\s*,\\s*\"value\"\\s*:\\s*\"([^\"]+)\"\\s*}");
        Matcher matcher = pattern.matcher(json);

        while (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1)); // x value (key)
            int base = Integer.parseInt(matcher.group(2)); // Base
            String value = matcher.group(3); // Encoded value

            if (base < 2 || base > 36) {
                System.out.println("Skipping invalid base: " + base + " for x = " + x);
                continue;
            }

            BigInteger y = new BigInteger(value, base); // Convert from given base to decimal
            xValues.add(BigInteger.valueOf(x));
            yValues.add(y);

            if (xValues.size() == k) break;
        }

        if (xValues.size() < k) {
            System.out.println("Not enough valid data points to compute the polynomial.");
            return;
        }

        // Solve using Lagrange Interpolation
        BigInteger constantTerm = lagrangeInterpolation(xValues, yValues, BigInteger.ZERO);
        System.out.println("Secret (c): " + constantTerm);
    }

    private static BigInteger lagrangeInterpolation(List<BigInteger> x, List<BigInteger> y, BigInteger targetX) {
        BigInteger result = BigInteger.ZERO;
        int k = x.size();

        for (int i = 0; i < k; i++) {
            BigInteger term = y.get(i);
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    numerator = numerator.multiply(targetX.subtract(x.get(j)));
                    denominator = denominator.multiply(x.get(i).subtract(x.get(j)));
                }
            }
            term = term.multiply(numerator).divide(denominator);
            result = result.add(term);
        }
        return result;
    }

    private static int extractKeyValue(String json, String key) {
        Pattern pattern = Pattern.compile(key + "\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : -1;
    }
}
