package december.spring.studywithme.dto;

import december.spring.studywithme.entity.Post;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PostPageResponseDTO {
    private Integer currentPage;
    private Long totalElements;
    private Integer totalPages;
    private Integer size;
    private String sortBy;
    private String from;
    private String to;
    private List<PostResponseDTO> postList;

    public PostPageResponseDTO(Integer currentPage, Page<Post> postPage, String from, String to) {
        this.currentPage = currentPage;
        this.totalElements = postPage.getTotalElements();
        this.totalPages = postPage.getTotalPages();
        this.size = postPage.getSize();
        this.sortBy = postPage.getSort().toString();
        this.from = from;
        this.to = to;
        this.postList = postPage.getContent().stream().map(PostResponseDTO::new).toList();
    }
}
