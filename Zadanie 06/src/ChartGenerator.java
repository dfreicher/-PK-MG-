import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChartGenerator {

    private String inputFileName;
    private String outputFileName;
    private List<String> data;

    private final Map<String, Double[]> dataOfChart = new HashMap<>();
    private final Map<String, Double[]> coloursOfChart = new HashMap<>();

    private static final int centerOfChartX = 200;
    private static final int centerOfChartY = 600;
    private static final int radiousOfChart = 150;

    public ChartGenerator(String inputFileName, String outputFileName) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
    }

    public void init() {
        readDataFromFile();
        calculatePartsOfChart(splitData());
        randomColours();
    }

    /**
     * read data from input file
     */
    private void readDataFromFile() {
        Scanner in = null;
        data = new ArrayList<String>();

        try {
            in = new Scanner(new File(getInputFileName()));

            while(in.hasNextLine()) {
                String line = in.nextLine().toLowerCase();
                data.add(line);
            }

            in.close();
        } catch (FileNotFoundException e) {
            Logger.getLogger(ChartGenerator.class.getName()).log(Level.INFO, null, e);
        }
    }

    /**
     * splitting data
     */
    private double splitData() {
        double sumOfPercentages = 0;

        for (String line : getData()) {
            String elements[] = line.split("\\s+");
            String key = elements[0];
            for (int i = 1; i < elements.length - 1; i++) {
                key += " " + elements[i];
            }

            dataOfChart.put(key, new Double[] {0.0, Double.valueOf(elements[elements.length-1])});
            sumOfPercentages += Double.valueOf(elements[elements.length-1]);
        }

        return sumOfPercentages;
    }

    private void calculatePartsOfChart(double sumOFPercentages) {
        for (Map.Entry<String, Double[]> row : dataOfChart.entrySet()) {
            double percentagePart = ( row.getValue()[1] / sumOFPercentages) * 100;
            double angle = ( row.getValue()[1] / sumOFPercentages ) * 360;
            dataOfChart.put(row.getKey(), new Double[] {angle,percentagePart});
        }
    }

    /**
     * Create output postscript file
     */
    public void createChart() {

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(getOutputFileName());
            writer.println("%! Chart");
            generateChart(writer);
            generateLegendHeader(writer);
            generateLegend(writer);
            writer.println("showpage");
        } catch (FileNotFoundException e) {
            Logger.getLogger(ChartGenerator.class.getName()).log(Level.INFO, null, e);
        } finally {
            if (writer != null){
                writer.close();
            }
        }

    }

    /**
     * random colours for chart
     */
    private void randomColours() {
        for (Map.Entry<String, Double[]> row : dataOfChart.entrySet()) {
            Double[] colours;

            do {
                colours = new Double[3];
                Random random = new Random();
                colours[0] = (double) Math.round(random.nextDouble()*4)/4;
                colours[1] = (double) Math.round(random.nextDouble()*4)/4;
                colours[2] = (double) Math.round(random.nextDouble()*4)/4;
            } while (isColorUsed(colours));

            coloursOfChart.put(row.getKey(), colours);
        }
    }

    /**
     *
     * @param RGB
     * @return check if color is used
     */
    private boolean isColorUsed(Double[] RGB) {
        for (Map.Entry<String, Double[]> row : coloursOfChart.entrySet()) {
            Double[] value = row.getValue();
            if(value[0] == RGB[0] && value[1] == RGB[1] && value[2] == RGB[2]) {
                return true;
            }
        }
        return false;
    }

    /**
     * execute postscript file
     */
    public void runScript() {
        String cmd = "cmd /c start \"C:\\Software\\gs\\gs9.20\\bin\\gswin64.exe\" " + getOutputFileName();
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            Logger.getLogger(ChartGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Generate main part of chart
     */
    private void generateChart(PrintWriter writer) {
        double startAngle = 0;
        double endAngle;
        for (Map.Entry<String, Double[]> row : dataOfChart.entrySet()) {
            endAngle = startAngle + row.getValue()[0];
            writer.println("% " + row.getKey() + " %");
            writer.println("newpath\n" + centerOfChartX + " " + centerOfChartY + " moveto");
            writer.println(centerOfChartX + " " + centerOfChartY + " " + radiousOfChart + " " + startAngle + " " + endAngle + " arc");
            writer.println("closepath\ngsave");
            Double colours[] = coloursOfChart.get(row.getKey());
            writer.println(colours[0] + " " + colours[1] + " " + colours[2] + " setrgbcolor\nfill\ngrestore\nstroke");
            writer.println("%\n");
            startAngle = endAngle;
        }
    }

    /**
     * Generate Legend for chart
     */
    private void generateLegend(PrintWriter writer) {
        int axisY = ((centerOfChartY - radiousOfChart - 60) - 25);
        for (Map.Entry<String, Double[]> wiersz : dataOfChart.entrySet()) {
            writer.println((centerOfChartX - radiousOfChart) + " " + axisY +  " moveto");
            Double colours[] = coloursOfChart.get(wiersz.getKey());
            writer.println(colours[0] + " " + colours[1] + " " + colours[2] + " setrgbcolor");
            writer.println("(" + wiersz.getKey() + ") show" );
            writer.println("0 0 0 setrgbcolor");
            writer.format("(   %.2f%%) show\n\n", wiersz.getValue()[1]);
            axisY -= 15;
        }
    }

    /**
     * Generate header of legend
     * @param writer
     */
    private void generateLegendHeader(PrintWriter writer) {
        writer.println("%\n%% LEGEND %%\n%");
        writer.println("/Times-Bold findfont\n" + 25 + " scalefont\nsetfont\nnewpath");
        writer.println((centerOfChartX - radiousOfChart) + " " + (centerOfChartY - radiousOfChart - 60) + " moveto");
        writer.println("(LEGEND:) show");
        writer.println("\n/Verdana findfont\n" + 15 + " scalefont\nsetfont\n");
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public List<String> getData() {
        return data;
    }
}
