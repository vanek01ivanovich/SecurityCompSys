import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static List<Integer> sortedSelection;
    public static List<Double> rangeParts = new ArrayList<>();
    public static int parts = 10;
    public static double h;
    public static List<BigDecimal> probabilityNotRejectList = new ArrayList<>();
    public static BigDecimal p = BigDecimal.valueOf(1);
    public static BigDecimal pt = BigDecimal.ZERO;
    public static BigDecimal valueForReturn = BigDecimal.ZERO;
    public static BigDecimal d;
    public static double ty;
    public static final double y = 0.75;
    public static List<BigDecimal> probabilityRejectList = new ArrayList<>();

    public static void main(String[] args) {
        BigDecimal pti;
        BigDecimal pti_1;
        BigDecimal lambda;

        int probability = 44;
        int intensity = 810;
        double ti;
        double tcp;

        Integer[] rejectSelection = {25, 80, 157, 39, 372, 45, 108, 549, 1771,
                                969, 508, 1134, 90, 382, 413, 444, 329, 158,
                                551, 536, 216, 337, 493, 12, 1, 514, 88, 243,
                                56, 521, 231, 301, 1120, 528, 513, 95, 79,
                                460, 41, 383, 223, 39, 51, 625, 346, 11, 26,
                                645, 377, 169, 88, 396, 126, 269, 962, 38,
                                850, 2, 80, 73, 65, 253, 180, 80, 553, 150,
                                808, 412, 384, 199, 640, 688, 613, 70, 227,
                                481, 238, 253, 207, 879, 182, 670, 146, 453,
                                502, 206, 94, 7, 28, 17, 31, 34, 136, 659,209,
                                143, 652, 119, 115, 259};

        if(rejectSelection.length == 0){
            System.out.println("Не може бути пустого масиву!!!");
            System.exit(1);
        }

        sortedSelection = new ArrayList<>(Arrays.asList(rejectSelection));
        sortedSelection = sortedSelection.stream().sorted().collect(Collectors.toList());
        System.out.println("Відсортована вибірка = " + sortedSelection.toString());

        tcp = sortedSelection.stream().mapToInt(i -> i).average().orElse(0);
        System.out.println("Tcp = " + tcp);

        h = (double) sortedSelection.get(sortedSelection.size() - 1)/ parts;
        System.out.println("Довжина одного інтервалу = " + h);

        rangeParts.add(0.0);
        IntStream.rangeClosed(1,parts).forEach(i -> {
            rangeParts.add(i*h);
            probabilityRejectList.add(probabilityReject(i).setScale(7,BigDecimal.ROUND_CEILING));
        });
        System.out.println("Границі інтервалів = " + rangeParts.toString());
        System.out.println("Значення статистичної щільності розподілу ймовірності відмови = " + probabilityRejectList.toString());

        IntStream.rangeClosed(1,parts).forEach(i -> {
            p = probabilityNotReject(i,probabilityRejectList.get(i-1));
            probabilityNotRejectList.add(p.setScale(5,BigDecimal.ROUND_CEILING));
        });
        System.out.println("Значення ймовірності безвідмовної роботи = " + probabilityNotRejectList.toString());

        pti = probabilityNotRejectList.stream().filter(i -> BigDecimal.valueOf(y).compareTo(i) > 0).findFirst().orElse(BigDecimal.ZERO);
        pti_1 = probabilityNotRejectList.stream().filter(i -> BigDecimal.valueOf(y).compareTo(i) < 0).findFirst().orElse(BigDecimal.ONE);

        d = pti.subtract(BigDecimal.valueOf(y)).divide(pti.subtract(pti_1),7, BigDecimal.ROUND_CEILING);
        System.out.println("d = " + d);

        ti = h * (probabilityNotRejectList.indexOf(pti) + 1);
        ty = ti - h * d.doubleValue();
        System.out.println("Ty = " + ty);

        int maxRange = (int)rangeParts.stream().filter(i -> i <= intensity).count();
        pt = getIntensityOrProbability(probability,maxRange);
        pt = BigDecimal.ONE.subtract(pt);
        System.out.println("Ймовірність безвідмовної роботи на час " + probability + " годин = " + pt);

        lambda = probabilityRejectList.get(maxRange-1).divide(getIntensityOrProbability(intensity,maxRange),7,BigDecimal.ROUND_CEILING);
        System.out.println("Інтенсивність відмов на час " + intensity + " годин = " + lambda);
    }

    private static BigDecimal getIntensityOrProbability(int value,int maxRange){
        IntStream.range(0,maxRange)
                .forEach(i -> {
                    if (i == maxRange -1 ){
                        valueForReturn = valueForReturn.add(probabilityRejectList.get(i)
                                .multiply(BigDecimal.valueOf(value).subtract(BigDecimal.valueOf(rangeParts.get(i)))));
                    }else{
                        valueForReturn = valueForReturn.add(BigDecimal.valueOf(h).multiply(probabilityRejectList.get(i)));
                    }
                });
        return valueForReturn;
    }

    private static BigDecimal probabilityReject(int part){
        return BigDecimal.valueOf(sortedSelection.stream()
                .filter(i -> (i <= part * h && i >= part * h - h))
                .count() / (sortedSelection.size() * h));
    }

    private static BigDecimal probabilityNotReject(int part, BigDecimal probabilityReject){
        double pInteger = part*h;
        double pIntegerSecond = part*h-h;
        BigDecimal firstValue = BigDecimal.valueOf(pInteger).multiply(probabilityReject);
        BigDecimal secondValue = BigDecimal.valueOf(pIntegerSecond).multiply(probabilityReject);
        return p.subtract(firstValue.subtract(secondValue));
    }
}
