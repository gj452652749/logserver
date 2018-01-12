package com.hawker.flume.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.hawker.flume.pojo.ResponseResult;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DtFlumeApplyServiceTest {
	@Autowired
	DtFlumeApplyService service;
	@Test
	public void getFiles() {
		service.getFiles("3","/usr/local/spark");
	}
	@Test
	public void getPage() {
		ResponseResult result=service.getPage(1, 10);
		System.out.println(JSON.toJSONString(result));
	}
}
