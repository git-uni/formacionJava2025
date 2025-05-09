package com.edisa.formacion.mayo2025.dropwizard;

import com.google.zxing.*;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;

@Path("/api")
public class Recursos {

    @GET
    @Path("/saludo")
    public Response saludar(@QueryParam("nombre") String nombre,
                            @QueryParam("apellido") String apellido,
                            @QueryParam("edad") int edad) {

        //    return Response.status(404).build();
        return Response.ok("hola").build();
    }

    @POST
    @Path("/saludo/post")
    public String saludar_post(@QueryParam("nombre") String nombre,
                               @QueryParam("apellido") String apellido,
                               @QueryParam("edad") int edad) {

        return "Hola desde el metodo POST, " + nombre + " " + apellido + ". Tienes " + edad + " años.";
    }

    @GET
    @Path("/codabar/generar")
    @Produces("image/jpg")
    public Response generarCodigoBarras(@QueryParam("texto") String texto,
                                        @QueryParam("formato_codigo_barras") String formatoCodigoBarras) throws WriterException {

        int width = 600;
        int height = 600;

        BarcodeFormat format;
        try {
            format = BarcodeFormat.valueOf(formatoCodigoBarras);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Formato no válido: " + formatoCodigoBarras + ". Usa QR_CODE, EAN_13, CODE_128, etc.");
        }

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix matrix = new MultiFormatWriter().encode(texto, format, width, height, hints);


        // Crear imagen a partir del BitMatrix
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                boolean bit = matrix.get(x, y);
                image.setRGB(x, y, bit ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }

        // Convertir la imagen a un array de bytes
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        try {
            javax.imageio.ImageIO.write(image, "jpg", baos);
            baos.flush();
        } catch (Exception e) {
            return Response.serverError().entity("Error al generar la imagen").build();
        }

        byte[] imageData = baos.toByteArray();
        Response.ResponseBuilder response = Response.ok(imageData).type("image/jpg");

        response.header("Content-Disposition", "attachment; filename=\"qr.jpg\"");

        return response.build();

    }

    @POST
    @Path("/codabar/generar")
    @Consumes({"image/png", "image/jpg"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response leerCodigoBarras(InputStream inputImage) throws IOException {


        try {
            BufferedImage bufferedImage = ImageIO.read(inputImage);
            if (bufferedImage == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"No se pudo leer la imagen\"}").build();
            }

            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = new MultiFormatReader().decode(bitmap);

            String textoLeido = result.getText();

            String json = "{\"texto\":\"" + textoLeido + "\"}";
            return Response.ok(json, MediaType.APPLICATION_JSON).build();


        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }

    }

}