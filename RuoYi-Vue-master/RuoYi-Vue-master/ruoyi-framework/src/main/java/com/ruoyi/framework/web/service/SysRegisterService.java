package com.ruoyi.framework.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.user.CaptchaException;
import com.ruoyi.common.exception.user.CaptchaExpireException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysUserService;

/**
 * 注册校验方法
 * 
 * @author ruoyi
 */
@Component
public class SysRegisterService
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 注册
     */
    public String register(RegisterBody registerBody)
    {
        String msg = "", username = registerBody.getUsername(), password = registerBody.getPassword();
        String roleType = registerBody.getRoleType();
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);

        // 验证码开关
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled)
        {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        if (StringUtils.isEmpty(username))
        {
            msg = "用户名不能为空";
        }
        else if (StringUtils.isEmpty(password))
        {
            msg = "用户密码不能为空";
        }
        else if (StringUtils.isEmpty(roleType))
        {
            msg = "请选择注册角色（买家或卖家）";
        }
        else if (!"buyer".equals(roleType) && !"seller".equals(roleType))
        {
            msg = "角色类型不正确，只能选择买家或卖家";
        }
        else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            msg = "账户长度必须在2到20个字符之间";
        }
        else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            msg = "密码长度必须在5到20个字符之间";
        }
        else if (!userService.checkUserNameUnique(sysUser))
        {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        }
        else
        {
            // 设置用户基本信息（用于验证）
            sysUser.setNickName(StringUtils.isNotEmpty(registerBody.getNickName()) ? registerBody.getNickName() : username);
            sysUser.setPhonenumber(StringUtils.isNotEmpty(registerBody.getPhonenumber()) ? registerBody.getPhonenumber() : "");
            sysUser.setEmail(StringUtils.isNotEmpty(registerBody.getEmail()) ? registerBody.getEmail() : "");
            
            // 验证手机号唯一性
            if (StringUtils.isNotEmpty(sysUser.getPhonenumber()) && !userService.checkPhoneUnique(sysUser))
            {
                msg = "注册失败，手机号码已存在";
            }
            // 验证邮箱唯一性
            else if (StringUtils.isNotEmpty(sysUser.getEmail()) && !userService.checkEmailUnique(sysUser))
            {
                msg = "注册失败，邮箱账号已存在";
            }
            else
            {
                // 验证通过，设置用户信息并注册
                sysUser.setPwdUpdateDate(DateUtils.getNowDate());
                sysUser.setPassword(SecurityUtils.encryptPassword(password));
                
                // 根据角色类型分配角色ID
                // 买家角色ID=100, 卖家角色ID=101
                Long[] roleIds;
                if ("buyer".equals(roleType))
                {
                    roleIds = new Long[]{100L};  // 买家角色
                }
                else
                {
                    roleIds = new Long[]{101L};  // 卖家角色
                }
                sysUser.setRoleIds(roleIds);
                
                boolean regFlag = userService.registerUser(sysUser);
                if (!regFlag)
                {
                    msg = "注册失败,请联系系统管理人员";
                }
                else
                {
                    AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success")));
                }
            }
        }
        return msg;
    }

    /**
     * 校验验证码
     * 
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null)
        {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException();
        }
    }
}
