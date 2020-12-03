import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Model {
    private int n;
    private int m;
    private double e;
    private double E;
    private int p;
    private BufferedImage image;
    private BufferedImage restoredImage;
    private List<Rectangle> rectangles;
    private double a;
    private int iteration;
    private Matrix W;
    private Matrix W_;
    private Matrix X;
    private Matrix X_;
    private Matrix Y;
    private Matrix deltaX;

    public void start() {
        splitImageIntoRectangles();
        createFirstLayerWeightsMatrix();
        createSecondLayerWeightsMatrix();
        learn();
        restoreImage();
    }

    public void setN(int n) {
        this.n = n;
    }

    public void setM(int m) {
        this.m = m;
    }

    public void setE(double e) {
        this.e = e;
    }

    public void setA(double a) {
        this.a = a;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void setP(int p) {
        this.p = p;
    }

    private void splitImageIntoRectangles() {
        rectangles = new ArrayList<>();
        int x = 0;
        while (x < image.getWidth()) {
            int y = 0;
            while (y < image.getHeight()) {
                Rectangle rectangle = new Rectangle(x, y);
                for (int i = x; i < x + n; i++) {
                    for (int j = y; j < y + m; j++) {
                        if (i < image.getWidth() && j < image.getHeight()) {
                            Color color = new Color(image.getRGB(i, j));
                            rectangle.addPixel(color);
                        } else {
                            rectangle.add(-1);
                            rectangle.add(-1);
                            rectangle.add(-1);
                        }
                    }
                }
                rectangle.createVectorX0();
                rectangles.add(rectangle);
                y = y + m;
            }
            x = x + n;
        }

    }

    private void createFirstLayerWeightsMatrix() {
        double randomWeights[][] = new double[n * m * 3][p];
        for (int row = 0; row < n * m * 3; row++) {
            for (int column = 0; column < p; column++) {
                randomWeights[row][column] = Math.random() * 2 - 1;
            }
        }
        W = new Matrix(randomWeights);
    }

    private void createSecondLayerWeightsMatrix() {
        W_ = W.transpose();
    }

    private void learn() {
        iteration = 0;
        E = Double.MAX_VALUE;

        while (E > e) {
            E = 0;
            for (Rectangle pattern : rectangles) {
                X = pattern.getVectorX0();
                Y = X.multiply(W);
                X_ = Y.multiply(W_);
                deltaX = X_.subtract(X);

                W = W.subtract(X.transpose().multiply(a).multiply(deltaX).multiply(W_.transpose()));
                W_ = W_.subtract(Y.transpose().multiply(a).multiply(deltaX));
                normaliseWeights();

                E += calculateError();
            }
            iteration++;
            System.out.println("Iteration = " + iteration + "; Error = " + E);
        }
        /*
        System.out.println("First layer weights: \n ");
        W.print();
        System.out.println("Second layer weights: \n ");
        W_.print();
        */
    }

    private void normaliseWeights() {
        for (int i = 0; i < W.getMatrix().length; i++) {
            double value = 0;
            for (int j = 0; j < W.getMatrix()[0].length; j++) {
                value += W.getMatrix()[i][j] * W.getMatrix()[i][j];
            }
            value = Math.sqrt(value);
            for (int j = 0; j < W.getMatrix()[0].length; j++) {
                W.getMatrix()[i][j] = W.getMatrix()[i][j] / value;
            }
        }
        for (int i = 0; i < W_.getMatrix().length; i++) {
            double value = 0;
            for (int j = 0; j < W_.getMatrix()[0].length; j++) {
                value += W_.getMatrix()[i][j] * W_.getMatrix()[i][j];
            }
            value = Math.sqrt(value);
            for (int j = 0; j < W_.getMatrix()[0].length; j++) {
                W_.getMatrix()[i][j] = W_.getMatrix()[i][j] / value;
            }
        }
    }

    private double calculateError() {
        double error = 0;
        for (int i = 0; i < X.getMatrix()[0].length; i++) {
            error += deltaX.getMatrix()[0][i] * deltaX.getMatrix()[0][i];
        }
        return error;
    }

    private double restorePixel(double RGB) {
        double value = 255 * (RGB + 1) / 2;
        if (value < 0) {
            value = 0;
        } else if (value > 255) {
            value = 255;
        }
        return value;
    }

    private void restoreImage() {
        restoredImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        for (Rectangle rectangle : rectangles) {
            X = rectangle.getVectorX0();
            Y = X.multiply(W);
            X_ = Y.multiply(W_);
            int x = rectangle.getX();
            int y = rectangle.getY();
            int pixelPosition = 0;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    int red = (int) restorePixel(X_.getMatrix()[0][pixelPosition++]);
                    int green = (int) restorePixel(X_.getMatrix()[0][pixelPosition++]);
                    int blue = (int) restorePixel(X_.getMatrix()[0][pixelPosition++]);
                    Color color = new Color(red, green, blue);
                    if (x + i < image.getWidth()) {
                        if (y + j < image.getHeight()) {
                            restoredImage.setRGB(x + i, y + j, color.getRGB());
                        }
                    }
                }
            }
        }
    }

    public BufferedImage getRestoredImage() {
        return restoredImage;
    }

    public int getNumOfRectangles() {
        return rectangles.size();
    }

    public int getIteration() {
        return iteration;
    }

    public double getE() {
        return E;
    }
}
