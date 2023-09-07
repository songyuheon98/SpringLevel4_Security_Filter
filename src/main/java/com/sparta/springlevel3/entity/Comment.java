package com.sparta.springlevel3.entity;

import com.sparta.springlevel3.dto.CommentRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 클라이언트가 작성한 댓글 정보를 담은 Entity
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "comment")
public class Comment extends Timestamped{
    /**
     * @GeneratedValue(strategy = GenerationType.IDENTITY) 를 사용하면 id가 자동으로 생성되는데,
     * 이때 id 생성 전략을 IDENTITY로 지정하면,
     * DB에 저장되는 시점에 id가 자동으로 생성되는 방식
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column(name = "postid", nullable = false) 를 사용하면,
     * DB의 postid 컬럼이라는 것을 명시해주고,
     * nullable = false 를 사용하면, postid가 null이 될 수 없다는 것을 명시해준다.
     */
    @Column(name = "postid", nullable = false)
    private Long postid;

    /**
     * @Column(name = "username", nullable = false) 를 사용하면,
     * DB의 username 컬럼이라는 것을 명시해주고,
     * nullable = false 를 사용하면, username이 null이 될 수 없다는 것을 명시해준다.
     */
    @Column(name = "username", nullable = false)
    private String username;

    /**
     * @Column(name = "contents", nullable = false, length = 500) 를 사용하면,
     * DB의 contents 컬럼이라는 것을 명시해주고,
     * nullable = false 를 사용하면, contents가 null이 될 수 없다는 것을 명시해준다.
     */
    @Column(name = "contents", nullable = false, length = 500)
    private String contents;

    /**
     * @ManyToOne(fetch = FetchType.LAZY) 를 사용하면,
     * Comment와 Memo의 관계가 N:1임을 명시해준다.
     * fetch = FetchType.LAZY 를 사용하면, Comment를 조회할 때 연관된 Memo를 가져오지 않는다.
     * 연관된 Memo가 필요한 경우에는 getMemo() 메서드를 사용해서 따로 조회해야 한다.
     * @JoinColumn(name = "postid", referencedColumnName = "id", insertable = false, updatable = false) 를 사용하면,
     * Comment의 postid 컬럼과 Memo의 id 컬럼을 조인 조건으로 사용한다는 것을 명시해준다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postid", referencedColumnName = "id", insertable = false, updatable = false)

    /**
     * 게시글과 댓글을 전부 한 번에 조회하기 위해 추가한 필드
     */
    private Memo memo;

//    public MemoResponseDto createComment(CommentRequestDto requestDto, String username) {
//        // RequestDto -> Entity
//        Comment comment = new Comment(requestDto, username);

    /**
     * 댓글 정보를 저장하기 위한 생성자
     * @param requestDto 클라이언트가 작성한 댓글 정보
     * @param tokenUsername 토큰에 담긴 username
     */
    public Comment(CommentRequestDto requestDto, String tokenUsername) {
        this.username = tokenUsername;
        this.postid = requestDto.getPostid();
        this.contents = requestDto.getContents();
    }


    /**
     * 댓글 정보를 수정하기 위한 생성자
     * @param contents
     */

    public void update(String contents) {
        this.contents = contents;

    }
}
