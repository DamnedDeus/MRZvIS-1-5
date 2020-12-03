import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Processing {
    private int m;
    private int n;
    private int p;
    private double e;
    private double a;
    private int L;
    BufferedImage image;
    BufferedImage restoredImage;
    Model model;

    public Processing() {
        inputImage();
        inputParameters();
        setParametersToModel();
        model.start();
        saveImage();
        printParameters();
        System.exit(0);
    }

    private void inputImage() {
        JFrame frame = new JFrame();
        JFileChooser chosenFile = new JFileChooser("images");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.png", new String[]{"png"});
        chosenFile.setFileFilter(filter);
        int ret = chosenFile.showOpenDialog(frame);
        if (ret == 0) {
            File file = chosenFile.getSelectedFile();
            try {
                image = ImageIO.read(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void inputParameters() {
        Scanner scanner = new Scanner(System.in, "UTF-8");
        System.out.println("Rectangle's width (n) :" );
        this.n = scanner.nextInt();
        System.out.println("Rectangle's height (m) :");
        this.m = scanner.nextInt();
        System.out.println("Number of neurons on the second layer (p <= " + (m * n * 6) + ") :");
        this.p = scanner.nextInt();
        System.out.println("Max error value (e <= " + (0.1 * p) + ") :");
        this.e = scanner.nextDouble();
        System.out.println("Learning rate (0 < a <= 0.01) :");
        this.a = scanner.nextDouble();
    }

    public void printParameters() {
        int N = n * m * 3;
        L = model.getNumOfRectangles();
        double Z = (N * this.L) / ((N + this.L) * this.p + 2.0);
        //System.out.println("n = " + this.n);
        //System.out.println("m = " + this.m);
        //System.out.println("p = " + this.p);
        System.out.println("Z = " + Z);
        System.out.println("E = " + model.getE());
        System.out.println("L = " + this.L);
        System.out.println("Iteration = " + model.getIteration());
    }

    private void setParametersToModel(){
        this.model = new Model();
        model.setImage(image);
        model.setE(e);
        model.setP(p);
        model.setM(m);
        model.setN(n);
        model.setA(a);
    }

    private void saveImage(){
        restoredImage = model.getRestoredImage();
        File file = new File("output.png");
        try{
            ImageIO.write(restoredImage,"png", file);
        } catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Image saved");
    }

}
