package com.pwc.spring.test.service;

import java.util.List;

import com.pwc.spring.test.dao.FirstDao;

public class FirstService {
	private FirstDao dao;
	private String name;
	private List<String> userList;
	public FirstDao getDao() {
		return dao;
	}
	public void setDao(FirstDao dao) {
		this.dao = dao;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getUserList() {
		return userList;
	}
	public void setUserList(List<String> userList) {
		this.userList = userList;
	}
	
}
