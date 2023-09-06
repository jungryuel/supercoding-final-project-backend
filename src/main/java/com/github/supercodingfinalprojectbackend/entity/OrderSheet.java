package com.github.supercodingfinalprojectbackend.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "orderSheetId", callSuper = false)
@Table(name = "order_sheets")
public class OrderSheet extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_sheet_id")
    private Long orderSheetId;
    @ManyToOne
    @JoinColumn(name = "mentee_id")
    private Mentee mentee;
    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Posts post;
    @Column(name = "total_price")
    private Integer totlaPrice;
    @Column(name = "is_completed")
    private Boolean isCompleted;
    @Column(name = "transaction_type")
    private String transactionType;
}
