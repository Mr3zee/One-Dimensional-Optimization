package algo;

import java.util.*;
import java.util.function.Function;

public class Optimization {
    private interface OptimizationAlgorithm extends QuinaryFunction<
                Function<Double, Double>,
                Double,
                Double,
                Double,
                MainGraph,
                Double
                > { }

    private static Algorithm unwrapAlgo(String name, OptimizationAlgorithm algo) {
        return new Algorithm((variant, epsilon) -> {
            double left = variant.getLeft();
            double right = variant.getRight();
            OptimizationResult result = new OptimizationResult(name, left, right);
            MainGraph graph = new MainGraph("f(x)", variant.getFunction());
            graph.addIteration(left, right);
            result.setGraph(graph);
            double finalPoint = algo.apply(variant.getFunction(), left, right, epsilon, graph);
            result.setResult(finalPoint);
            return result;
        });
    }

    public static OptimizationResult run(Algorithm algorithm, Variant variant, double epsilon) {
        return algorithm.f.apply(variant, epsilon);
    }

    public static final Algorithm DICHOTOMY = unwrapAlgo("DICHOTOMY", (f, left, right, epsilon, graph) -> {
        double x;
        do {
            x = getMiddle(left, right);
            double f1 = f.apply(x - epsilon / 2);
            double f2 = f.apply(x + epsilon / 2);
            if (f1 < f2) {
                right = x;
            } else {
                left = x;
            }
            graph.addIteration(left, right);
        } while (checkBounds(left, right, epsilon));
        return f.apply(x);
    });

    private static double getMiddle(double a, double b) {
        return (a - b) / 2 + b;
    }

    private static boolean checkBounds(double left, double right, double epsilon) {
        return Math.abs(left - right) >= epsilon;
    }

    private static final double REVERSED_GOLDEN_CONST = (Math.sqrt(5) - 1) / 2;

    public static final Algorithm GOLDEN_SECTION = unwrapAlgo("GOLDEN_SECTION", (f, left, right, epsilon, graph) -> {
        double delta = (right - left) * REVERSED_GOLDEN_CONST;

        double x2 = left + delta;
        double x1 = right - delta;

        double f2 = f.apply(x2);
        double f1 = f.apply(x1);

        do {
            delta = REVERSED_GOLDEN_CONST * delta;
            if (f1 >= f2) {
                left = x1; x1 = x2; x2 = left + delta;
                f1 = f2; f2 = f.apply(x2);
            } else {
                right = x2; x2 = x1; x1 = right - delta;
                f2 = f1; f1 = f.apply(x1);
            }
            graph.addIteration(left, right);
        } while (delta > epsilon);
        return f.apply(getMiddle(left, right));
    });

    private static final List<Double> FIBONACCI_NUMBERS = getNFibonacci();

    public static final Algorithm FIBONACCI = unwrapAlgo("FIBONACCI", (f, left, right, epsilon, graph) -> {
        int n = calculateFibonacciConst(left, right, epsilon);
        int k = 0;
        double lambda = getFibonacciVar(left, right, n, k + 2, k);
        double mu = getFibonacciVar(left, right, n, k + 1, k);
        double f_mu = f.apply(mu), f_lambda = f.apply(lambda);

        double an, bn;
        while (true) {
            k++;
            if (k == n - 2) {
                mu = lambda + epsilon;
                if (f_mu >= f_lambda) {
                    an = lambda;
                    bn = right;
                } else {
                    an = left;
                    bn = mu;
                }
                graph.addIteration(an, bn);
                break;
            }
            if (f_lambda > f_mu) {
                left = lambda;
                lambda = mu;
                f_lambda = f_mu;
                mu = getFibonacciVar(left, right, n, k + 1, k);
                f_mu = f.apply(mu);
            } else {
                right = mu;
                mu = lambda;
                f_mu = f_lambda;
                lambda = getFibonacciVar(left, right, n, k + 2, k);
                f_lambda = f.apply(lambda);
            }
            graph.addIteration(left, right);
        }
        return f.apply(getMiddle(an, bn));
    });

    private static int calculateFibonacciConst(double left, double right, double epsilon) {
        return Math.min(1475, Math.abs(Collections.binarySearch(FIBONACCI_NUMBERS, (right - left) / epsilon)) + 1);
    }


    private static double getFibonacciVar(double a, double b, int n, int i, int j) {
        return a + FIBONACCI_NUMBERS.get(n - i) / FIBONACCI_NUMBERS.get(n - j) * (b - a);
    }

    private static List<Double> getNFibonacci() {
        List<Double> arr = new ArrayList<>(1476);
        arr.add(1.0);
        arr.add(1.0);
        for (int i = 2; i < 1476; i++) {
            arr.add(arr.get(i - 1) + arr.get(i - 2));
        }
        return arr;
    }

    public static final Algorithm PARABOLIC = unwrapAlgo("PARABOLIC", (f, a, c, epsilon, graph) -> {
        double b = getMiddle(a, c), x;
        double fa = f.apply(a), fb = f.apply(b), fc = f.apply(c);
        while (checkBounds(a, c, epsilon)) {
            x = parabolicMinimum(a, b, c, fa, fb, fc);
            double fx = f.apply(x);
            if (fx < fb) {
                if (x < b) {
                    c = b;
                    fc = fb;
                } else {
                    a = b;
                    fa = fb;
                }
                b = x;
                fb = fx;
            } else {
                if (x < b) {
                    a = x;
                    fa = fx;
                } else {
                    c = x;
                    fc = fx;
                }
            }
            graph.addIteration(a, c);
            graph.addGraphToLastIteration(createParabola(a, b, c, fa, fb, fc));
        }
        return f.apply(b);
    });

    private static double parabolicMinimum(double a, double b, double c, double fa, double fb, double fc) {
        return b + 0.5 * ((fa - fb) * (c - b) * (c - b) - (fc - fb) * (b - a) * (b - a))
                / ((fa - fb) * (c - b) + (fc - fb) * (b - a));
    }

    private static SingleGraph createParabola(double x1, double x2, double x3, double f1, double f2, double f3) {
        double d = ((x1 - x2) * (x1 * x2 - x1 * x3 - x2 * x3 + x3 * x3));
        double a =   ( (f1 - f3)           * x2      + (f2 - f1)           * x3      + (f3 - f2)           * x1     ) / d;
        double b = - ( (f1 - f3)           * x2 * x2 + (f2 - f1)           * x3 * x3 + (f3 - f2)           * x1 * x1) / d;
        double c = - (-(f1 * x3 - f3 * x1) * x2 * x2 - (f2 * x1 - f1 * x2) * x3 * x3 - (f3 * x2 - f2 * x3) * x1 * x1) / d;
        return new SingleGraph("Parabola", x -> a * x * x + b * x + c);
    }

    public static final Algorithm BRENT = unwrapAlgo("BRENT", (f, a, c, epsilon, graph) -> {
        double x, w, v, d, e, g, u, fx, fw, fv;
        x = w = v = a + REVERSED_GOLDEN_CONST * (c - a);
        fx = fw = fv = f.apply(x);
        d = e = c - a;
        while(checkBounds(a, c, epsilon)) {
            g = e;
            e = d;
            if (different(w, x, v) && different(fw, fx, fv)
                    && (u = parabolicMinimum(w, x, v, fw, fx, fv)) == u
                    && a <= u && u <= c && Math.abs(u - x) < (g / 2)) {
                graph.addGraphToLastIteration(createParabola(w, x, v, fw, fx, fv));
                // u - accepted
            } else  {
                // u - rejected, u - golden section
                if (x < getMiddle(a, c)) {
                    e = c - x;
                    u = x + REVERSED_GOLDEN_CONST * e;
                } else {
                    e = x - a;
                    u = x - REVERSED_GOLDEN_CONST * e;
                }
            }
            double fu = f.apply(u);
            if (fu <= fx) {
                if (u >= x) {
                    a = x;
                } else {
                    c = x;
                }
                v = w; w = x; x = u;
                fv = fw; fw = fx; fx = fu;
            } else {
                if (u >= x) {
                    c = u;
                } else {
                    a = u;
                }
                if (fu <= fw || w == x) {
                    v = w; w = u;
                    fv = fw; fw = fu;
                } else if (fu <= fv || v == x || v == w) {
                    v = u;
                    fv = fu;
                }
            }
            graph.addIteration(a, c);
            graph.addVLineGraphToLastIteration(new VLineGraph("x", x));
            graph.addVLineGraphToLastIteration(new VLineGraph("w", w));
            graph.addVLineGraphToLastIteration(new VLineGraph("v", v));
            d = c - a;
        }
        return f.apply(x);
    });

    private static boolean different(double a, double b, double c) {
        return a != b && b != c && c != a;
    }

    private static void printBounds(double left, double right) {
        System.out.format("[%s, %s]\n", left, right);
    }

    public static final Map<String, Algorithm> ALGORITHMS = new TreeMap<>();

    public static void init() {
        ALGORITHMS.put("DICHOTOMY", DICHOTOMY);
        ALGORITHMS.put("GOLDEN SECTION", GOLDEN_SECTION);
        ALGORITHMS.put("FIBONACCI", FIBONACCI);
        ALGORITHMS.put("PARABOLIC", PARABOLIC);
        ALGORITHMS.put("BRENT", BRENT);
    }
}
