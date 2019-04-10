/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xyz.browser.app.modular.api;

import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.mutidatasource.annotion.DataSource;
import com.google.common.collect.Lists;
import com.xyz.browser.app.config.properties.GunsProperties;
import com.xyz.browser.app.core.common.constant.DatasourceEnum;
import com.xyz.browser.app.core.common.constant.state.ManagerStatus;
import com.xyz.browser.app.core.common.exception.BizExceptionEnum;
import com.xyz.browser.app.core.oss.OssOpt;
import com.xyz.browser.app.core.redission.DistributedRedisLock;
import com.xyz.browser.app.core.shiro.ShiroKit;
import com.xyz.browser.app.core.util.JsonResult;
import com.xyz.browser.app.core.util.JwtTokenUtil;
import com.xyz.browser.app.modular.api.dto.PasswordDto;
import com.xyz.browser.app.modular.hbase.dao.BlockMapper;
import com.xyz.browser.app.modular.hbase.model.Block;
import com.xyz.browser.app.modular.hbase.service.BlockService;
import com.xyz.browser.app.modular.system.model.User;
import com.xyz.browser.app.modular.system.service.IDeptService;
import com.xyz.browser.app.modular.system.service.IRoleService;
import com.xyz.browser.app.modular.system.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 系统管理员控制器
 *
 * @author fengshuonan
 * @Date 2017年1月11日 下午1:08:17
 */
@RestController
@RequestMapping("/interface/user")
@Slf4j
public class InterfaceUserController extends BaseController {

    @Autowired
    private GunsProperties gunsProperties;

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DistributedRedisLock distributedRedisLock;

    @Autowired
    private IRoleService roleService;


    @Autowired
    private OssOpt ossOpt;

    private static FastDateFormat sdf = FastDateFormat.getInstance("yyyy-MM-dd");
    @Autowired
    private IDeptService deptService;
    @Autowired
    private BlockService blockService;
//    private JdbcTemplate phoenixJdbcTemplate;
    @RequestMapping(value = "/test", method = RequestMethod.POST)

    public JsonResult test(HttpServletRequest request) {
//        List<Block> blockList = blockService.list();
        return new JsonResult().addData("blocks",null);
    }


    /**
     * 修改密码
     * @param request
     * @param passwordDto
     * @return
     */
    @RequestMapping(value = "/modifyPwd", method = RequestMethod.POST)
    public JsonResult modifyPwd(HttpServletRequest request,@RequestBody PasswordDto passwordDto) {
        if(StringUtils.isBlank(passwordDto.getOldPassword())
                || StringUtils.isBlank(passwordDto.getNewPassword())){
            return new JsonResult(BizExceptionEnum.PARAMETER_CANT_BE_EMPTY);
        }

        Integer userid = Integer.valueOf(JwtTokenUtil.getSubjectFromRequest(request));
        User user = userService.selectById(userid);
        if(user == null || user.getStatus() != ManagerStatus.OK.getCode()){
            return new JsonResult(BizExceptionEnum.USER_DISABLE);
        }
        if(!ShiroKit.md5(passwordDto.getOldPassword(), user.getSalt()).equals(user.getPassword())){
            return new JsonResult(BizExceptionEnum.OLD_PWD_NOT_RIGHT);
        }
        user.setPassword(ShiroKit.md5(passwordDto.getNewPassword(), user.getSalt()));
        userService.updateById(user);

        return new JsonResult();
    }

}
