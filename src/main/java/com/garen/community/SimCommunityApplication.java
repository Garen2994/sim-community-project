package com.garen.community;

import org.elasticsearch.transport.netty4.Netty4Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/**
 * @Title : 启动类:SpringBoot项目的入口
 * @Author : garen_hou
 * @Email : garen2994@hotmail.com
 * @Date :  2020/7/24 10:38 下午
 */
@SpringBootApplication
public class SimCommunityApplication {
	//管理Bean的生命周期
	@PostConstruct
	public void init(){
		//解决Netty启动冲突的问题
		System.setProperty("es.set.netty.runtime.available.processors", "false");
	}
	public static void main(String[] args) {
		SpringApplication.run(SimCommunityApplication.class, args);
	}

}
