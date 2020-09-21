package extrator_caracteristicas;

import java.io.File;
import java.io.FileOutputStream;

import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class ExtractCaracteristicas {


    public static double[] extraiCaracteristicas(File f) {

        double[] caracteristicas = new double[7];

        double cabeloAzulMarge = 0;
        double vestidoVerdeMarge = 0;
        double colarLaranjaMarge = 0;
        double corPeleCarl = 0;
        double casacoCinzaCarl = 0;
        double blusaRosaCarl = 0;

        Image img = new Image(f.toURI().toString());
        PixelReader pr = img.getPixelReader();

        Mat imagemOriginal = Imgcodecs.imread(f.getPath());
        Mat imagemProcessada = imagemOriginal.clone();

        int w = (int) img.getWidth();
        int h = (int) img.getHeight();


        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {

                Color cor = pr.getColor(j, i);

                double r = cor.getRed() * 255;
                double g = cor.getGreen() * 255;
                double b = cor.getBlue() * 255;

                if (i < (h / 2 + h / 6) && isCabeloAzulMarge(r, g, b)) {
                    cabeloAzulMarge++;
                    imagemProcessada.put(i, j, new double[]{0, 255, 0});
                }
                // i > (h / 2)
                if (i > (h / 3 - h / 2) && isVestidoVerdeMarge(r, g, b)) {
                    vestidoVerdeMarge++;
                    imagemProcessada.put(i, j, new double[]{0, 255, 0});
                }
                if (i < (h / 2 + h / 3) && isColarLaranjaMarge(r, g, b)) {
                    colarLaranjaMarge++;
                    imagemProcessada.put(i, j, new double[]{0, 255, 0});
                }
                if (isCorPeleCarl(r, g, b)) {
                    corPeleCarl++;
                    imagemProcessada.put(i, j, new double[]{0, 0, 255});
                }
                if (i > (h / 2) && isCasacoCinzaCarl(r, g, b)) {
                    casacoCinzaCarl++;
                    imagemProcessada.put(i, j, new double[]{0, 0, 255});
                }
                if (i > (h / 2)  && isBlusaRosaCarl(r, g, b)) {
                    blusaRosaCarl++;
                    imagemProcessada.put(i, j, new double[]{0, 0, 255});
                }

            }
        }

        // Normaliza as caracter�sticas pelo n�mero de pixels totais da imagem para %
        cabeloAzulMarge = (cabeloAzulMarge / (w * h)) * 100;
        vestidoVerdeMarge = (vestidoVerdeMarge / (w * h)) * 100;
        colarLaranjaMarge = (colarLaranjaMarge / (w * h)) * 100;
        corPeleCarl = (corPeleCarl / (w * h)) * 100;
        casacoCinzaCarl = (casacoCinzaCarl / (w * h)) * 100;
        blusaRosaCarl = (blusaRosaCarl / (w * h)) * 100;

        caracteristicas[0] = cabeloAzulMarge;
        caracteristicas[1] = vestidoVerdeMarge;
        caracteristicas[2] = colarLaranjaMarge;
        caracteristicas[3] = corPeleCarl;
        caracteristicas[4] = casacoCinzaCarl;
        caracteristicas[5] = blusaRosaCarl;
        //APRENDIZADO SUPERVISIONADO - J� SABE QUAL A CLASSE NAS IMAGENS DE TREINAMENTO
        caracteristicas[6] = f.getParentFile().getName().charAt(0) == 'm' ? 0 : 1;

        HighGui.imshow("Imagem original", imagemOriginal);
        HighGui.imshow("Imagem processada", imagemProcessada);

        HighGui.waitKey(0);

        return caracteristicas;
    }

    public static boolean isCabeloAzulMarge(double r, double g, double b) {
        return b >= 100 && b <= 255 && g >= 40 && g <= 100 && r >= 30 && r <= 95;
    }

    public static boolean isVestidoVerdeMarge(double r, double g, double b) {
        return b >= 75 && b <= 120 && g >= 140 && g <= 206 && r >= 102 && r <= 190;
    }

    public static boolean isColarLaranjaMarge(double r, double g, double b) {
        return b >= 29 && b <= 43 && g >= 52 && g <= 78 && r >= 122 && r <= 183;
    }

    public static boolean isCorPeleCarl(double r, double g, double b) {
        return b >= 18 && b <= 31 && g >= 57 && g <= 99 && r >= 109 && r <= 190;
    }

    public static boolean isCasacoCinzaCarl(double r, double g, double b) {
        return b >= 125 && b <= 188 && g >= 126 && g <= 188 && r >= 122 && r <= 186;
    }

    public static boolean isBlusaRosaCarl(double r, double g, double b) {
        return b >= 84 && b <= 141 && g >= 71 && g <= 131 && r >= 158 && r <= 199;
    }


    public static void extrair() {

        // Cabe�alho do arquivo Weka
        String exportacao = "@relation caracteristicas\n\n";
        exportacao += "@attribute cabeloAzulMarge real\n";
        exportacao += "@attribute vestidoVerdeMarge real\n";
        exportacao += "@attribute colarLaranjaMarge real\n";
        exportacao += "@attribute corPeleCarl real\n";
        exportacao += "@attribute casacoCinzaCarl real\n";
        exportacao += "@attribute blusaRosaCarl real\n";
        exportacao += "@attribute classe {Marge, Carl}\n\n";
        exportacao += "@data\n";


        // Diret�rio onde est�o armazenadas as imagens
        File diretorioMarge = new File("src\\imagens\\marge_simpson");
        File diretorioCarl = new File("src\\imagens\\carl_carlson");
        File[] arquivosMarge = diretorioMarge.listFiles();
        File[] arquivosCarl = diretorioCarl.listFiles();

        // Defini��o do vetor de caracter�sticas
        double[][] caracteristicas = new double[1389][7];

        // Percorre todas as imagens do diret�rio
        int cont = -1;
        for (File imagem : arquivosCarl) {
            cont++;
            caracteristicas[cont] = extraiCaracteristicas(imagem);

            String classe = caracteristicas[cont][6] == 0 ? "Marge" : "Carl";

            System.out.println("Cabelo Azul Marge: " + caracteristicas[cont][0]
                    + " - Vestido Verde Marge: " + caracteristicas[cont][1]
                    + " - Colar Laranja Marge: " + caracteristicas[cont][2]
                    + " - Cor Pele Carl: " + caracteristicas[cont][3]
                    + " - Casaco Cinza Carl: " + caracteristicas[cont][4]
                    + " - Blusa Rosa Carl: " + caracteristicas[cont][5]
                    + " - Classe: " + classe);

            exportacao += caracteristicas[cont][0] + ","
                    + caracteristicas[cont][1] + ","
                    + caracteristicas[cont][2] + ","
                    + caracteristicas[cont][3] + ","
                    + caracteristicas[cont][4] + ","
                    + caracteristicas[cont][5] + ","
                    + classe + "\n";
        }

        for (File imagem : arquivosMarge) {
            cont++;
            caracteristicas[cont] = extraiCaracteristicas(imagem);

            String classe = caracteristicas[cont][6] == 0 ? "Marge" : "Carl";

            System.out.println("Cabelo Azul Marge: " + caracteristicas[cont][0]
                    + " - Vestido Verde Marge: " + caracteristicas[cont][1]
                    + " - Colar Laranja Marge: " + caracteristicas[cont][2]
                    + " - Cor Pele Carl: " + caracteristicas[cont][3]
                    + " - Casaco Cinza Carl: " + caracteristicas[cont][4]
                    + " - Blusa Rosa Carl: " + caracteristicas[cont][5]
                    + " - Classe: " + classe);

            exportacao += caracteristicas[cont][0] + ","
                    + caracteristicas[cont][1] + ","
                    + caracteristicas[cont][2] + ","
                    + caracteristicas[cont][3] + ","
                    + caracteristicas[cont][4] + ","
                    + caracteristicas[cont][5] + ","
                    + classe + "\n";
        }

        // Grava o arquivo ARFF no disco
        try {
            File arquivo = new File("caracteristicas.arff");
            FileOutputStream f = new FileOutputStream(arquivo);
            f.write(exportacao.getBytes());
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
