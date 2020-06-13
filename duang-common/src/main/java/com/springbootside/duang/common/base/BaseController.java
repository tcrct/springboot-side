package com.springbootside.duang.common.base;

import cn.hutool.core.io.IoUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import com.springbootside.duang.common.dto.R;
import com.springbootside.duang.common.dto.ReturnDto;
import com.springbootside.duang.common.dto.ValidatorErrorDto;
import com.springbootside.duang.common.utils.SpringKit;
import com.springbootside.duang.common.utils.ToolsKit;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Controller基类
 *
 * @author Laotang
 * @since 1.0
 */
public abstract class BaseController<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private Validator validator;

    protected HttpServletRequest getRequest() {
        return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
    }
    protected HttpServletResponse getResponse() {
        return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
    }

    protected String getValue(String name) {
        String param = getRequest().getParameter(name);
        if (ToolsKit.isEmpty(param)) {
            param = (String)getRequest().getAttribute(name);
        }
        return param;
    }

    protected Integer getIntValue(String name) {
        String tempValue = getValue(name);
        try {
            return optional(tempValue).isPresent() ? Integer.valueOf(tempValue) : null;
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 取泛型对象类型
     * @return
     */
    protected Class<?> getGenericTypeClass() {
        return ToolsKit.getSuperClassGenericType(getClass(), 0);
    }

    /**
     * 取泛型Bean对象
     *  必须要在客户端的请求头里添加Content_type=application/json
     *
     * @param <T> 泛型
     * @return 对应的Bean
     */
    protected <T> T getBean() {
        BufferedReader reader = null;
        try {
            reader = getRequest().getReader();
            if(!optional(reader).isPresent()) {
                LOGGER.info("取请求主体内容时，内容值为空！");
                return null;
            }
            String body = IoUtil.read(reader);
            // 必须要在客户端的请求头里添加Content_type=application/json
            if (!ContentType.JSON.getValue().startsWith(getRequest().getHeader(Header.CONTENT_TYPE.getValue()))) {
                LOGGER.info("请确保请求头里的[{}]字段设置为[{}]！",Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
                return null;
            }
            return (T)ToolsKit.jsonParseObject(body, getGenericTypeClass());
        } catch (Exception e) {
            LOGGER.warn("BaseController getBean时出错: " +e.getMessage(), e);
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    private <T> Optional<T> optional(T obj) {
        return Optional.ofNullable(obj);
    }

    /**
     * 根据ID查找记录
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findById", method= RequestMethod.GET)
    public R findById() {
        Class<?> clazz = getGenericTypeClass();
        System.out.println("##########:" + clazz.getName());
        return R.success();
    }

    /**
     * 保存操作
     *
     * value:  指定请求的实际地址， 比如 /action/info之类。
     * method：  指定请求的method类型， GET、POST、PUT、DELETE等
     * consumes： 指定处理请求的提交内容类型（Content-Type），例如application/json, text/html;
     * produces:    指定返回的内容类型，仅当request请求头中的(Accept)类型中包含该指定类型才返回
     * params： 指定request中必须包含某些参数值是，才让该方法处理
     * headers： 指定request中必须包含某些指定的header值，才能让该方法处理请求
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/save",
            method= RequestMethod.POST,
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R save() {
        T vo = getBean();
        Set<ConstraintViolation<T>> violationSet = validator.validate(vo);
        if (ToolsKit.isNotEmpty(violationSet)) {
            List<ValidatorErrorDto> validatorErrorDtoList = new ArrayList<>();
            for (ConstraintViolation<T> model : violationSet) {
                validatorErrorDtoList.add(new ValidatorErrorDto(model.getPropertyPath().toString(), model.getMessage()));
            }
            return R.error(1001, validatorErrorDtoList);
        }
        // 调用对应的Service层里的save方法

        Object serviceImpl = SpringKit.getBeanByGenericType(vo.getClass());
        if (ToolsKit.isEmpty(serviceImpl)) {
            LOGGER.warn("根据泛型[{}]没有找到对应ServiceImpl类，请检查！", vo.getClass());
            return R.error(1, "error");
        }
        try {
            ICurdService<T> curdService = (ICurdService<T>) serviceImpl;
            curdService.save(vo);
            return R.success();
        } catch (Exception e) {
            return R.error(1, e.getMessage());
        }
    }

    /**
     * 根据条件搜索记录
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/search",
            method= RequestMethod.POST,
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R search() {
        Class<?> clazz = getGenericTypeClass();
        return R.success();
    }


}
