package com.example.meroPASAL.security.controller;

import com.example.meroPASAL.security.service.ShopkeeperServiceQr;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

@RequestMapping("${api.prefix}/shopQR")
@RequiredArgsConstructor
public class ShopkeeperControllerQr {

    private final ShopkeeperServiceQr shopkeeperService;


    @GetMapping("/{id}/qr")
    public ResponseEntity<byte[]> getBusinessQR(@PathVariable Long id) {
        byte[] imageData = shopkeeperService.getBusinessQR(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=qr_" + id + ".png")
                .contentType(MediaType.IMAGE_PNG)
                .body(imageData);
    }

}
