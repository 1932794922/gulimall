package com.august.gulimall.member.service.impl;

import com.august.gulimall.common.exception.RenException;
import com.august.gulimall.member.dao.MemberLevelDao;
import com.august.gulimall.member.entity.MemberLevelEntity;
import com.august.gulimall.member.service.MemberLevelService;
import com.august.gulimall.member.vo.UserLoginVO;
import com.august.gulimall.member.vo.UserRegistVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.august.gulimall.common.service.impl.CrudServiceImpl;
import com.august.gulimall.member.dao.MemberDao;
import com.august.gulimall.member.dto.MemberDTO;
import com.august.gulimall.member.entity.MemberEntity;
import com.august.gulimall.member.service.MemberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 会员
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Service
public class MemberServiceImpl extends CrudServiceImpl<MemberDao, MemberEntity, MemberDTO> implements MemberService {

    @Autowired
    private MemberLevelService  memberLevelService;



    @Override
    public QueryWrapper<MemberEntity> getWrapper(Map<String, Object> params) {
        String id = (String) params.get("id");

        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

    @Override
    public void regist(UserRegistVO vo) {
        MemberEntity entity = new MemberEntity();
        // 检查用户名和手机号是否唯一，为了让controller感知到，抛出自定义异常好被感知到是哪个
        checkUserNameUnique(vo.getUserName());
        checkPhoneUnique(vo.getPhone());
        checkEmailUnique(vo.getEmail());
        entity.setEmail(vo.getEmail());
        entity.setUsername(vo.getUserName());
        entity.setMobile(vo.getPhone());
        entity.setNickname(vo.getUserName());
        // 设置默认等级
        MemberLevelEntity levelEntity = memberLevelService.getDefaultLevel();
        if(levelEntity == null){
            entity.setLevelId(1L);
        }else {
            entity.setLevelId(levelEntity.getId());
        }

        // 密码加密存储，spring家的BCryptPasswordEncoder加密器
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        entity.setPassword(encode);
        // 其他默认信息
        // 保存
        this.baseDao.insert(entity);
    }

    @Override
    public MemberEntity login(UserLoginVO vo) {
        // 因为用户登录的账号可以是手机号，或者是用户名
        List<MemberEntity> memberEntities = this.baseDao.selectList(
                new QueryWrapper<MemberEntity>().eq("username", vo.getLoginacct())
                        .or().eq("mobile", vo.getLoginacct())
                        .or().eq("email", vo.getLoginacct())
        );
        if(memberEntities == null || memberEntities.size() == 0){
            return null;
        }
        MemberEntity entity = memberEntities.get(0);
        if (entity == null) {
            throw new RenException(4000, "用户不存在");
        } else {
            String password = entity.getPassword();
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            // 页面传来的密码和数据库中的密码匹配
            boolean matches = bCryptPasswordEncoder.matches(vo.getPassword(), password);
            if (matches) {
                return entity;
            } else {
                throw new RenException(4001, "密码错误");
            }
        }
    }

    public void checkUserNameUnique(String userName) {
        Long username = this.baseDao.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if (username > 0) {
            throw new RenException(4001, "用户名已存在");
        }
    }

    public void checkPhoneUnique(String phone) {
        Long mobile = this.baseDao.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (mobile > 0) {
            throw new RenException(4002, "手机号已存在");
        }
    }

    public void checkEmailUnique(String email) {
        Long mobile = this.baseDao.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", email));
        if (mobile > 0) {
            throw new RenException(4003, "邮箱已存在");
        }
    }
}