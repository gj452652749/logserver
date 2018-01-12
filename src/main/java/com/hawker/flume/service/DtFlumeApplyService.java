package com.hawker.flume.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hawker.flume.dao.DtFlumeApplyDao;
import com.hawker.flume.pojo.DtFlumeApplyBean;
import com.hawker.flume.pojo.ResponseResult;
@Service
public class DtFlumeApplyService {
	protected final static Logger logger = LoggerFactory.getLogger(DtFlumeApplyService.class);
	@Autowired
	DtFlumeApplyDao dao;
	public boolean save(DtFlumeApplyBean bean) {
		try {
		int code=dao.save(bean);
		if(code!=1)
			return false;
		}catch(Exception e) {
			logger.warn("数据库插入出错!",e);
			return false;
		}
		return true;
	}
	public ResponseResult update(int id,String files,String topic) {
		ResponseResult result=new ResponseResult();
		int code=dao.update(id, files,topic);
		if(code!=1) {
			result.setCode(1);
			result.setErrmsg("更新失败！");
		}
		result.setRows(code);
		return result;
	}
	public ResponseResult delete(int id) {
		ResponseResult result=new ResponseResult();
		int code=dao.delete(id);
		if(code!=1) {
			result.setCode(1);
			result.setErrmsg("删除失败！");
		}
		result.setRows(code);
		return result;
	}
	public ResponseResult getPage(int pageno,int pagesize) {
		ResponseResult result=new ResponseResult();
		int offset=(pageno-1)*pagesize;
		//int max=pageno*pagesize;
		List<DtFlumeApplyBean> list=dao.getPage(offset,pagesize);
		int rows=dao.getRows();
//		JSONArray jsonArray=new JSONArray();
//		for(DtFlumeApplyBean bean:list)
//			jsonArray.add(bean);
		result.setResult(list);
		result.setRows(rows);
		return result;
	}
	/**
	 * 返回不重复的监听目录
	 * @param list
	 * @return
	 */
	public List<String> getDistinctFiles(List<String> list) {
		List<String> distinctFileList=new ArrayList<String>();
		for(String ele:list) {
			if(StringUtils.isEmpty(ele))
				continue;
			String [] files=ele.split(",");
			for(String file:files) {
			//加入非重复的file
			if(!distinctFileList.contains(file)) 
				distinctFileList.add(file);
			}
		}
		return distinctFileList;
	}
	public List<String> getFiles(String ip,String updatedFiles) {
		List<String> list=dao.getFiles(ip);
		list.add(updatedFiles);
		List<String> distinctFileList=getDistinctFiles(list);
		for(String ele:distinctFileList)
			System.out.println(ele);
		return distinctFileList;
	}
	public List<String> getFiles(String ip) {
		List<String> list=dao.getFiles(ip);
		List<String> distinctFileList=getDistinctFiles(list);
		for(String ele:distinctFileList)
			System.out.println(ele);
		return distinctFileList;
	}
	public DtFlumeApplyBean getById(int id) {
		try {
		DtFlumeApplyBean bean=dao.getById(id);
		return bean;
		}catch(Exception e) {
			logger.warn("no record found!"+id);
		}
		return null;
	}
}
