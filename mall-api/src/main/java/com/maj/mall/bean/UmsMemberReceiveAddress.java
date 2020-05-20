package com.maj.mall.bean;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 用户收货地址表
 */
public class UmsMemberReceiveAddress implements Serializable {
	private static final long serialVersionUID = 7691465960364307788L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long memberId;
	/**
	 * 收货人名称
	 */
	private String name;
	private String phoneNumber;
	/**
	 * 是否为默认
	 */
	private Integer defaultStatus;
	/**
	 * 邮政编码
	 */
	private String postCode;
	/**
	 * 省份，城市，区，详细街道（地址）
	 */
	private String province;
	private String city;
	private String region;
	private String detailAddress;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public Integer getDefaultStatus() {
		return defaultStatus;
	}
	public void setDefaultStatus(Integer defaultStatus) {
		this.defaultStatus = defaultStatus;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getDetailAddress() {
		return detailAddress;
	}
	public void setDetailAddress(String detailAddress) {
		this.detailAddress = detailAddress;
	}
	
	

}
