package com.hangha.stockdiscussion.post.domain.service;


import com.hangha.stockdiscussion.post.application.command.PostUpdateCommand;
import com.hangha.stockdiscussion.post.application.command.PostWriteCommand;

public interface PostInterface {
    //게시글작성
    void writePost(PostWriteCommand command);
    //게시글 수정
    void updatePost(PostUpdateCommand command);
}
