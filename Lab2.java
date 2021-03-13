import java.math.BigDecimal;
import java.util.*;

public class Lab2 {

    static int[][] connection = {{0, 0, 1, 1, 1, 0},
                                 {0, 0, 1, 1, 0, 1},
                                 {0, 0, 0, 1, 1, 1},
                                 {0, 0, 0, 0, 1, 1},
                                 {0, 0, 0, 0, 0, 0},
                                 {0, 0, 0, 0, 0, 0}};
    static double[] P = {0.74, 0.14, 0.56, 0.35, 0.20, 0.21};
    static int[] startPoints = {1, 2};
    static int[] endPoints = {5, 6};

    static List<List<Integer>> allWays = new ArrayList<>();
    static List<List<Integer>> initialTableOfStates = new ArrayList<>();
    static List<BigDecimal> valuesP = new ArrayList<>();
    static Set<List<Integer>> finalTableOfStates = new HashSet<>();

    public static void main(String[] args) {

        List<Integer> temp = new ArrayList<>();

        for (int i = 0; i < startPoints.length; i++) {
            temp.add(startPoints[i]);
            recDfs(i, temp);
            temp.clear();
        }
        System.out.println("All Possible ways: " + allWays.toString());

        makeInitialTableOfStates();
        makeFinalTableOfStates();
        makePStates();

        System.out.println("Psystem = " + valuesP.stream().reduce(BigDecimal.ZERO, BigDecimal::add));

    }

    private static void makePStates() {
        System.out.println("____________________________________");
        System.out.println("E1 | E2 | E3 | E4 | E5 | E6 | Pstate");
        for (List<Integer> finalTableOfState : finalTableOfStates) {
            BigDecimal value = BigDecimal.ONE;
            for (int j = 0; j < finalTableOfState.size(); j++) {
                if (finalTableOfState.get(j) == 1) {
                    System.out.print(" + | ");
                    value = value.multiply(BigDecimal.valueOf(P[j]));
                } else {
                    System.out.print(" - | ");
                    value = value.multiply(BigDecimal.valueOf(1 - P[j]));
                }
            }
            valuesP.add(value);
            System.out.println(value);
        }
        System.out.println("------------------------------------");
    }

    private static void makeFinalTableOfStates() {
        for (List<Integer> allWay : allWays) {
            for (List<Integer> initialTableOfState : initialTableOfStates) {
                int flag = 0;
                for (Integer integer : allWay) {
                    if (initialTableOfState.get(integer - 1) == 1) {
                        flag++;
                    }
                }
                if (flag == allWay.size()) {
                    finalTableOfStates.add(initialTableOfState);
                }
            }
        }
    }

    private static void makeInitialTableOfStates() {
        List<Integer> temp;
        for (int i = 0; i < (Math.pow(2, connection.length)); i++) {
            String binValue = Integer.toBinaryString(i);
            temp = Arrays.stream(binValue.split(""))
                    .mapToInt(Integer::parseInt)
                    .collect(ArrayList::new,
                            ArrayList::add,
                            ArrayList::addAll);

            if (temp.size() < connection.length) {
                int tempSize = temp.size();
                for (int j = 0; j < connection.length - tempSize; j++) {
                    temp.add(j, 0);
                }
            }
            initialTableOfStates.add(temp);
        }
    }

    private static List<Integer> recDfs(int i, List<Integer> temp) {
        for (int j = 0; j < connection[i].length; j++) {
            if (connection[i][j] == 1) {
                temp.add(j + 1);
                recDfs(j, temp);
                if (Arrays.stream(endPoints).filter(value -> value == temp.get(temp.size() - 1)).count() > 0) {
                    allWays.add(new ArrayList<>(temp));
                }
                temp.remove(temp.size() - 1);
            }
        }
        return temp;
    }
}