package com.kosta.legolego.review.entity;

import com.kosta.legolego.orders.entity.Order;
import com.kosta.legolego.review.dto.ReviewDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
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
    private Timestamp createdDate;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @ManyToOne
    @JoinColumn(name = "board_num", nullable = true)
    private PreTripBoard preTripBoard;

    @OneToOne(mappedBy = "review", fetch = FetchType.EAGER)
    private Order order;

    // 후기 수정 시 변경 할 수 있는 필드
    public void patch(ReviewDto reviewDto) {
        if(reviewDto.getRating() != null)
            this.rating = reviewDto.getRating();

        if(reviewDto.getContent() != null)
            this.content = reviewDto.getContent();
    }
}
