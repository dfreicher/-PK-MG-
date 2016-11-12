public class Main {

    public static void main(String[] args) {
        String fileName = args[0];
        int n = Integer.parseInt(args[1]);
        int m = Integer.parseInt(args[2]);

        ChessboardGenerator generator = new ChessboardGenerator(fileName, n, m);
        generator.init(30, "0.2 0.9 0.7", "0.1 0.3 0.5");
        generator.createChessboard();
        generator.runScript();
    }
}
