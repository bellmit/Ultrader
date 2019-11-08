package com.ultrader.bot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@EntityScan
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @Column(name="ID", unique=true, updatable=false, nullable=false)
    private String id;
    @Column(name="type", updatable=false, nullable=false)
    private String type;
    @Column(name="content", updatable=false, nullable=false, length = 3000)
    private String content;
    @Column(name="title", updatable=false, nullable=false, length = 100)
    private String title;
    @Column(name="date")
    private Date date;
}
