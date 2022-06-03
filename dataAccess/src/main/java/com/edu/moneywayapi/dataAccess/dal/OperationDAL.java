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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryDAL categoryDAL;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserDAL userDAL;

    @Column(nullable = false)
    private Double value;

    @Column(name = "date_operation", nullable = false)
    private LocalDateTime createdAt;
}