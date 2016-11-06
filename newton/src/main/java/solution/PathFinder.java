package solution;

import com.sun.istack.internal.NotNull;
import org.jblas.ComplexDouble;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class PathFinder {

    private static final ComplexDouble THREE = new ComplexDouble(3, 0);

    @NotNull
    public List<ComplexDouble> findPathForPoint(@NotNull ComplexDouble point) {
        List<ComplexDouble> result = new ArrayList<>();
        ComplexDouble cur = point;
        for (int i = 0; i < 100; i++) {
            result.add(cur);
            cur = generateNext(cur);
        }

        final Root root = getNearestRoot(cur);
        result.add(root.getValue());

        return result;
    }

    @NotNull
    public Root getRoot(@NotNull ComplexDouble point) {
        final List<ComplexDouble> path = findPathForPoint(point);
        return getNearestRoot(path.get(path.size() - 1));
    }

    @NotNull
    public List<Root> getRoots(@NotNull List<ComplexDouble> points) {
        return points.stream().map(this::getRoot).collect(toList());
    }

    private Root getNearestRoot(@NotNull ComplexDouble point) {
        Root best = Root.FIRST;
        for (Root temp : Root.values()) {
            if (sqDist(point, temp.getValue()) < sqDist(point, best.getValue())) {
                best = temp;
            }
        }
        return best;
    }

    private double sqDist(@NotNull ComplexDouble a, @NotNull ComplexDouble b) {
        ComplexDouble temp = a.sub(b);
        return (temp.real() * temp.real()) + (temp.imag() * temp.imag());
    }

    @NotNull
    private ComplexDouble generateNext(@NotNull ComplexDouble point) {
        return point.sub(f(point).div(df(point)));
    }

    @NotNull
    private ComplexDouble f(@NotNull ComplexDouble point) {
        return point.mul(point).mul(point).sub(ComplexDouble.UNIT);
    }

    @NotNull
    private ComplexDouble df(@NotNull ComplexDouble point) {
        return point.mul(point).mul(THREE);
    }
}
