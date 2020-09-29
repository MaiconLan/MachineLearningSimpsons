package principal;

import extrator_caracteristicas.AprendizagemBayesiana;
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
    private Text marge;

    @FXML
    private Text krusty;

    private double[] caracteristicasImgSel;

    private DecimalFormat df = new DecimalFormat("##0.0000");

    @FXML
    public void extrairCaracteristicas() {
        AprendizagemBayesiana.extrair();
    }

    @FXML
    public void utilizaNaiveBayes() {
        if (caracteristicasImgSel.length != 0) {
            double[] nb = AprendizagemBayesiana.naiveBayes(caracteristicasImgSel);
            marge.setText("Marge: " + df.format(nb[0] * 100) + "%");
            krusty.setText("Krusty: " + df.format(nb[1] * 100) + "%");
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
        }
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

}
