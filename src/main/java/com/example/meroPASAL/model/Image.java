package com.example.meroPASAL.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Blob;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String fileType;
    @Lob
    private Blob image;
    private String downloadUrl;

    @ManyToOne
    @JoinColumn(
            name = "product_id"
    )
    @JsonIgnore
    private Product product;


}
