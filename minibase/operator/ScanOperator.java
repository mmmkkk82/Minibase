package ed.inf.adbs.minibase.operator;

import ed.inf.adbs.minibase.base.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class implements the SCAN operation, reading data from data file and return them as {@link Tuple} instances.
 * Each {@link RelationalAtom} will be interpreted as a {@link ScanOperator} in query plan.
 * This operator will always be the leaf node of the query plan,
 * the selection and join conditions will be implemented in the parent nodes of this operator.
 */
public class ScanOperator extends Operator {

    private final String relationName;
    private Scanner relationScanner;
    private final List<String> relationSchema;

    /**
     * Initialize the file reader, make connection to {@link DBCatalog}.
     * Use the relation name to load corresponding schema.
     * Use the terms in the relational atom to build the variable mask
     * @param baseQueryAtom a relational atom in query body, providing information like relation name and variable mask.
     */
    public ScanOperator(RelationalAtom baseQueryAtom) {
        for (Term term : baseQueryAtom.getTerms()) {
            if (term instanceof Variable)
                this.variableMask.add(((Variable) term).getName());
            else
                this.variableMask.add(null);
        }

        this.relationName = baseQueryAtom.getName();
        DBCatalog dbc = DBCatalog.getInstance();
        this.relationSchema = dbc.getSchema(relationName);
        this.reset();
    }

//    @Override
//    public void dump(String outputFile) {
//        while (this.relationScanner.hasNextLine()) {
//            System.out.println(this.getNextTuple());
//        }
//    }

    /**
     * Reset the operator state. A new Scanner instance will be built,
     * so that the following read operations will back to the beginning of file.
     */
    @Override
    public void reset() {
        DBCatalog dbc = DBCatalog.getInstance();
        try {
            this.relationScanner = new Scanner(new File(dbc.getRelationPath(relationName)));
        } catch (FileNotFoundException e) {
            System.out.println("Relation data file not found: " + dbc.getRelationPath(relationName));
            e.printStackTrace();
        }
    }

    /**
     * Read the next line of relation file, return it as a {@link Tuple} instance.
     * The schema information stored in {@link DBCatalog} indicates
     * whether a column of relation database should be interpreted as Integer or String.
     * @return a {@link Tuple} instance that represents the data in next line of the data file.
     */
    @Override
    public Tuple getNextTuple() {
        if (this.relationScanner.hasNextLine()) {
            String line = this.relationScanner.nextLine();
            String[] raw_data = line.split("[^a-zA-Z0-9]+");
            ArrayList<Term> terms = new ArrayList<>();
            for (int i = 0; i < raw_data.length; i++) {
                if (this.relationSchema.get(i).equals("int")) {
                    terms.add(new IntegerConstant(Integer.parseInt(raw_data[i])));
                } else {
                    terms.add(new StringConstant(raw_data[i]));
                }
            }
            return new Tuple(this.relationName, terms);
        } else {
            return null;
        }
    }

    /**
     * Unit test of ScanOperator, output is printed to the console.
     * @param args Command line inputs, can be empty.
     */
    public static void main(String[] args) {
        DBCatalog dbc = DBCatalog.getInstance();
        dbc.init("data/evaluation/db");

        List<Term> queryAtomTerms = new ArrayList<>();
        queryAtomTerms.add( new IntegerConstant(9));
        queryAtomTerms.add( new Variable("x"));
        queryAtomTerms.add( new Variable("y"));
        RelationalAtom queryAtom = new RelationalAtom("R", queryAtomTerms); // R:(9, x, y)
        ScanOperator scanOp = new ScanOperator(queryAtom);
        System.out.println(scanOp.getVariableMask());

        scanOp.dump(null);

        System.out.println("---------");
        System.out.println(scanOp.getNextTuple());
        scanOp.reset();
        System.out.println(scanOp.getNextTuple());
        System.out.println(scanOp.getNextTuple());
        scanOp.reset();
        System.out.println(scanOp.getNextTuple());

        System.out.println("---------");
        scanOp.dump(null);

    }

}