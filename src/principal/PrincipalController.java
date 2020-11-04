package principal;

import extrator_caracteristicas.AprendizagemBayesiana;
import extrator_caracteristicas.Perceptron;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.text.DecimalFormat;

public class PrincipalController {

    @FXML
    private ImageView imageView;

    @FXML
    private Text cabeloAzulMarge;

    @FXML
    private Text vestidoVerdeMarge;

    @FXML
    private Text colarLaranjaMarge;

    @FXML
    private Text cabeloAzulKrusty;

    @FXML
    private Text blusaRosaKrusty;

    @FXML
    private Text calcaVerdeKrusty;

    @FXML
    private Text margeNaive;

    @FXML
    private Text krustyNaive;

    @FXML
    private Text margeJ48;

    @FXML
    private Text krustyJ48;

    @FXML
    private Text probabilidadeCachorro;

    @FXML
    private Text probabilidadeGato;

    private double[] caracteristicasImgSel;

    private DecimalFormat df = new DecimalFormat("##0.0000");

    private Perceptron perceptron = new Perceptron();

    @FXML
    public void perceptron() throws Exception {
        perceptron.extrair();
        File som = buscaSom();

        double[] p = perceptron.perceptron(som);
        double[] pmn = perceptron.perceptronMultilayerNetwork(p);
        probabilidadeGato.setText(df.format(pmn[1] * 100) + "%");
        probabilidadeCachorro.setText(df.format(pmn[0] * 100) + "%");
    }

    @FXML
    public void extrairCaracteristicas() {
        AprendizagemBayesiana.extrair();
    }

    private void utilizaNaiveBayes() {
        if (caracteristicasImgSel.length != 0) {
            double[] nb = AprendizagemBayesiana.naiveBayes(caracteristicasImgSel);
            margeNaive.setText("Marge: " + format(nb[0], 100) + "%");
            krustyNaive.setText("Krusty: " + format(nb[1], 100) + "%");
        }
    }

    private void utilizaj48() {
        if (caracteristicasImgSel.length != 0) {
            double[] nb = AprendizagemBayesiana.j48(caracteristicasImgSel);
            margeJ48.setText("Marge: " + format(nb[0], 100) + "%");
            krustyJ48.setText("Krusty: " + format(nb[1], 100) + "%");
        }
    }

    @FXML
    public void selecionaImagem() {
        File f = buscaImg();
        if (f != null) {
            Image img = new Image(f.toURI().toString());
            imageView.setImage(img);
            imageView.setFitWidth(img.getWidth());
            imageView.setFitHeight(img.getHeight());
            double[] caracteristicas = AprendizagemBayesiana.extraiCaracteristicas(f);
            for (double d : caracteristicas) {
                System.out.println(d);
            }
            caracteristicasImgSel = caracteristicas;
            caracteristicas();
            utilizaNaiveBayes();
            utilizaj48();
        }
    }

    private void caracteristicas() {
        cabeloAzulMarge.setText("Cabelo Azul: " + format(caracteristicasImgSel[0]) + "%");
        vestidoVerdeMarge.setText("Vestido Verde: " + format(caracteristicasImgSel[1]) + "%");
        colarLaranjaMarge.setText("Colar Laranja: " + format(caracteristicasImgSel[2]) + "%");
        cabeloAzulKrusty.setText("Cabelo Azul: " + format(caracteristicasImgSel[3]) + "%");
        blusaRosaKrusty.setText("Blusa Rosa: " + format(caracteristicasImgSel[4]) + "%");
        calcaVerdeKrusty.setText("Calça Verde: " + format(caracteristicasImgSel[5]) + "%");
    }

    private String format(double number, double multiplier) {
        return df.format(number * multiplier);
    }

    private String format(double number) {
        return df.format(number);
    }

    private File buscaImg() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new
                FileChooser.ExtensionFilter(
                "Imagens", "*.jpg", "*.JPG",
                "*.png", "*.PNG", "*.gif", "*.GIF",
                "*.bmp", "*.BMP"));
        fileChooser.setInitialDirectory(new File("src/imagens"));
        File imgSelec = fileChooser.showOpenDialog(null);
        try {
            if (imgSelec != null) {
                return imgSelec;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private File buscaSom() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new
                FileChooser.ExtensionFilter(
                "Sons", "*.wav", "*.WAV"));
        fileChooser.setInitialDirectory(new File("src/dataset"));
        File somSelec = fileChooser.showOpenDialog(null);
        try {
            if (somSelec != null) {
                return somSelec;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
