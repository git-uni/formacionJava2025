package com.edisa.formacion.mayo2025.ejercicio1;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.MalformedParametersException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException, WriterException {
        if(args.length < 2){
            throw new MalformedParametersException("El programa debe recibir 2 argumentos: texto del QR a generar y ruta en la que se desea generar el QR.");
        }

        String text = args[0];
        String filePath = args[1];
        int width = 300;
        int height = 300;


        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");


        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        // Crear imagen a partir del BitMatrix
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                boolean bit = matrix.get(x, y);
                image.setRGB(x, y, bit ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }

        // Guardar imagen como JPG
        ImageIO.write(image, "jpg", new File(filePath + "/qr.jpg"));
        System.out.println("QR generado en: " + filePath);

    }
}