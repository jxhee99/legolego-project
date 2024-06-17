package com.kosta.legolego.review.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "review")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Review {
    @Id
    @Column(name = "review_num")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewNum;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "create_date", nullable = false)
    private Date createdData;

    @Column(name = "rating", nullable = false)
    private Integer rating;

//    @ManyToOne
//    @JoinColumn(name = "board_num", nullable = false)
//    private PreTripBoard preTripBoard;
}