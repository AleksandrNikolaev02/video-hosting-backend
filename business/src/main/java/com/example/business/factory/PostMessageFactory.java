package com.example.business.factory;

import com.example.dto.PostMessageDTO;
import com.example.dto.StatusProcessChannel;

public class PostMessageFactory {
    public static PostMessageDTO createSuccessDto(String pipelineKey) {
        return new PostMessageDTO(StatusProcessChannel.DATA_SUCCESS, pipelineKey);
    }

    public static PostMessageDTO createFailureDto(String pipeline) {
        return new PostMessageDTO(StatusProcessChannel.DATA_FAILURE, pipeline);
    }
}
