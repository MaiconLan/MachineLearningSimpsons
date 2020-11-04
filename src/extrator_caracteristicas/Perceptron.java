package extrator_caracteristicas;

import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class Perceptron {

    private static final Double momentum = 0.2;
    private static final String camadas = "a,a";
    private static final Integer tempo = 500;
    private static final Double aprendizado = 0.3;

    public void extrair() throws IOException {

        String exportacao = "@relation caracteristicas\n\n";
        exportacao += "@attribute amplitudeCachorro real\n";
        exportacao += "@attribute amplitudeGato real\n";
        exportacao += "@attribute classe {Cachorro, Gato}\n\n";
        exportacao += "@data\n";

        File diretorioGatos = new File("src/train/cat");
        File diretorioCachorros = new File("src/train/dog");

        List<File> arquivos = Arrays.asList(diretorioGatos.listFiles());
        arquivos.addAll(Arrays.asList(diretorioCachorros.listFiles()));

        double[][] caracteristicas = new double[210][3];

        int cont = -1;
        for (File som : arquivos) {
            cont++;
            caracteristicas[cont] = extractAmplitudeFromFile(som);

            String classe = caracteristicas[cont][2] == 0 ? "Cachorro" : "Gato";

            System.out.println(
                    "amplitudeCachorro: " + caracteristicas[cont][0]
                            + " - amplitudeGato: " + caracteristicas[cont][1] +
                            " - Classe: " + classe);

            exportacao += caracteristicas[cont][0] + ","
                    + caracteristicas[cont][1] + ","
                    + classe + "\n";
        }

        try {
            File arquivo = new File("caracteristicas.arff");
            FileOutputStream f = new FileOutputStream(arquivo);
            f.write(exportacao.getBytes());
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double[] extractAmplitudeFromFile(File som) throws IOException {
        double[] caracteristicas = new double[3];
        double amplitudeCachorro = 0;
        double amplitudeGato = 0;

        byte[] bytes = Files.readAllBytes(som.toPath());

        int tamanho = bytes.length / 4;
        double[] mono = new double[tamanho];

        double esquerda;
        double direita;
        for (int i = 0; 4 * i + 3 < tamanho; i++) {
            esquerda = (short) ((bytes[4 * i + 1] & 0xff) << 8) | (bytes[4 * i] & 0xff);
            direita = (short) ((bytes[4 * i + 3] & 0xff) << 8) | (bytes[4 * i + 2] & 0xff);
            mono[i] = (esquerda + direita) / 2.0;
        }

        int WS = 2048;
        int OF = 8;
        int windowStep = WS / OF;

        int nX = (tamanho - WS) / windowStep;
        int nY = WS / 2 + 1;
        double[][] plotData = new double[nX][nY];

        double maxAmp = Double.MIN_VALUE;
        double minAmp = Double.MAX_VALUE;

        double amplitudeMedia = 0;

        double ampSquare;

        double[] inputImag = new double[tamanho];
        double threshold = 1.0;

        for (int i = 0; i < nX; i++) {
            Arrays.fill(inputImag, 0.0);
            double[] ws = FFT.fft(Arrays.copyOfRange(mono, i * windowStep, i * windowStep + WS), inputImag, true);
            for (int j = 0; j < nY; j++) {
                ampSquare = (ws[2 * j] * ws[2 * j]) + (ws[2 * j + 1] * ws[2 * j + 1]);
                if (ampSquare == 0.0) {
                    plotData[i][j] = ampSquare;
                } else {
                    plotData[i][nY - j - 1] = 10 * Math.log10(Math.max(ampSquare, threshold));
                }

                if (plotData[i][j] > maxAmp)
                    maxAmp = plotData[i][j];
                else if (plotData[i][j] < minAmp)
                    minAmp = plotData[i][j];

                amplitudeMedia += plotData[i][j];

            }
        }

        amplitudeMedia = amplitudeMedia / plotData.length;

        if (amplitudeMedia > 28000)
            amplitudeCachorro = amplitudeMedia;
        else
            amplitudeGato = amplitudeMedia;

        caracteristicas[0] = amplitudeCachorro;
        caracteristicas[1] = amplitudeGato;

        if (som.getName().contains("dog"))
            caracteristicas[2] = 0;
        else if (som.getName().contains("cat"))
            caracteristicas[2] = 1;

        gerarImagem(som, nX, nY, maxAmp, minAmp, plotData);

        return caracteristicas;

    }

    public void gerarImagem(File som, int nX, int nY, double maxAmp, double minAmp, double plotData[][]) {
        try {
            double diff = maxAmp - minAmp;
            for (int i = 0; i < nX; i++) {
                for (int j = 0; j < nY; j++) {
                    plotData[i][j] = (plotData[i][j] - minAmp) / diff;
                }
            }

            BufferedImage theImage = new BufferedImage(nX, nY, BufferedImage.TYPE_INT_RGB);
            double ratio;
            for (int x = 0; x < nX; x++) {
                for (int y = 0; y < nY; y++) {
                    ratio = plotData[x][y];

                    double H = (1.0 - ratio) * 0.4;
                    double S = 1.0;
                    double B = 1.0;

                    Color newColor = Color.getHSBColor((float) H, (float) S, (float) B);
                    theImage.setRGB(x, y, newColor.getRGB());
                }
            }

            File outputfile = new File(som.getName() + ".png");
            ImageIO.write(theImage, "png", outputfile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double[] perceptron(File arquivo) throws Exception {
        ConverterUtils.DataSource ds = new ConverterUtils.DataSource("caracteristicas.arff");
        Instances instancias = ds.getDataSet();

        instancias.setClassIndex(instancias.numAttributes() - 1);

        MultilayerPerceptron perceptron = new MultilayerPerceptron();

        perceptron.setHiddenLayers(camadas);
        perceptron.setMomentum(momentum);
        perceptron.setLearningRate(aprendizado);
        perceptron.setTrainingTime(tempo);

        perceptron.buildClassifier(instancias);

        ArffLoader arffLoader = new ArffLoader();
        arffLoader.setFile(arquivo);
        Instance instance = arffLoader.getDataSet().get(0);
        instance.setDataset(instancias);

        return perceptron.distributionForInstance(instance);
    }

    public double[] perceptronMultilayerNetwork(double[] caracteristicas) {
        double[] retorno = {0, 0, 0, 0};

        try {
            FileReader trainreader = new FileReader("caracteristicas.arff");
            Instances train = new Instances(trainreader);
            train.setClassIndex(train.numAttributes() - 1);
            MultilayerPerceptron mlp = new MultilayerPerceptron();
            mlp.setLearningRate(aprendizado);
            mlp.setMomentum(momentum);
            mlp.setTrainingTime(tempo);
            mlp.setHiddenLayers(camadas);
            mlp.buildClassifier(train);

            MultilayerPerceptron network = new MultilayerPerceptron();
            network.buildClassifier(train);

            Instance novo = new DenseInstance(train.numAttributes());
            novo.setDataset(train);
            novo.setValue(0, caracteristicas[0]);
            novo.setValue(1, caracteristicas[1]);
            novo.setValue(2, caracteristicas[2]);
            novo.setValue(3, caracteristicas[3]);

            retorno = network.distributionForInstance(novo);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return retorno;
    }
}
