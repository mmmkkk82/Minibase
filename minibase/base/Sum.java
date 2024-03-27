package ed.inf.adbs.minibase.base;

import java.util.Objects;

/**
 * To represent SUM function, which is a term that may appear at most once in the head of a query,
 * after all the head variables.
 */
public class Sum extends AggregationTerm{

    public Sum(String name){
        super(name);
    }

    @Override
    public String toString() {
        return "SUM(" + name + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Sum sum = (Sum) obj;
        return Objects.equals(name, sum.name);
    }
    /*
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Sum)) return false;
        return (this.name).equals(((Sum) obj).getVariable());
    }
     */
}
