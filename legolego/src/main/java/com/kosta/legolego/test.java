// JPA, mariadb 연동 확인 Test 코드 - 확인 후 삭제 해주세요

package com.kosta.legolego;

import jakarta.persistence.*;
import lombok.*;
//import javax.persistence.*;

@Entity
// 해당 클래스가 엔티티를 위한 클래스이며,
// 해당 클래스의 인스턴스들이 JPA로 관리되는 entity 객체라는 것을 의미
// 옵션에 따라서 자동으로 table 생성가능
// 클래스의 멤버 변수에 따라서 자동으로 칼럼들도 생성된다.

@Table(name = "tbl_test")
// @Entity 어노테이션과 같이 사용할 수 있는 어노테이션
// DB상에서 엔티티 클래스를 어떤 테이블로 생성할 것인지에 대한 정보를 담기위한 어노테이션


@Data
// @Getter  @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
// 이 모든걸 합친 어노테이션

@Builder // builder 패턴 사용하기 위한 어노테이션
@AllArgsConstructor // @Builder 를 이용하기 위해서 항상 같이 처리해야 컴파일 에러가 발생하지 않는다
@NoArgsConstructor // @Builder 를 이용하기 위해서 항상 같이 처리해야 컴파일 에러가 발생하지 않는다

public class test {

    @Id // @Entity 가 붙은 클래스는 PK에 해당하는 특정필드를 @Id로 지정해야 한다
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 해당 어노테이션은 사용자가 입력하는 값을 사용하는 경우가 아니면 자동으로 생성되는 번호를 사용하기 위해 사용한다
    private Long id;

    @Column
    // 추가적인 필드(컬럼)이 팔요한 경우 사용 * DB 테이블에는 컬럼으로 생성되지 않는 필드의 경우 @Transient를 사용한다
    private String text;
}
