package com.hawker.flume.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.hawker.flume.pojo.DtFlumeApplyBean;
@Mapper
public interface DtFlumeApplyDao {

	int save(DtFlumeApplyBean bean);

	/**
	 * 根据id更新files
	 * 
	 * @param id
	 * @param files
	 */
	public int update(int id, String files,String topics);

	public int delete(int id);
	public List<DtFlumeApplyBean> getPage(int offset,int max);
	public int getRows();
	public DtFlumeApplyBean getById(int id);
	public List<String> getFiles(String ip);
}
