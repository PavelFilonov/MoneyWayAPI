package com.edu.moneywayapi.dataAccess.dal;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "group1")
@Builder
public class GroupDAL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @ManyToMany(cascade = CascadeType.ALL)
    @Column(nullable = false)
    @JoinTable(
            name = "group_category",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<CategoryDAL> categories;
}
