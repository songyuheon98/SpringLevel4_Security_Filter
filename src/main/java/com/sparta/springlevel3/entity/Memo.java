package com.sparta.springlevel3.entity;

import com.sparta.springlevel3.dto.MemoRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 클라이언트가 작성한 메모 정보를 담은 Entity
 */
@Entity // JPA가 관리할 수 있는 Entity 클래스 지정
@Getter
@Setter
@Table(name = "memo") // 매핑할 테이블의 이름을 지정
@NoArgsConstructor
public class Memo extends Timestamped{

    @Id
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(name = "username", nullable = false)
    private String username;
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(name = "title", nullable = false)
    private String title;
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(name = "contents", nullable = false, length = 500)
    private String contents;



    public Memo(MemoRequestDto requestDto, String tokenUsername) {
        this.username = tokenUsername;
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
    }


    public void update(MemoRequestDto requestDto, String username) {
        this.username = username;
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
    }

    /**
     * Memo와 Comment의 관계는 1:N 관계이다.
     * Memo 한 개에 Comment 여러 개가 달릴 수 있다.
     * 따라서 Memo 클래스에는 Comment 리스트가 필요하다.
     * @OneToMany(mappedBy = "memo", cascade = CascadeType.REMOVE) 를 사용하면,
     * Memo를 삭제할 때 연관된 Comment들도 모두 삭제된다.
     * mappedBy = "memo" 를 사용하면, Comment 클래스의 memo 필드가 연관관계의 주인임을 명시해준다.
     * cascade = CascadeType.REMOVE 를 사용하면, Memo를 삭제할 때 연관된 Comment들도 모두 삭제된다.
     * cascade = CascadeType.REMOVE 를 사용하지 않으면, Memo를 삭제할 때 연관된 Comment들은 삭제되지 않는다.
     */
    @OneToMany(mappedBy = "memo", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();
}