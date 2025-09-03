package edu.pe.vallegrande.AIOME.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("aiome")
public class Aiome {
    @Id
    private Integer id;
    private String question;
    private String response;
    private String status;
    private LocalDateTime date;
    private String aitype;
}