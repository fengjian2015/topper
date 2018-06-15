package com.bclould.tocotalk.model;

import java.io.Serializable;

public class UserCodeInfo implements Serializable {
    private String email;
    private String password;
    private int id;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
