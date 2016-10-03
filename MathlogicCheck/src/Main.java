import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(new File("input.txt"));
        String header = in.nextLine();
        String axioms = header.substring(0, header.indexOf("|-"));
        ArrayList<String> assumptionList = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(axioms, ",");
        while (st.hasMoreTokens()) {
            String s = st.nextToken().replaceAll("\\s", "");
            assumptionList.add(s);
        }
        String target = header.substring(header.indexOf("|-") + 2).replaceAll("\\s", "");
        ArrayList<String> proof = new ArrayList<>();
        while (in.hasNextLine()) {
            String s = in.nextLine().replaceAll("\\s", "");
            proof.add(s);
        }

        ArrayList<String> result = new Validator(assumptionList, proof).validate();
        PrintWriter out = new PrintWriter(new File("output.txt"));
        result.stream().forEach(out::println);
        out.close();
    }
}
