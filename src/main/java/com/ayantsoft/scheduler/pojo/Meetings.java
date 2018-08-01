package com.ayantsoft.scheduler.pojo;

import java.util.List;

public class Meetings {
	
	private String uuid;
	private Long meeting_number;
	private String account_id;
	private String host_id;
	private String topic;
	private String start_time;
	private String timezone;
	private Integer duration;
	private Long total_size;
	private Integer recording_count;
	private List<RecordingFiles> recording_files;
	
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Long getMeeting_number() {
		return meeting_number;
	}
	public void setMeeting_number(Long meeting_number) {
		this.meeting_number = meeting_number;
	}
	public String getAccount_id() {
		return account_id;
	}
	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}
	public String getHost_id() {
		return host_id;
	}
	public void setHost_id(String host_id) {
		this.host_id = host_id;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public Long getTotal_size() {
		return total_size;
	}
	public void setTotal_size(Long total_size) {
		this.total_size = total_size;
	}
	public Integer getRecording_count() {
		return recording_count;
	}
	public void setRecording_count(Integer recording_count) {
		this.recording_count = recording_count;
	}
	public List<RecordingFiles> getRecording_files() {
		return recording_files;
	}
	public void setRecording_files(List<RecordingFiles> recording_files) {
		this.recording_files = recording_files;
	}

}
