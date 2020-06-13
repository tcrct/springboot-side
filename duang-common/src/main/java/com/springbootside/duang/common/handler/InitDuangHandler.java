package com.springbootside.duang.common.handler;

import com.springbootside.duang.common.dto.HeadDto;
import com.springbootside.duang.common.utils.DuangId;
import com.springbootside.duang.common.utils.ToolsKit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求到达业务层，进行一系列初始化处理
 *
 * @author Laotang
 * @since 1.0
 */
public class InitDuangHandler implements IHandler {

    @Override
    public boolean handler(HttpServletRequest request, HttpServletResponse response) {
        // 初始化返回对象头部信息
        HeadDto headDto = new HeadDto();
        headDto.setRequestId(DuangId.get().toString());
        headDto.setRequestTime(ToolsKit.getCurrentDateTime());
        headDto.setUri(request.getRequestURI());
        ToolsKit.setThreadLocalDto(headDto);
        return true;
    }

}
