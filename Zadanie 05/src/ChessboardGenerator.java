import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChessboardGenerator {

    private String fileName;
    private int sideLength;
    private String colorOfFirstSquare;
    private String colorOfSecondSquare;
    private int m;
    private int n;

    public ChessboardGenerator(String fileName, int m, int n) {
        this.fileName = fileName;
        this.m = m;
        this.n = n;
    }

    /**
     *
     * @param sideLength - length of square side
     * @param colorOfFirstSquare - RGB color of first square
     * @param colorOfSecondSquare - RGB color of second square
     */
    public void init(int sideLength, String colorOfFirstSquare, String colorOfSecondSquare) {
        setSideLength(sideLength);
        setColorOfFirstSquare(colorOfFirstSquare);
        setColorOfSecondSquare(colorOfSecondSquare);

    }

    /**
     * Create output postscript file
     */
    public void createChessboard() {

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(getFileName());

            writer.println("%! Chessboard");

            writer.println("/firstSquare{" + getSideLength() + " 0 rlineto 0 " + getSideLength() + " rlineto -"
                    + getSideLength() + " 0 rlineto closepath " + getColorOfFirstSquare() + " setrgbcolor fill} def");

            writer.println("/secondSquare{" + getSideLength() + " 0 rlineto 0 " + getSideLength() + " rlineto -"
                    + getSideLength() + " 0 rlineto closepath " + getColorOfSecondSquare() + " setrgbcolor fill} def\n");

            for (int i = 0, sideX = 0; i < getM(); i++, sideX += getSideLength()) {
                for(int j = 0, sideY = 0; j < getN(); j++, sideY += getSideLength()) {
                    writer.println(sideX + " " + sideY + " moveto " + (( (j+i) % 2 != 0) ? "secondSquare" : "firstSquare"));
                }
                writer.println("");
            }

            writer.println("showpage");
        } catch (FileNotFoundException e) {
            Logger.getLogger(ChessboardGenerator.class.getName()).log(Level.INFO, null, e);
        } finally {
            if (writer != null){
                writer.close();
            }
        }

    }

    /**
     * execute postscript file
     */
    public void runScript() {
        String cmd = "cmd /c start \"C:\\Software\\gs\\gs9.20\\bin\\gswin64.exe\" " + getFileName();
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            Logger.getLogger(ChessboardGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getFileName() {
        return fileName;
    }

    public int getM() {
        return m;
    }

    public int getN() {
        return n;
    }

    public int getSideLength() {
        return sideLength;
    }

    public void setSideLength(int sideLength) {
        this.sideLength = sideLength;
    }

    public String getColorOfFirstSquare() {
        return colorOfFirstSquare;
    }

    public void setColorOfFirstSquare(String colorOfFirstSquare) {
        this.colorOfFirstSquare = colorOfFirstSquare;
    }

    public String getColorOfSecondSquare() {
        return colorOfSecondSquare;
    }

    public void setColorOfSecondSquare(String colorOfSecondSquare) {
        this.colorOfSecondSquare = colorOfSecondSquare;
    }
}
