package december.spring.studywithme.service;

import december.spring.studywithme.dto.CommentRequestDTO;
import december.spring.studywithme.dto.CommentResponseDTO;
import december.spring.studywithme.entity.*;
import december.spring.studywithme.exception.CommentException;
import december.spring.studywithme.exception.NoContentException;
import december.spring.studywithme.repository.CommentRepository;
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

    /**
     * 1. 댓글 등록
     * @param userDetails 로그인한 사용자의 세부 정보
     * @param postId 게시물의 ID
     * @param requestDto 댓글 생성 요청 데이터
     * @return CommentResponseDTO 형태의 댓글 정보
     */
    @Transactional
    public CommentResponseDTO createComment(UserDetailsImpl userDetails, Long postId, CommentRequestDTO requestDto) {
        Post post = postService.getValidatePost(postId);
        Comment comment = Comment.builder()
                .post(post)
                .user(userDetails.getUser())
                .contents(requestDto.getContents())
                .build();

        commentRepository.save(comment);
        return new CommentResponseDTO(comment);
    }

    /**
     * 2. 전체 댓글 조회
     * @param postId 게시물의 ID
     * @return List<CommentResponseDTO> 형태의 댓글 목록
     */
    public List<CommentResponseDTO> getAllComments(Long postId) {
        Post post = postService.getValidatePost(postId);
        List<Comment> commentList = post.getCommentList();

        if (commentList.isEmpty()) {
            throw new NoContentException("가장 먼저 댓글을 작성해보세요!");
        }

        return commentList.stream().map(CommentResponseDTO::new).toList();
    }

    /**
     * 3. 단일 댓글 조회
     * @param postId 게시물의 ID
     * @param commentId 댓글의 ID
     * @return CommentResponseDTO 형태의 댓글 정보
     */
    public CommentResponseDTO getComment(Long postId, Long commentId) {
        Post post = postService.getValidatePost(postId);

        Comment comment = getValidateComment(post.getId(), commentId);

        return new CommentResponseDTO(comment);
    }

    /**
     * 4. 댓글 수정
     * @param userDetails 로그인한 사용자의 세부 정보
     * @param postId 게시물의 ID
     * @param commentId 댓글의 ID
     * @param requestDto 댓글 수정 요청 데이터
     * @return CommentResponseDTO 형태의 댓글 정보
     */
    @Transactional
    public CommentResponseDTO updateComment(UserDetailsImpl userDetails, Long postId, Long commentId, CommentRequestDTO requestDto) {
        Post post = postService.getValidatePost(postId);
        Comment comment = getValidateComment(post.getId(), commentId);
        checkCommentWriter(comment, userDetails);

        comment.update(requestDto);
        commentRepository.save(comment);

        return new CommentResponseDTO(comment);
    }

    /**
     * 5. 댓글 삭제
     * @param userDetails 로그인한 사용자의 세부 정보
     * @param postId 게시물의 ID
     * @param commentId 댓글의 ID
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
     * @param postId 게시물의 ID
     * @param commentId 댓글의 ID
     * @return Comment 형태의 댓글 정보
     */
    public Comment getValidateComment(Long postId, Long commentId) {
        return commentRepository.findByPostIdAndId(postId, commentId).orElseThrow(() ->
                new CommentException("게시글에 해당 댓글이 존재하지 않습니다."));
    }

    /**
     * 댓글 작성자 확인
     * @param comment 댓글 정보
     * @param userDetails 로그인한 사용자의 세부 정보
     */
    private void checkCommentWriter(Comment comment, UserDetailsImpl userDetails) {
        if (!comment.getUser().getUserId().equals(userDetails.getUsername())) {
            throw new CommentException("작성자가 아니므로, 접근이 제한됩니다.");
        }
    }
}
