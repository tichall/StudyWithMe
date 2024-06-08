package december.spring.studywithme.service;

import december.spring.studywithme.dto.CommentRequestDto;
import december.spring.studywithme.dto.CommentResponseDto;
import december.spring.studywithme.entity.*;
import december.spring.studywithme.exception.CommentException;
import december.spring.studywithme.exception.LikeException;
import december.spring.studywithme.exception.NoContentException;
import december.spring.studywithme.exception.PostException;
import december.spring.studywithme.repository.CommentRepository;
import december.spring.studywithme.repository.LikeRepository;
import december.spring.studywithme.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final LikeRepository likeRepository;

    /**
     * 댓글 등록
     */
    @Transactional
    public CommentResponseDto createComment(UserDetailsImpl userDetails, Long postId, CommentRequestDto requestDto) {
        Post post = postService.getValidatePost(postId);
        Comment comment = Comment.builder()
                .post(post)
                .user(userDetails.getUser())
                .contents(requestDto.getContents())
                .build();

        commentRepository.save(comment);
        return new CommentResponseDto(comment);
    }

    /**
     * 전체 댓글 조회
     */
    public List<CommentResponseDto> getAllComments(Long postId) {
        Post post = postService.getValidatePost(postId);
        List<Comment> commentList = post.getCommentList();

        if (commentList.isEmpty()) {
            throw new NoContentException("가장 먼저 댓글을 작성해보세요!");
        }

        return commentList.stream().map(CommentResponseDto::new).toList();
    }

    /**
     * 댓글 단일 조회
     */
    public CommentResponseDto getComment(Long postId, Long commentId) {
        Post post = postService.getValidatePost(postId);

        Comment comment = getValidateComment(post.getId(), commentId);

        return new CommentResponseDto(comment);
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public CommentResponseDto updateComment(UserDetailsImpl userDetails, Long postId, Long commentId, CommentRequestDto requestDto) {
        Post post = postService.getValidatePost(postId);
        Comment comment = getValidateComment(post.getId(), commentId);
        checkCommentWriter(comment, userDetails);

        comment.update(requestDto);
        commentRepository.save(comment);
        commentRepository.flush();

        return new CommentResponseDto(comment);
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(UserDetailsImpl userDetails, Long postId, Long commentId) {
        Post post = postService.getValidatePost(postId);
        Comment comment = getValidateComment(post.getId(), commentId);
        checkCommentWriter(comment, userDetails);

        commentRepository.delete(comment);
    }


    /**
     * 댓글 존재 여부 확인
     */
    public Comment getValidateComment(Long postId, Long commentId) {
        return commentRepository.findByPostIdAndId(postId, commentId).orElseThrow(() ->
                new CommentException("게시글에 해당 댓글이 존재하지 않습니다."));
    }

    /**
     * 댓글 작성자 확인
     */
    private void checkCommentWriter(Comment comment, UserDetailsImpl userDetails) {
        if (!comment.getUser().getUserId().equals(userDetails.getUsername())) {
            throw new CommentException("작성자가 아니므로, 접근이 제한됩니다.");
        }
    }
}
