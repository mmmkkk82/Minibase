package ed.inf.adbs.minibase.base;

import java.util.Objects;

public class IntegerConstant extends Constant {
    private Integer value;

    public IntegerConstant(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || getClass()!=obj.getClass()) return false;
        IntegerConstant integerConstant = (IntegerConstant) obj;
        return Objects.equals(value, integerConstant.value);
    }
}
