package com.august.gulimall.member.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 会员收藏的专题活动
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Data
@TableName("ums_member_collect_subject")
public class MemberCollectSubjectEntity {

    /**
     * id
     */
	private Long id;
    /**
     * subject_id
     */
	private Long subjectId;
    /**
     * subject_name
     */
	private String subjectName;
    /**
     * subject_img
     */
	private String subjectImg;
    /**
     * 活动url
     */
	private String subjectUrll;
}