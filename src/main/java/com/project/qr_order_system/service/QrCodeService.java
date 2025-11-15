package com.project.qr_order_system.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class QrCodeService {

    @Value("${app.frontend-url}")
    private String frontUrl;

    /**
     * QR코드 생성
     */
    public byte[] createQrCodeImage(Long storeId, Integer tableNumber) throws WriterException, IOException {

        String url;

        // tableNumber가 있는지 확인
        if(tableNumber != null && tableNumber > 0){
            // tableNumber가 있으면 DINE_IN
            url = frontUrl + "/qrorder/storeId=" + storeId + "&tableNumber=" + tableNumber;
        } else {
            // 없으면 TAKEOUT
            url = frontUrl + "/qrorder/storeId=" + storeId;
        }

        log.info("QR 코드 생성 {}", url);

        // QR 코드 생성
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 250, 250);

        // QR 코드 생성한 것을 이미지로 변환
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // 이미지를 byte로 변환
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", byteStream);

        return byteStream.toByteArray();
    }


}
