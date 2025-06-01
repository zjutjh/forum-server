package org.jh.forum.start.controller;

import org.jh.forum.api.service.AnnouncementService;
import org.jh.forum.common.dto.request.CreateAnnouncementRequest;
import org.jh.forum.common.dto.request.EditAnnouncementRequest;
import org.jh.forum.common.dto.request.ListAnnouncementRequest;
import org.jh.forum.common.dto.request.StickyAnnouncementRequest;
import org.jh.forum.common.dto.response.AnnouncementDetailsResponse;
import org.jh.forum.common.dto.response.AnnouncementOperationResponse;
import org.jh.forum.common.dto.response.ListAnnouncementResponse;
import org.jh.forum.start.models.AjaxResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * 开发链路说明：
 * 完整链路说明：
 * 1. Controller 接收HTTP请求
 * 2. 调用 Service(RPC) 处理请求
 * 3. Service 调用 Manager 处理业务逻辑
 * 4. Manager 调用 Mapper 操作数据库
 * 5. 返回结果给前端
 *
 * @author SituChengxiang（SK）
 */
@Slf4j
@RestController
@RequestMapping("/announcement")
@Tag(name = "公告管理接口", description = "公告的创建、发布、查询等功能")
public class AnnouncementController {

    @Resource
    private AnnouncementService announcementService;    /**
     * 创建公告草稿接口
     *
     * HTTP方法：POST
     * 请求路径：/announcement
     * 请求体：JSON格式的CreateAnnouncementRequest
     *
     * @param request 创建公告的请求参数
     * @return 创建结果，包含公告基本信息
     */
    @Operation(summary = "创建公告", description = "创建一个公告，支持多种类型")
    @PostMapping
    public AjaxResult<AnnouncementOperationResponse> createAnnouncement(
            @Valid @RequestBody CreateAnnouncementRequest request) {
        try {
            // 第1步：记录请求日志
            log.info(
                "收到创建草稿公告请求，标题: {}, 类型: {}, 创建用户: {}, 预计时间：{}",
                request.getTitle(),
                request.getType(),
                request.getCreatorId(), // 修复：使用驼峰命名的getter方法
                request.getScheduledAt()
            );

            AnnouncementOperationResponse response = announcementService.createAnnouncement(request);

            // 第3步：记录成功日志
            log.info(
                "公告创建成功，ID: {}",
                response.getAnnounceId()
            );

            // 第4步：返回成功结果
            return AjaxResult.success("created", response);
        } catch (Exception e) {
            // 异常处理：记录错误日志并返回失败结果
            log.error(
                "创建草稿公告失败，标题: {}, 错误信息: {}",
                request.getTitle(),
                e.getMessage(),
                e
            );
            return AjaxResult.fail(500, "创建公告失败：" + e.getMessage());
        }
    }    /**
     * 查询公告接口 - 支持列表查询和详情查询
     *
     * HTTP方法：GET
     * 请求路径：/announcement?page=xxx&size=xxx (列表查询)
     * 请求路径：/announcement?id=xxx (详情查询)
     *
     * @param id 公告ID（可选，用于详情查询）
     * @param request 列表查询参数（可选，用于列表查询）
     * @return 公告详情或列表
     */
    @Operation(
        summary = "查询公告",
        description = "根据参数查询公告详情或列表"
    )
    @GetMapping
    public AjaxResult<?> getAnnouncement(
        @RequestParam(required = false) Integer id,
        @Valid ListAnnouncementRequest request
    ) {
        // 如果提供了id参数，则查询详情
        if (id != null) {
            return getAnnouncementDetail(id);
        }
        // 否则查询列表
        return listAnnouncements(request);
    }

    /**
     * 查询公告详情的内部方法
     */
    private AjaxResult<AnnouncementDetailsResponse> getAnnouncementDetail(Integer id) {
        try {
            // 第1步：记录请求日志
            log.info("收到查询公告详情请求，ID: {}", id);

            // 第2步：参数校验
            if (id == null || id <= 0) {
                log.warn("公告ID无效: {}", id);
                return AjaxResult.fail(400, "公告ID无效");
            }

            // 第3步：调用RPC接口查询公告详情
            AnnouncementDetailsResponse response =
                announcementService.getAnnouncementById(id);

            if (response == null) {
                log.warn("公告不存在，ID: {}", id);
                return AjaxResult.fail(404, "公告不存在");
            }

            log.info(
                "查询公告详情成功，ID: {}, 标题: {}",
                response.getId(),
                response.getTitle()
            );
            return AjaxResult.success(response);
        } catch (Exception e) {
            log.error(
                "查询公告详情失败，ID: {}, 错误信息: {}",
                id,
                e.getMessage(),
                e
            );
            return AjaxResult.fail(500, "查询公告失败：" + e.getMessage());
        }
    }    /**
     * 查询公告列表的内部方法
     */
    private AjaxResult<ListAnnouncementResponse> listAnnouncements(
        ListAnnouncementRequest request
    ) {
        try {
            log.info(
                "收到查询公告列表请求，页码: {}, 状态: {}, 类型: {}",
                request.getPage(),
                request.getStatus(),
                request.getType()
            );

            // 强制设置每页大小为8条
            request.setSize(8);

            ListAnnouncementResponse response =
                announcementService.listAnnouncements(request);

            log.info(
                "查询公告列表成功，总数: {}, 当前页: {}",
                response.getTotal(),
                response.getPage()
            );
            return AjaxResult.success(response);
        } catch (Exception e) {
            log.error("查询公告列表失败，错误信息: {}", e.getMessage(), e);
            return AjaxResult.fail(500, "查询公告列表失败：" + e.getMessage());
        }    }

    /**
     * 编辑公告接口
     *
     * HTTP方法：PUT
     * 请求路径：/announcement
     * 请求体：JSON格式的EditAnnouncementRequest（包含id字段）
     *
     * @param request 编辑公告的请求参数
     * @return 编辑结果
     */
    @Operation(summary = "编辑公告", description = "编辑指定ID的公告信息")
    @PutMapping
    public AjaxResult<AnnouncementOperationResponse> editAnnouncement(
            @Valid @RequestBody EditAnnouncementRequest request) {
        try {
            // 第1步：记录请求日志
            log.info(
                "收到编辑公告请求，ID: {}, 标题: {}, 类型: {}, 修改用户: {}",
                request.getId(),
                request.getTitle(),
                request.getType(),
                request.getUpdatorId()
            );

            // 第2步：参数校验
            if (request.getId() == null || request.getId() <= 0) {
                log.warn("公告ID无效: {}", request.getId());
                return AjaxResult.fail(400, "公告ID无效");
            }

            AnnouncementOperationResponse response = announcementService.editAnnouncement(request.getId(), request);

            if (response == null) {
                log.warn("编辑公告失败，公告可能不存在，ID: {}", request.getId());
                return AjaxResult.fail(404, "公告不存在或编辑失败");
            }

            // 第3步：记录成功日志
            log.info(
                "公告编辑成功，ID: {}",
                response.getAnnounceId()
            );

            // 第4步：返回成功结果
            return AjaxResult.success("edited", response);
        } catch (Exception e) {
            // 异常处理：记录错误日志并返回失败结果
            log.error(
                "编辑公告失败，ID: {}, 标题: {}, 错误信息: {}",
                request.getId(),
                request.getTitle(),
                e.getMessage(),
                e
            );
            return AjaxResult.fail(500, "编辑公告失败：" + e.getMessage());
        }
    }    /**
     * 删除公告接口
     *
     * HTTP方法：DELETE
     * 请求路径：/announcement?id=xxx
     * 查询参数：id（公告ID）
     *
     * @param id 公告ID
     * @return 删除结果
     */
    @Operation(summary = "删除公告", description = "软删除指定公告")
    @DeleteMapping
    public AjaxResult<AnnouncementOperationResponse> deleteAnnouncement(@RequestParam Integer id) {
        try {
            log.info("收到删除公告请求，ID: {}", id);

            AnnouncementOperationResponse result = announcementService.deleteAnnouncement(id);

            if (result == null) {
                log.warn("删除公告失败，公告可能不存在，ID: {}", id);
                return AjaxResult.fail(404, "公告不存在或删除失败");
            }

            log.info("删除公告成功，ID: {}", id);
            return AjaxResult.success("deleted", result);
        } catch (Exception e) {
            log.error(
                "删除公告失败，ID: {}, 错误信息: {}",
                id,
                e.getMessage(),
                e
            );
            return AjaxResult.fail(500, "删除公告失败：" + e.getMessage());
        }
    }    /**
     * 置顶/取消置顶公告接口
     *
     * HTTP方法：PUT
     * 请求路径：/announcement/sticky?id=xxx
     * 查询参数：id（公告ID）
     * 请求体：JSON格式的StickyAnnouncementRequest
     *
     * @param id 公告ID
     * @param request 置顶/取消置顶请求参数
     * @return 操作结果
     */
    @Operation(summary = "置顶/取消置顶公告", description = "设置或取消公告的置顶状态")
    @PutMapping("/sticky")
    public AjaxResult<AnnouncementOperationResponse> stickyAnnouncement(
            @RequestParam Integer id,
            @Valid @RequestBody StickyAnnouncementRequest request) {        try {
            log.info("收到置顶/取消置顶公告请求，ID: {}, 置顶状态: {}", id, request.getIsSticky());

            if (id == null || id <= 0) {
                log.warn("公告ID无效: {}", id);
                return AjaxResult.fail(400, "公告ID无效");
            }

            // 将URL参数中的id设置到request对象中，保持与现有service接口的兼容性
            request.setId(id);

            AnnouncementOperationResponse response = announcementService.stickyAnnouncement(
                request.getId(), 
                request.getIsSticky()
            );

            if (response == null) {
                log.warn("置顶/取消置顶公告失败，公告可能不存在，ID: {}", id);
                return AjaxResult.fail(404, "公告不存在或操作失败");
            }

            log.info(
                "公告{}成功，ID: {}",
                request.getIsSticky() ? "置顶" : "取消置顶",
                response.getAnnounceId()
            );

            return AjaxResult.success(request.getIsSticky() ? "stickied" : "unstickied", response);
        } catch (Exception e) {
            log.error(
                "置顶/取消置顶公告失败，ID: {}, 错误信息: {}",
                id,
                e.getMessage(),
                e
            );
            return AjaxResult.fail(500, "置顶/取消置顶公告失败：" + e.getMessage());
        }
    }
}