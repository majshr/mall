package com.maj.mall.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 用户信息
 * @author maj
 */
public class UmsMember implements Serializable {
	private static final long serialVersionUID = 7628692561036598194L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long memberLevelId;
	private String username;
	private String password;
	/**
	 * 昵称
	 */
	private String nickname;
	private String phone;
	/**
	 * 帐号启用状态：0->禁用；1->启用
	 */
	private Integer status;
	private Date createTime;
	/**
	 * 头像
	 */
	private String icon;
	/**
	 * 性别:0->未知；1->男；2->女
	 */
	private Integer gender;
	private Date birthday;
	private String city;
	private String job;
	/**
	 * 个性签名
	 */
	private String personalizedSignature;
	/**
     * 用户来源：1本网站；2微博
     */
	private Integer sourceType;
	/**
	 * 积分
	 */
	private Integer integration;
	/**
	 * 成长值
	 */
	private Integer growth;
	/**
	 * 剩余抽奖次数
	 */
	private Integer luckeyCount;
	
    /**
     * 历史积分数量
     */
    private Integer historyIntegration;

    private String accessCode;
    private String accessToken;

    private String sourceUid;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMemberLevelId() {
		return memberLevelId;
	}
	public void setMemberLevelId(Long memberLevelId) {
		this.memberLevelId = memberLevelId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getSourceUid() {
        return sourceUid;
    }

    public void setSourceUid(String sourceUid) {
        this.sourceUid = sourceUid;
    }

    public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getPersonalizedSignature() {
		return personalizedSignature;
	}
	public void setPersonalizedSignature(String personalizedSignature) {
		this.personalizedSignature = personalizedSignature;
	}
	public Integer getSourceType() {
		return sourceType;
	}
	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}
	public Integer getIntegration() {
		return integration;
	}
	public void setIntegration(Integer integration) {
		this.integration = integration;
	}
	public Integer getGrowth() {
		return growth;
	}
	public void setGrowth(Integer growth) {
		this.growth = growth;
	}
	public Integer getLuckeyCount() {
		return luckeyCount;
	}
	public void setLuckeyCount(Integer luckeyCount) {
		this.luckeyCount = luckeyCount;
	}
	public Integer getHistoryIntegration() {
		return historyIntegration;
	}
	public void setHistoryIntegration(Integer historyIntegration) {
		this.historyIntegration = historyIntegration;
	}

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }
	
	

}
