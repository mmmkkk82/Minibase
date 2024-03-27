package ed.inf.adbs.minibase.base;

/**
 * To indicates terms with aggregation operations in the query head.
 */
public class AggregationTerm extends Term{
    protected String name;

    public AggregationTerm(String name){
        this.name = name;
    }

    public String getVariable(){
        return name;
    }
}
