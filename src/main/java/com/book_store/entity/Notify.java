package com.book_store.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "notify")
public class Notify implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name="order_id", nullable=false)
    private Order order;
    @Column
    private Integer status;
    @Column(name = "create_at")
    @CreationTimestamp
    private Timestamp createdAt;
    @Column(name = "update_at")
    @UpdateTimestamp
    private Timestamp updatedAt;
}
