package com.hangha.stockdiscussion.post.controller.dto;



import com.hangha.stockdiscussion.post.application.command.PostUpdateCommand;
import com.hangha.stockdiscussion.post.application.command.PostWriteCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRequestDto {

    private Long postId;

    @NotNull
    private String content;

    @NotNull
    private String title;


    public PostWriteCommand WriteCommand(Long userId) {
        return new PostWriteCommand(
                userId,
                this.title,   // 제목
                this.content  // 내용
        );
    }

    public PostUpdateCommand updateCommand(Long userId) {
        return new PostUpdateCommand(
                userId,
                this.postId,
                this.title,   // 제목
                this.content  // 내용
        );
    }

}
