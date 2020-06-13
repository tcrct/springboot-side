package com.springbootside.duang.common.config;

import com.springbootside.duang.common.dto.HeadDto;
import com.springbootside.duang.common.dto.R;
import com.springbootside.duang.common.dto.ReturnDto;
import com.springbootside.duang.common.utils.ToolsKit;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 在原有业务R的基础上，增加返回内容
 *
 * @author Laotang
 * @since 1.0
 */
@ControllerAdvice
public class DuangResponseBodyAdvice implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof R) {
            R r = (R) body;
            HeadDto headDto = ToolsKit.getThreadLocalDto();
            if (headDto.getUri().equalsIgnoreCase(request.getURI().getPath())) {
                int code = r.getCode();
                Object obj = r.getMsg();
                headDto.setCode(code);
                headDto.setMsg(obj);
                // 服务器业务处理执行时间(毫秒)
                headDto.setProcessTime(System.currentTimeMillis() - headDto.getStartTime());
                ReturnDto returnDto = new ReturnDto(headDto);
                if (code == 0) {
                    headDto.setMsg("success");
                    returnDto.setData(obj);
                }
                // 移除
                ToolsKit.removeThreadLocalDto();
                return returnDto;
            }
        }
        return body;
    }
}
