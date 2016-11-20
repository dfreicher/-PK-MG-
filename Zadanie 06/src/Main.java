public class Main {

    public static void main(String[] args) {
        String inputFileName = args[0];
        String outputFileName = args[1];

        ChartGenerator generator = new ChartGenerator(inputFileName, outputFileName);
        generator.init();
        generator.createChart();
        generator.runScript();
    }
}
