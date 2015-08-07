package lds.gw.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

public class Model implements Serializable {

	private static final long serialVersionUID = 7861695824476278477L;

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
