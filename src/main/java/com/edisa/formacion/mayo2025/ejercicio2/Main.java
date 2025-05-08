package com.edisa.formacion.mayo2025.ejercicio2;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.MalformedParametersException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException, WriterException {
        if(args.length < 3){
            throw new MalformedParametersException("El programa debe recibir 3 argumentos: texto del QR a generar , ruta en la que se desea generar el QR y formato del QR");
        }

        String text = args[0];
        String filePath = args[1];
        String formatStr = args[2].toUpperCase();
        int width = 300;
        int height = 300;

        BarcodeFormat format;
        try {
            format = BarcodeFormat.valueOf(formatStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Formato no válido: " + formatStr + ". Usa QR_CODE, EAN_13, CODE_128, etc.");
        }

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix matrix = new MultiFormatWriter().encode(text, format, width, height, hints);

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