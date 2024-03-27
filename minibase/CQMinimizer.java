package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.*;
import ed.inf.adbs.minibase.base.Atom;
import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.parser.QueryParser;
import ed.inf.adbs.minibase.base.RelationalAtom;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * Minimization of conjunctive queries
 *
 */
public class CQMinimizer {
    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Usage: CQMinimizer input_file output_file");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        minimizeCQ(inputFile, outputFile);

        parsingExample(inputFile);
    }

    public static void minimizeCQ(String inputFile, String outputFile) {
        // TODO: add your implementation
        try {
            Query query = QueryParser.parse(Paths.get(inputFile));
            List<Term> head = query.getHead().getTerms();
            List<Atom> body =query.getBody();


            //List<Variable> outputVariables = new ArrayList<Variable>();
            //for (Term term : head) outputVariables.add((Variable) term);

            // List<RelationalAtom> bodyAtom = new ArrayList<RelationalAtom>();
            //for (Atom atom : body) bodyAtom.add((RelationalAtom) atom);

            // convert type of output variables to Variable
            List<Variable> outputVariables = head.stream()
                    .map(term -> (Variable) term)
                    .collect(Collectors.toList());

            // convert type of atoms in query body to RelationalAtom
            List<RelationalAtom> bodyAtom = body.stream()
                    .map(atom -> (RelationalAtom) atom)
                    .collect(Collectors.toList());

            // to remove Atom, we need to check for removability for each atom
            // iterate from the tail to the head, so deleting the previous atom will not affect the fetch of the next atom
            for (int i = bodyAtom.size(); i > 0; i--) {
                int currentAtomIndex = bodyAtom.size() - i;

                // check whether this Atom contains output variable that is unique in body
                if (containUniqueOutputVariable(currentAtomIndex, bodyAtom, outputVariables)) {
                    continue; // if so, we shouldn't remove it
                }

                // check if the current atom is a homomorphism to any other atom
                if (hasHomomorphism(currentAtomIndex, bodyAtom)) {
                    bodyAtom.remove(currentAtomIndex);
                }
            }

            // print minimal CQ to output file
            System.out.println("Minimal: " + bodyAtom);

            List<Atom> reducedBodyAtoms = new ArrayList<>(bodyAtom);
            Query miniQuery = new Query(query.getHead(), reducedBodyAtoms);
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))){
                writer.write(miniQuery.toString());
            }catch (IOException e){
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Exception occurred during CQ minimization");
            e.printStackTrace();
        }
    }

    /**
     * Check whether an atom contains some output variables that never appear in any other atom.
     * @param currentAtomIndex index of the atom that is being checked
     * @param bodyAtom  list of atoms
     * @param outputVariables list of output variables
     * @return true if atom contains unique output variable, false if doesn't contain
     */
    private static boolean containUniqueOutputVariable(int currentAtomIndex, List<RelationalAtom> bodyAtom, List<Variable> outputVariables) {
        for (Term term : bodyAtom.get(currentAtomIndex).getTerms()) {
            if (!(term instanceof Variable)) continue;
            // whether this term appears in other atoms
            if (outputVariables.contains(term)) {
                boolean isDuplicate = false;
                for (RelationalAtom atom : bodyAtom) {
                    // compare with other atoms except itself
                    if (bodyAtom.indexOf(atom) == currentAtomIndex) continue;
                    if (atom.getTerms().contains(term)) {
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param currentAtomIndex
     * @param bodyAtom
     * @return
     */
    private static boolean hasHomomorphism(int currentAtomIndex, List<RelationalAtom> bodyAtom) {
        RelationalAtom currentAtom = bodyAtom.get(currentAtomIndex);
        for (RelationalAtom targetAtom : bodyAtom) {
            if (bodyAtom.indexOf(targetAtom) == currentAtomIndex) continue;
            if (!Objects.equals(targetAtom.getName(), currentAtom.getName())) continue;

            // homomorphism exists if:
            // 1. the non-variable terms in the being checked atom matches the value in corresponding place of that atom
            // 2. the variables in the being checked atom not appear in any other atoms
            boolean foundHomomorphism = true;
            for (int i = 0; i < currentAtom.getTerms().size(); i++) {
                Term currentTerm = currentAtom.getTerms().get(i);
                if (currentTerm instanceof Constant) {
                    if (!currentTerm.equals(targetAtom.getTerms().get(i))) {
                        foundHomomorphism = false;
                        break;
                    }
                } else { // this term is variable
                    if (!currentTerm.equals(targetAtom.getTerms().get(i))) {
                        // if the variable names are not the same, check whether there is a mapping
                        for (RelationalAtom otherAtom : bodyAtom) {
                            if (otherAtom.getTerms().contains(currentTerm)) {
                                // once we found an atom contains this term, there are 3 situations:
                                if (bodyAtom.indexOf(otherAtom) == currentAtomIndex) {
                                    // ignore the current atom itself
                                } else if (bodyAtom.indexOf(otherAtom) == bodyAtom.indexOf(targetAtom)) {
                                    // in the target term, the being checked term should only appear at the corresponding place
                                    if (currentTerm != targetAtom.getTerms().get(i)) {
                                        foundHomomorphism = false;
                                        break;
                                    }
                                } else {
                                    // if an atom is neither the current atom nor the target atom (that we are trying to build homomorphism with),
                                    // it should not contain this variable; otherwise the mapping of this variable will change other parts of the query body
                                    foundHomomorphism = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (foundHomomorphism) {
                return true;
            }
        }
        // don't find homomorphism
        return false;
    }

    /**
     * Example method for getting started with the parser.
     * Reads CQ from a file and prints it to screen, then extracts Head and Body
     * from the query and prints them to screen.
     */

    public static void parsingExample(String filename) {

        try {
            Query query = QueryParser.parse(Paths.get(filename));
            System.out.println("Entire query: " + query);
            RelationalAtom head = query.getHead();
            System.out.println("Head: " + head);
            List<Atom> body = query.getBody();
            System.out.println("Body: " + body);
        }
        catch (Exception e)
        {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    }

}
/**
 * The code is a Java implementation of the chase algorithm, which is used to find the minimal equivalent conjunctive query (CQ) of a given CQ. The chase algorithm operates by adding new facts to the knowledge base until no new facts can be added. The given CQ is minimal if no atoms can be removed from it without changing its answers.
 *
 * The minimizeCQ method reads a CQ from a file specified by the inputFile parameter, applies the chase algorithm to minimize it, and writes the resulting CQ to a file specified by the outputFile parameter. The method first parses the input file into a Query object, which contains the head and body of the CQ. It then converts the types of output variables to Variable and the types of atoms in the body to RelationalAtom.
 *
 * The method then iterates through the atoms in the body from the tail to the head, trying to remove atoms that can be removed without changing the answers of the CQ. An atom can be removed if it contains no unique output variables and holds a homomorphism to another atom. The contain_unique_output_variable method checks whether an atom contains some output variables that never appeared in any other atoms. The has_homomorphism method checks whether an atom holds homomorphism to any other atoms in the given list.
 *
 * Finally, the method prints the minimal CQ to the console and writes it to the output file.
 *
 * Overall, the code is well-documented and easy to follow.
 */