package com.ayantsoft.scheduler.pojo;

public class RecordingFiles {
	
	private String id;
	private String meeting_id;
	private String recording_start;
	private String recording_end;
	private String file_type;
	private Long file_size;
	private String play_url;
	private String download_url;
	private String status;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMeeting_id() {
		return meeting_id;
	}
	public void setMeeting_id(String meeting_id) {
		this.meeting_id = meeting_id;
	}
	public String getRecording_start() {
		return recording_start;
	}
	public void setRecording_start(String recording_start) {
		this.recording_start = recording_start;
	}
	public String getRecording_end() {
		return recording_end;
	}
	public void setRecording_end(String recording_end) {
		this.recording_end = recording_end;
	}
	public String getFile_type() {
		return file_type;
	}
	public void setFile_type(String file_type) {
		this.file_type = file_type;
	}
	public Long getFile_size() {
		return file_size;
	}
	public void setFile_size(Long file_size) {
		this.file_size = file_size;
	}
	public String getPlay_url() {
		return play_url;
	}
	public void setPlay_url(String play_url) {
		this.play_url = play_url;
	}
	public String getDownload_url() {
		return download_url;
	}
	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
