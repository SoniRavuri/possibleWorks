import java.math.BigInteger;
import java.util.*;

public class SecretPolynomialSolver {
    public static void main(String[] args) {
        try {
            solveForSecret("{\"keys\":{\"n\":4,\"k\":3},\"1\":{\"base\":\"10\",\"value\":\"4\"},\"2\":{\"base\":\"2\",\"value\":\"111\"},\"3\":{\"base\":\"10\",\"value\":\"12\"},\"6\":{\"base\":\"4\",\"value\":\"213\"}}");
            solveForSecret("{\"keys\":{\"n\":10,\"k\":7},\"1\":{\"base\":\"7\",\"value\":\"420020006424065463\"},\"2\":{\"base\":\"7\",\"value\":\"10511630252064643035\"},\"3\":{\"base\":\"2\",\"value\":\"101010101001100101011100000001000111010010111101100100010\"},\"4\":{\"base\":\"8\",\"value\":\"31261003022226126015\"},\"5\":{\"base\":\"7\",\"value\":\"2564201006101516132035\"},\"6\":{\"base\":\"15\",\"value\":\"a3c97ed550c69484\"},\"7\":{\"base\":\"13\",\"value\":\"134b08c8739552a734\"},\"8\":{\"base\":\"10\",\"value\":\"23600283241050447333\"},\"9\":{\"base\":\"9\",\"value\":\"375870320616068547135\"},\"10\":{\"base\":\"6\",\"value\":\"30140555423010311322515333\"}}");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void solveForSecret(String jsonContent) {
        int k = extractKeyValue(jsonContent, "\"k\"");

        List<BigInteger> xValues = new ArrayList<>();
        List<BigInteger> yValues = new ArrayList<>();

        for (String entry : jsonContent.split("\\{")) {
            if (!entry.contains("\"base\"") || !entry.contains("\"value\"")) continue;

            int x = extractKey(entry);
            int base = extractKeyValue(entry, "\"base\"");
            String value = extractStringValue(entry, "\"value\"");

            // ✅ Check if base is valid (between 2 and 36)
            System.out.println("Extracted base for x = " + x + " is: " + base); // Debugging line
if (base < 2 || base > 36) {
    System.out.println("[Warning] Skipping invalid base: " + base + " for x = " + x);
    continue;
}

        

            BigInteger y = new BigInteger(value, base);
            xValues.add(BigInteger.valueOf(x));
            yValues.add(y);

            if (xValues.size() == k) break;
        }

        // Solve using Lagrange Interpolation
        BigInteger constantTerm = lagrangeInterpolation(xValues, yValues, BigInteger.ZERO);
        System.out.println("Secret (c): " + constantTerm);
    }

    // private static BigInteger lagrangeInterpolation(List<BigInteger> x, List<BigInteger> y, BigInteger targetX) {
    //     BigInteger result = BigInteger.ZERO;
    //     int k = x.size();

    //     for (int i = 0; i < k; i++) {
    //         BigInteger term = y.get(i);
    //         BigInteger numerator = BigInteger.ONE;
    //         BigInteger denominator = BigInteger.ONE;

    //         for (int j = 0; j < k; j++) {
    //             if (i != j) {
    //                 numerator = numerator.multiply(targetX.subtract(x.get(j)));
    //                 denominator = denominator.multiply(x.get(i).subtract(x.get(j)));
    //             }
    //         }
    //         term = term.multiply(numerator).divide(denominator);
    //         result = result.add(term);
    //     }
    //     return result;
    // }
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

    // Ensure non-negative result
    // return result.mod(BigInteger.TEN.pow(20)); // Use a large modulus to keep result positive
    return result.mod(BigInteger.valueOf(100000000000000003L)); // A prime number modulus


}


    private static int extractKeyValue(String json, String key) {
    String pattern = key + "\\s*:\\s*\"?(\\d+)\"?"; // Handles both quoted and unquoted numbers
    return extractFirstInt(json, pattern);
}


    private static int extractKey(String entry) {
        String pattern = "\"(\\d+)\"\\s*:";
        return extractFirstInt(entry, pattern);
    }

    private static String extractStringValue(String json, String key) {
        String pattern = key + "\\s*:\\s*\"([^\"]+)\"";
        return extractFirstString(json, pattern);
    }

    private static int extractFirstInt(String json, String pattern) {
        return Integer.parseInt(extractFirstString(json, pattern));
    }

    private static String extractFirstString(String json, String pattern) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(pattern).matcher(json);
        return matcher.find() ? matcher.group(1) : "0";
    }
}
