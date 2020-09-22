package extrator_caracteristicas;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileOutputStream;

public class ExtractCaracteristicas {


    public static double[] extraiCaracteristicas(File f) {
        double[] caracteristicas = new double[7];

        double cabeloAzulMarge = 0;
        double vestidoVerdeMarge = 0;
        double colarLaranjaMarge = 0;
        double corPeleCarl = 0;
        double casacoCinzaCarl = 0;
        double blusaRosaCarl = 0;
        double cabeloAzulKrusty = 0;
        double blusaRosaKrusty = 0;
        double calcaVerdeKrusty = 0;

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

                if (i > (h / 3 - h / 2) && isVestidoVerdeMarge(r, g, b)) {
                    vestidoVerdeMarge++;
                    imagemProcessada.put(i, j, new double[]{0, 255, 0});
                }
                if (i < (h / 2 + h / 3) && isColarLaranjaMarge(r, g, b)) {
                    colarLaranjaMarge++;
                    imagemProcessada.put(i, j, new double[]{0, 255, 0});
                }

                /*
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
                 */

                if (i < (h / 2 + h/4) && isCabeloAzulKrusty(r, g, b)) {
                    cabeloAzulKrusty++;
                    imagemProcessada.put(i, j, new double[]{0, 128, 255});
                }
                if (i > (h / 2) && isBlusaRosaKrusty(r, g, b)) {
                    blusaRosaKrusty++;
                    imagemProcessada.put(i, j, new double[]{0, 128, 255});
                }
                if (i > (h / 2)  && isCalcaVerdeKrusty(r, g, b)) {
                    calcaVerdeKrusty++;
                    imagemProcessada.put(i, j, new double[]{0, 128, 255});
                }

            }
        }

        // Normaliza as caracter�sticas pelo n�mero de pixels totais da imagem para %
        cabeloAzulMarge = (cabeloAzulMarge / (w * h)) * 100;
        vestidoVerdeMarge = (vestidoVerdeMarge / (w * h)) * 100;
        colarLaranjaMarge = (colarLaranjaMarge / (w * h)) * 100;
        cabeloAzulKrusty = (cabeloAzulKrusty / (w * h)) * 100;
        blusaRosaKrusty = (blusaRosaKrusty / (w * h)) * 100;
        calcaVerdeKrusty = (calcaVerdeKrusty / (w * h)) * 100;

        caracteristicas[0] = cabeloAzulMarge;
        caracteristicas[1] = vestidoVerdeMarge;
        caracteristicas[2] = colarLaranjaMarge;
        caracteristicas[3] = cabeloAzulKrusty;
        caracteristicas[4] = blusaRosaKrusty;
        caracteristicas[5] = calcaVerdeKrusty;
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
        return b >= 35 && b <= 43 && g >= 52 && g <= 74 && r >= 122 && r <= 183;
    }

    public static boolean isCabeloAzulKrusty(double r, double g, double b) {
        return b >= 85 && b <= 160 && g >= 105 && g <= 187 && r >= 0 && r <= 55;
    }

    public static boolean isBlusaRosaKrusty(double r, double g, double b) {
        return b >= 120 && b <= 210 && g >= 74 && g <= 145 && r >= 117 && r <= 200;
    }

    public static boolean isCalcaVerdeKrusty(double r, double g, double b) {
        return b >= 1 && b <= 110 && g >= 126 && g <= 225 && r >= 83 && r <= 176;
    }

/*
    public static boolean isCorPeleCarl(double r, double g, double b) {
        return b >= 0 && b <= 30 && g >= 30 && g <= 80 && r >= 90 && r <= 170;
    }

    public static boolean isCasacoCinzaCarl(double r, double g, double b) {
        return b >= 100 && b <= 170 && g >= 100 && g <= 150 && r >= 85 && r <= 160;
    }

    public static boolean isBlusaRosaCarl(double r, double g, double b) {
        return b >= 84 && b <= 141 && g >= 71 && g <= 131 && r >= 158 && r <= 199;
    }
 */


    public static void extrair() {

        // Cabe�alho do arquivo Weka
        String exportacao = "@relation caracteristicas\n\n";
        exportacao += "@attribute cabeloAzulMarge real\n";
        exportacao += "@attribute vestidoVerdeMarge real\n";
        exportacao += "@attribute colarLaranjaMarge real\n";
        /*
        exportacao += "@attribute corPeleCarl real\n";
        exportacao += "@attribute casacoCinzaCarl real\n";
        exportacao += "@attribute blusaRosaCarl real\n";
         */

        exportacao += "@attribute cabeloAzulKrusty real\n";
        exportacao += "@attribute blusaRosaKrusty real\n";
        exportacao += "@attribute calcaVerdeKrusty real\n";

        exportacao += "@attribute classe {Marge, Krusty}\n\n";
        exportacao += "@data\n";


        // Diret�rio onde est�o armazenadas as imagens
        File diretorioMarge = new File("src\\imagens\\marge_simpson");
        File[] arquivosMarge = diretorioMarge.listFiles();

        File diretorioKrusty = new File("src\\imagens\\krusty_the_clown");
        File[] arquivosKrusty = diretorioKrusty.listFiles();

        //File diretorioCarl = new File("src\\imagens\\carl_carlson");
        //File[] arquivosCarl = diretorioCarl.listFiles();

        // Defini��o do vetor de caracter�sticas
        double[][] caracteristicas = new double[1389][7];

        // Percorre todas as imagens do diret�rio
        int cont = -1;
        //percorrerImagensCarl(arquivosCarl, cont, caracteristicas, exportacao);
        percorrerImagensKrusty(arquivosKrusty, cont, caracteristicas, exportacao);
        percorrerImagensMarge(arquivosMarge, cont, caracteristicas, exportacao);

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

    private static void percorrerImagensKrusty(File[] arquivosKrusty, int cont, double[][] caracteristicas, String exportacao) {
        for (File imagem : arquivosKrusty) {
            cont++;
            caracteristicas[cont] = extraiCaracteristicas(imagem);

            String classe = caracteristicas[cont][6] == 0 ? "Marge" : "Krusty";

            System.out.println("Cabelo Azul Marge: " + caracteristicas[cont][0]
                    + " - Vestido Verde Marge: " + caracteristicas[cont][1]
                    + " - Colar Laranja Marge: " + caracteristicas[cont][2]
                    + " - Cabelo Azul Krusty: " + caracteristicas[cont][3]
                    + " - Blusa Rosa Krusty: " + caracteristicas[cont][4]
                    + " - Calca Verde Krusty: " + caracteristicas[cont][5]
                    + " - Classe: " + classe);

            exportacao += caracteristicas[cont][0] + ","
                    + caracteristicas[cont][1] + ","
                    + caracteristicas[cont][2] + ","
                    + caracteristicas[cont][3] + ","
                    + caracteristicas[cont][4] + ","
                    + caracteristicas[cont][5] + ","
                    + classe + "\n";
        }
    }

    public static void percorrerImagensCarl(File[] arquivosCarl, int cont, double[][] caracteristicas, String exportacao) {
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
    }

    public static void percorrerImagensMarge(File[] arquivosMarge, int cont, double[][] caracteristicas, String exportacao) {
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
    }

}
