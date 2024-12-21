package com.hangha.stockdiscussion.post.application;

import com.hangha.stockdiscussion.post.application.command.PostUpdateCommand;
import com.hangha.stockdiscussion.post.application.command.PostWriteCommand;
import com.hangha.stockdiscussion.post.controller.dto.PostRequestDto;
import com.hangha.stockdiscussion.post.domain.service.PostInterface;
import org.springframework.stereotype.Service;

@Service
public class PostApplicationService {
    private final PostInterface postinterface;

    public PostApplicationService(PostInterface postinterface) {
        this.postinterface = postinterface;
    }


    public void postWrite(Long userId, PostRequestDto postRequestDto){
        PostWriteCommand command = postRequestDto.WriteCommand(userId);
        postinterface.writePost(command);
    }

    public  void postUpdate(Long userId, PostRequestDto postRequestDto){
        PostUpdateCommand command = postRequestDto.updateCommand(userId);
        postinterface.updatePost(command);
    }

    public  void postDelete(Long userId,Long postId){
        postinterface.deletePost(postId,userId);
    }
}
