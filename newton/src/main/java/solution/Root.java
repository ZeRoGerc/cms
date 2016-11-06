package solution;

import com.sun.istack.internal.NotNull;
import org.jblas.ComplexDouble;

public enum Root {
    FIRST(ComplexDouble.UNIT),
    SECOND(new ComplexDouble(-1.0/2.0, Math.sqrt(3)/2)),
    THIRD(new ComplexDouble(-1.0/2.0, -Math.sqrt(3)/2));

    @NotNull
    private ComplexDouble value;

    Root(@NotNull ComplexDouble value) {
        this.value = value;
    }

    @NotNull
    public ComplexDouble getValue() {
        return value;
    }
}
