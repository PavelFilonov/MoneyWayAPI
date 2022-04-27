package com.edu.moneywayapi.dataAccess.dal;

import com.edu.moneywayapi.domain.entity.TypeOperation;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "operation")
@Builder
public class OperationDAL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TypeOperation type;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryDAL categoryDAL;

    @Column(nullable = false)
    private String value;

    @Column(name = "date_operation", nullable = false)
    private LocalDateTime dateOperation;
}