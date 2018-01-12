package com.hawker.flume.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hawker.flume.consts.ConfigConsts;
import com.hawker.flume.service.DtFlumeApplyService;
import com.hawker.flume.service.LogService;
import com.hawker.flume.vo.DtFlumeApplyBean;
import com.hawker.flume.vo.RemoteHost;
import com.hawker.flume.vo.ResponseResult;

@RestController
@RequestMapping("/flumeapply")
@CrossOrigin
public class DtFlumeApplyController {
	@Value("${flume.local.default}")
	String localDir;
	@Value("${flume.remote.default}")
	String remoteDir;
	@Autowired
	LogService logService;
	@Autowired
	DtFlumeApplyService service;
	@Autowired
	ConfigConsts appConfig;
	/**
	 * 鉴权远程主机(每一个agent只能调用一次)
	 * 验证用户名，把agent复制到远程主机，并远程启动
	 * @param host
	 */
	@RequestMapping(value="/apply", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult apply(@RequestBody RemoteHost host) {
		ResponseResult result=new ResponseResult();
		Boolean isSucess=logService.volidate(host);
		if(!isSucess) {
			result.setCode(1);
			result.setErrmsg("用户名或密码错误！");
			return result;
		}
		//第一次鉴权，无监听文件
		DtFlumeApplyBean applyBean=new DtFlumeApplyBean(host.getIp(),host.getPort(),
				host.getUsrname(),host.getPsword(),null);
		applyBean.setKafkaHosts(appConfig.getBrokerList());
		isSucess= service.save(applyBean);
		if(!isSucess) {
			result.setCode(1);
			result.setErrmsg("数据库插入出错，请检查唯一键！");
			return result;
		}
//		String localDir="";
//		String remoteDir="";
		isSucess=logService.remoteDeploy(localDir, host, remoteDir);
		if(!isSucess) {
			result.setCode(1);
			result.setErrmsg("flume服务远程部署失败！");
			return result;
		}
		result.setCode(0);
		result.setResult(JSON.toJSONString(applyBean));
		return result;
	}
	@RequestMapping(value="/save", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult save(DtFlumeApplyBean bean) {
		ResponseResult result = new ResponseResult();
		service.save(bean);
		result.setResult(JSON.toJSON(bean));
		return result;
	}

	/**
	 * 更新flume配置文件(配置文件存储到zk上)
	 */
	public ResponseResult updateConf(DtFlumeApplyBean bean) {
		// 查询获取host对应的所有监听目录
		List<String> distinctFileList = service.getFiles(bean.getIp(), bean.getFiles());
		//service.update(bean.getId(), bean.getFiles());
		ResponseResult result = new ResponseResult();
		Boolean isSucess = logService.updateConf(bean, remoteDir, distinctFileList);
		if (!isSucess) {
			result.setCode(1);
			result.setErrmsg("配置更新失败！");
			return result;
		}
		result.setCode(0);
		result.setResult("配置更新成功！");
		return result;
	}

	/**
	 * 更新flume配置文件(配置文件存储到zk上)
	 */
	@RequestMapping("/update")
	@ResponseBody
	public ResponseResult update(int id, String files) {
		DtFlumeApplyBean bean = service.getById(id);
		if(null==bean) {
			ResponseResult result=new ResponseResult();
			result.setCode(1);
			result.setErrmsg("数据库查询失败！");
			return result;
		}
		StringBuilder topics=new StringBuilder();
		String[] filesArray=files.split(",");
		for(int i=0;i<=(filesArray.length-1);i++) {
			String file=filesArray[i];
			if(file.contains("/")){
				file=file.replaceAll("/","_");
			}
			topics.append(bean.getIp()+file);
			if(i!=(filesArray.length-1))
				topics.append(",");
		}
		bean.setFiles(files);
		bean.setTopics(topics.toString());
		service.update(bean.getId(), bean.getFiles(),topics.toString());
		return updateConf(bean);
	}

	@RequestMapping("/delete")
	@ResponseBody
	public ResponseResult delete(int id) {
		DtFlumeApplyBean bean = service.getById(id);
		ResponseResult result = service.delete(id);
		if (result.getCode() != 0)
			return result;
		else
			return updateConf(bean);
	}

	@RequestMapping("/getPage")
	@ResponseBody
	public ResponseResult getPage(int pageno,int pagesize) {
		ResponseResult result = service.getPage(pageno,pagesize);
		return result;
	}
	/**
	 * 查看目录下的文件
	 */
	@RequestMapping(value="/listFiles", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult listFiles(@RequestBody RemoteHost host, String remoteDir) {
		ResponseResult result=new ResponseResult();
		String files=logService.listFiles(host, remoteDir);
		if(StringUtils.isEmpty(files)) {
			result.setCode(1);
			result.setErrmsg("列出子目录失败");
			return result;
		}
		JSONArray jsonArray=new JSONArray();
		String[] eles=files.split("\n");
		for(String ele:eles) {
			jsonArray.add(ele);
		}
		result.setCode(0);
		result.setResult(jsonArray.toString());
		return result;
	}

}
