# 2025年6月1日 会议纪要

## 鉴权方式
jwt

## 分页方式
基于游标和基于页号的
走base list

## token预置
还没留呢

# 通知统一用rpc

## 筛选项

## 置顶
sticky字段单开还是塞attribute里头？
如果塞attributes里头我要去研究

好了直接新开一个字段吧

- [ ] 公告类型（改一下integrate）
- [ ] creator_id和updator_id记得改jwt（AntoFillHandler）
- [ ] 调用BaseEntity
- [x] 调用BaseList
- [x] 分接口管理（管理员和用户端）
- [ ] Respond的枚举管理（status和type）
- [x] 接口命名
- [ ] 返回相应里面显示creator_name和updator_name（目前先拿字符填上）