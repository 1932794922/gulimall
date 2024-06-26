package com.august.gulimall.member.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 会员收藏的专题活动
 *
 * @author august xiao1932794922@gmail.com
 * @since 1.0.0 2022-10-16
 */
@Data
@ApiModel(value = "会员收藏的专题活动")
public class MemberCollectSubjectDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "subject_id")
	private Long subjectId;

	@ApiModelProperty(value = "subject_name")
	private String subjectName;

	@ApiModelProperty(value = "subject_img")
	private String subjectImg;

	@ApiModelProperty(value = "活动url")
	private String subjectUrll;


}