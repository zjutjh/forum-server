package org.jh.forum.server.dubbo;

import com.google.protobuf.Any;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.*;
import org.jh.forum.server.manger.CommentManager;

import jakarta.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * @author qianqianzyk
 * @version 1.0
 */
@DubboService(version = "1.0.0")
@Slf4j
public class CommentServerImpl implements CommentService {
    @Resource
    private CommentManager commentManager;

    @Override
    public ServiceResult publishComment(PublishCommentReq request) {
        // TODO 评论内容审查

        // TODO 评论附件审查

        Long commentId = commentManager.publishComment(request);

        // TODO 发送评论消息

        PublishCommentResp resp = PublishCommentResp.newBuilder()
                .setCommentId(commentId)
                .build();

        return ServiceResult.newBuilder()
                .setIsSuccess(true)
                .setData(Any.pack(resp))
                .build();
    }

    @Override
    public ServiceResult upvoteComment(UpvoteCommentReq request) {
        Boolean status = commentManager.upvoteComment(request.getCommentId());

        // TODO 发送点赞消息

        UpvoteCommentResp resp = UpvoteCommentResp.newBuilder()
                .setStatus(status)
                .build();

        return ServiceResult.newBuilder()
                .setIsSuccess(true)
                .setData(Any.pack(resp))
                .build();
    }

    @Override
    public ServiceResult pinComment(PinCommentReq request) {
        Boolean status = commentManager.pinComment(request.getCommentId());

        PinCommentResp resp = PinCommentResp.newBuilder()
                .setStatus(status)
                .build();

        return ServiceResult.newBuilder()
                .setIsSuccess(true)
                .setData(Any.pack(resp))
                .build();
    }

    @Override
    public CompletableFuture<ServiceResult> publishCommentAsync(PublishCommentReq request) {
        return CompletableFuture.supplyAsync(() -> this.publishComment(request));
    }

    @Override
    public CompletableFuture<ServiceResult> upvoteCommentAsync(UpvoteCommentReq request) {
        return CompletableFuture.supplyAsync(() -> this.upvoteComment(request));
    }

    @Override
    public CompletableFuture<ServiceResult> pinCommentAsync(PinCommentReq request) {
        return CompletableFuture.supplyAsync(() -> this.pinComment(request));
    }
}
