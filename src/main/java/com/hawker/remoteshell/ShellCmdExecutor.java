package com.hawker.remoteshell;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
public class ShellCmdExecutor {
	//全量队列
	private BlockingQueue<PrintWriter> printQueue = new LinkedBlockingQueue<>();
	private BlockingQueue<BufferedReader> readerQueue = new LinkedBlockingQueue<>();
	ExecutorService fullPullThreadpool = Executors.newFixedThreadPool(40);
	public void addFullInstance() {
		
	}
	public void executeFullDump() {
		
	}
}
