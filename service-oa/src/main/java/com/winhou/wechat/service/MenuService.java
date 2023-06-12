package com.winhou.wechat.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.winhou.model.wechat.Menu;
import com.winhou.vo.wechat.MenuVo;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author winhou
 * @since 2023-06-12
 */
public interface MenuService extends IService<Menu> {

    // 获取全部菜单
    List<MenuVo> findMenuInfo();

    // 同步菜单
    void syncMenu();

    // 删除菜单
    void removeMenu();
}
