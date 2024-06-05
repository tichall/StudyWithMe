package december.spring.studywithme.service;

import december.spring.studywithme.dto.LikeRequestDto;
import december.spring.studywithme.entity.ContentsType;
import december.spring.studywithme.entity.Like;
import december.spring.studywithme.entity.Post;
import december.spring.studywithme.entity.User;
import december.spring.studywithme.exception.LikeException;
import december.spring.studywithme.exception.PostException;
import december.spring.studywithme.repository.LikeRepository;
import december.spring.studywithme.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    //게시글 좋아요 등록 / 취소
    @Transactional
    public boolean likePost(Long postId, User user, LikeRequestDto likeRequestDto) {

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostException("게시글이 존재하지 않습니다."));

        if (likeRequestDto.getContentsType().equals(ContentsType.COMMENT)) {
            throw new LikeException("콘텐츠 유형이 게시글이 아닙니다.!!");
        }

        if (post.getUser().getUserId().equals(user.getUserId())) {
            throw new LikeException("본인이 작성한 게시글에는 좋아요를 남길 수 없습니다.");
        }

        return toggleLike(user, likeRequestDto, post);

    }

    // 댓글 좋아요 등록 / 취소
    @Transactional
    public boolean likeComment(Long postId, User user, LikeRequestDto likeRequestDto) {

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostException("게시글이 존재하지 않습니다."));

        if (likeRequestDto.getContentsType().equals(ContentsType.POST)) {
            throw new LikeException("콘텐츠 유형이 댓글이 아닙니다.");
        }

        return toggleLike(user, likeRequestDto, post);
    }

    // 좋아요 DB 저장
    private boolean toggleLike(User user, LikeRequestDto likeRequestDto, Post post) {
        Like like = likeRepository.findByUserAndPostAndContentsType(user, post, likeRequestDto.getContentsType());

        //like 객체 업데이트
        if (like != null) {
            like.update(!like.isLike());
        } else {
            like = Like.builder()
                    .user(user)
                    .post(post)
                    .contentsType(likeRequestDto.getContentsType())
                    .isLike(true)
                    .build();
            likeRepository.save(like);
        }

        return like.isLike();
    }
}
