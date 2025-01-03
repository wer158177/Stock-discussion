package com.hangha.postservice.controller.dto;



import com.hangha.postservice.application.command.PostUpdateCommand;
import com.hangha.postservice.application.command.PostWriteCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostRequestDto {

    private Long postId;

    @NotNull
    private String content;

    @NotNull
    private String title;
    private List<String> tags;


    public PostWriteCommand WriteCommand(Long userId) {
        return new PostWriteCommand(
                userId,
                this.title,   // 제목
                this.content,  // 내용
                this.tags
        );
    }

    public PostUpdateCommand updateCommand(Long userId) {
        return new PostUpdateCommand(
                userId,
                this.postId,
                this.title,   // 제목
                this.content, // 내용
                this.tags
        );
    }

}
