# OA办公系统-Web管理端

这是基于SpringBoot的智能OA办公系统，通过微信公众号实现OA管理流程

项目包括管理端和员工端

管理端：角色管理、用户管理、菜单管理、权限管理、审批管理、公众号菜单管理

员工端：员工在微信公众号操作，主要功能：办公审批、微信授权登录、消息推送等

## 项目技术

| 基础框架   | Spring Boot                                  |
| ---------- | -------------------------------------------- |
| 数据缓存   | Redis                                        |
| 数据库     | MyBatis-Plus + MySQL                         |
| 权限控制   | Spring Security                              |
| 工作流引擎 | Activiti7                                    |
| 管理端前端 | Vue + ElementUI + Axios + Node.js + Npm      |
| 前端框架   | vue-admin-template                           |
| 微信公众号 | 公众号菜单管理 + 微信授权登录 + 模板消息推送 |
| 接口文档   | Swagger                                      |

