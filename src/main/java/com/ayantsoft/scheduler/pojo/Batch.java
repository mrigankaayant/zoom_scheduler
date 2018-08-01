package com.ayantsoft.scheduler.pojo;

import java.util.List;

public class Batch {
	
	private Integer page_count;
	private Integer page_number;
	private Integer page_size;
	private Integer total_records;
	private List<Meetings> meetings;
	
	public Integer getPage_count() {
		return page_count;
	}
	public void setPage_count(Integer page_count) {
		this.page_count = page_count;
	}
	public Integer getPage_number() {
		return page_number;
	}
	public void setPage_number(Integer page_number) {
		this.page_number = page_number;
	}
	public Integer getPage_size() {
		return page_size;
	}
	public void setPage_size(Integer page_size) {
		this.page_size = page_size;
	}
	public Integer getTotal_records() {
		return total_records;
	}
	public void setTotal_records(Integer total_records) {
		this.total_records = total_records;
	}
	public List<Meetings> getMeetings() {
		return meetings;
	}
	public void setMeetings(List<Meetings> meetings) {
		this.meetings = meetings;
	} 
}
