package december.spring.studywithme.entity;

import lombok.Getter;

@Getter
public enum ContentsType {
    POST("post"),           // 게시글 좋아요
    COMMENT("comment");     // 댓글 좋아요

    private final String contents;

    ContentsType(String contents){
        this.contents = contents;
    }

}
