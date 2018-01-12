package com.hawker.flume.dao;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hawker.flume.vo.DtFlumeApplyBean;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DtFlumeApplyDaoTest {
	@Autowired
	DtFlumeApplyDao dao;
	@Test
	public void save() {
		dao.save(new DtFlumeApplyBean("127.0.0.1",22,"root","34","ddd"));
	}
	@Test
	public void update() {
		int id=1;
		String files="tail -F /usr/local/log/flume.log";
		dao.update(id, files,null);
	}
	@Test
	public void delete() {
		dao.delete(1);;
	}
	@Test
	public void getPage() {
		List<DtFlumeApplyBean> list=dao.getPage(0,9);
		for(DtFlumeApplyBean bean:list)
			System.out.println(bean.getFiles());
	}
	@Test
	public void getFiles() {
		List<String> list=dao.getFiles("2");
		for(String ele:list)
			System.out.println(ele);
	}
	@Test
	public void getById() {
		DtFlumeApplyBean bean=dao.getById(3);
		System.out.println(bean.getFiles());
	}
}
