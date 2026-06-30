package com.moov.niger.cciportal.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbl_tone_catalogue")
public class SongContent {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "tone_code")
    private String toneCode;

    @Column(name = "tone_url")
    private String toneUrl;

    @Column(name = "tone_name")
    private String toneName;

    @Column(name = "lang")
    private String lang;

    @Column(name = "category")
    private String category;

    @Column(name = "artist_name")
    private String artistName;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "status")
    private Byte status;

}
