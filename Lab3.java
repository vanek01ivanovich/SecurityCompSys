import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.IntStream;

public class Lab3 extends Lab2 {

    public static double T = 1000.0;
    public static BigDecimal Qsystem = BigDecimal.ZERO;
    public static int Tsystem;
    public static final int K1 = 1;
    public static final int K2 = 1;

    public static void main(String[] args) {

        mainFunctionLab2();
        System.out.println("Psystem = " + Psystem);
        Qsystem = BigDecimal.ONE.subtract(Psystem);
        Tsystem = (int) -(T / Math.log(Double.parseDouble(Psystem.toString())));

        System.out.println("Qsystem = " + Qsystem);
        System.out.println("Tsystem = " + Tsystem);
        System.out.println("______________________________________________");
        commonLoadedNotLoaded("load",K1);
        System.out.println("______________________________________________");
        distributeLoadedOrNotLoaded("load",K2);
        System.out.println("______________________________________________");
        commonLoadedNotLoaded("unload",K1);
        System.out.println("______________________________________________");
        distributeLoadedOrNotLoaded("unload",K2);

    }

    public static void commonLoadedNotLoaded(String loadOrNot,int K) {
        BigDecimal PReversedSystem = BigDecimal.ZERO;
        BigDecimal newQReversedSystem = BigDecimal.ZERO;
        if (loadOrNot.equalsIgnoreCase("load")){
            System.out.println("Common loaded:");
            double powerP = Math.pow(Double.parseDouble(BigDecimal.ONE.subtract(Psystem).toString()),K+1);
            PReversedSystem = BigDecimal.ONE.subtract(BigDecimal.valueOf(powerP));
            newQReversedSystem = BigDecimal.ONE.subtract(PReversedSystem);
        }else if (loadOrNot.equalsIgnoreCase("unload")){
            System.out.println("Common unloaded:");
            int factorial = IntStream.rangeClosed(1, K + 1).reduce(1, (x, y) -> x * y);
            newQReversedSystem = Qsystem.divide(BigDecimal.valueOf(factorial), 2, RoundingMode.HALF_UP);
            PReversedSystem = BigDecimal.ONE.subtract(newQReversedSystem);
        }

        int TReversedSystem = (int) -(T / Math.log(Double.parseDouble(PReversedSystem.toString())));

        System.out.println("PReversedSystem = " + PReversedSystem);
        System.out.println("QReversedSystem = " + newQReversedSystem);
        System.out.println("TReversedSystem = " + TReversedSystem);

        BigDecimal GQ = newQReversedSystem.divide(Qsystem, 2, RoundingMode.HALF_UP);
        BigDecimal GP = PReversedSystem.divide(Psystem, 2, RoundingMode.HALF_UP);
        BigDecimal GT = BigDecimal.valueOf((double) TReversedSystem / Tsystem).setScale(1, BigDecimal.ROUND_FLOOR);
        System.out.println("GQ = " + GQ);
        System.out.println("GP = " + GP);
        System.out.println("GT = " + GT);
    }

    public static void distributeLoadedOrNotLoaded(String loadOrNot,int K) {
        double[] newP = new double[connection.length];
        double[] newQ = new double[connection.length];

        if (loadOrNot.equalsIgnoreCase("load")){
            System.out.println("Distribute loaded:");
            for (int i = 0; i < P.length; i++) {
                newP[i] = 1 - Math.pow(1 - P[i], K + 1);
                newQ[i] = 1 - newP[i];
            }
        }else if (loadOrNot.equalsIgnoreCase("unload")){
            System.out.println("Distribute unloaded:");
            int factorial = IntStream.rangeClosed(1, K + 1).reduce(1, (x, y) -> x * y);
            for (int i = 0; i < P.length; i++) {
                newP[i] = 1 - (1-P[i])/factorial;
                newQ[i] = 1 - newP[i];
            }
        }

        valuesP.clear();
        makePStates(newP);
        BigDecimal newPsystem = getPsystem();
        BigDecimal newQsystem = BigDecimal.ONE.subtract(newPsystem);
        double newTsystem = (int) -(T / Math.log(Double.parseDouble(newPsystem.toString())));
        System.out.println("PReversedSystem = " + newPsystem);
        System.out.println("QReversedSystem = " + newQsystem);
        System.out.println("TReversedSystem = " + newTsystem);

        BigDecimal GQ = newQsystem.divide(Qsystem, 2, RoundingMode.HALF_UP);
        BigDecimal GP = newPsystem.divide(Psystem, 2, RoundingMode.HALF_UP);
        BigDecimal GT = BigDecimal.valueOf(newTsystem / Tsystem).setScale(1, BigDecimal.ROUND_FLOOR);

        System.out.println("GQ = " + GQ);
        System.out.println("GP = " + GP);
        System.out.println("GT = " + GT);

    }
}