package com.nimo.rediswebui.entity.redis;

import lombok.Data;

@Data
public class KeyMember {
	private String member;
	private String value;
	private long score;

	public KeyMember(String member, String value) {
		super();
		this.member = member;
		this.value = value;
	}
	public KeyMember(String member, long score) {
		super();
		this.member = member;
		this.score = score;
	}
	public KeyMember(String member, double score) {
		super();
		this.member = member;
		if(score%1 == 0) {
			this.value = String.valueOf((long)score);
		}else {
			this.value = String.valueOf(score);
		}

	}
	public KeyMember(String member) {
		super();
		this.member = member;
	}
	public KeyMember() {
		super();
	}

	
}
