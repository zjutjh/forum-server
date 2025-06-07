package org.jh.forum.server.dubbo;

import com.google.protobuf.Any;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jh.forum.api.dubbo.CommentService;
import org.jh.forum.api.dubbo.PublishCommentReq;
import org.jh.forum.api.dubbo.PublishCommentResp;
import org.jh.forum.api.dubbo.ServiceResult;
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
        // TODO user_id获取
        Long userId = 1L;

        try {
            // TODO 评论内容审查

            // TODO 评论附件审查

            // 创建评论记录
            Long commentId = commentManager.publishComment(request, userId);
            if (commentId == null || commentId == 0L) {
                return ServiceResult.newBuilder()
                        .setIsSuccess(false)
                        .setErrorCode("COMMENT_FAILED")
                        .setErrorMsg("评论发布失败")
                        .build();
            }

            // 创建返回结果
            PublishCommentResp resp = PublishCommentResp.newBuilder()
                    .setCommentId(commentId)
                    .build();

            return ServiceResult.newBuilder()
                    .setIsSuccess(true)
                    .setData(Any.pack(resp))
                    .build();
        } catch (Exception e) {
            return ServiceResult.newBuilder()
                    .setIsSuccess(false)
                    .setErrorCode("EXCEPTION")
                    .setErrorMsg("系统异常: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public CompletableFuture<ServiceResult> publishCommentAsync(PublishCommentReq request) {
        return CompletableFuture.supplyAsync(() -> this.publishComment(request));
    }
}
