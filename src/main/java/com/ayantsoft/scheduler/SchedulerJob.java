package com.ayantsoft.scheduler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Properties;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.ayantsoft.scheduler.pojo.Batch;
import com.ayantsoft.scheduler.pojo.MeetingDto;
import com.ayantsoft.scheduler.pojo.Meetings;
import com.ayantsoft.scheduler.pojo.RecordingFiles;
import com.ayantsoft.scheduler.pojo.Sequence;
import com.ayantsoft.scheduler.pojo.UserList;
import com.ayantsoft.scheduler.pojo.Users;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class SchedulerJob implements Job {
	
	//video_dir_path = /ext001/tomcat/apache-tomcat-8.0.46/webapps/zoom/assets/
	
	//<cron-expression>0 0 0 1/1 * ? *</cron-expression>

	ClassLoader objClassLoader = null;
	Properties commonProperties = new Properties();
	FileInputStream objFileInputStream = null;

	@Override
	public void execute(JobExecutionContext context)      
			throws JobExecutionException {

		try{

			String root_path = readKey("quartz.properties","video_dir_path");

			MongoClientURI uri = new MongoClientURI("mongodb://ayantdev:AyantAstra422@ayant-shard-00-00-mn0tf.mongodb.net:27017,"
					+ "ayant-shard-00-01-mn0tf.mongodb.net:27017,"
					+ "ayant-shard-00-02-mn0tf.mongodb.net:27017/admin?replicaSet=ayant-shard-0&ssl=true");

			MongoClient mongoClient = new MongoClient(uri);
			SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(mongoClient, "zoom_details");
			MongoTemplate mongoTemplate = new MongoTemplate(simpleMongoDbFactory);
			
			List<Users> hostList = null;
			URL url = new URL("https://api.zoom.us/v1/user/list?api_key=TiwlS58sRDmLJBGVVDInjA&api_secret=bsK1vW5fjMBRmykjtnkMkyVXchqKIh0CdIRX");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output = null;

			while((output = br.readLine()) != null){
				Gson g = new Gson();
				UserList userList = g.fromJson(output, UserList.class);
				hostList = userList.getUsers();
			}

			for(Users u:hostList){

				url = new URL("https://api.zoom.us/v1/recording/list?api_key=TiwlS58sRDmLJBGVVDInjA&api_secret=bsK1vW5fjMBRmykjtnkMkyVXchqKIh0CdIRX&host_id="+URLEncoder.encode(u.getId(), "UTF-8" ));
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Accept", "application/json");

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}

				br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				while ((output = br.readLine()) != null) {

					Gson g = new Gson();
					Batch b = g.fromJson(output, Batch.class); 

					List<Meetings> mets = b.getMeetings();

					for(Meetings m:mets){

						if(m.getRecording_count() >0){

							MeetingDto meetingDto = new MeetingDto();

							meetingDto.setUuid(m.getUuid());
							meetingDto.setMeetingNumber(m.getMeeting_number());
							meetingDto.setAccountId(m.getAccount_id());
							meetingDto.setHostId(m.getHost_id());
							meetingDto.setDuration(m.getDuration());

							List<RecordingFiles> rs = m.getRecording_files();

							for(RecordingFiles r:rs){

								if(r.getStatus().equals("completed")){
									meetingDto.setRecordingId(r.getId());
									if(r.getRecording_start() != null){

										int endIndex = r.getRecording_start().indexOf("T");
										String startDate = r.getRecording_start().substring(0,endIndex);
										meetingDto.setRecordingStart(startDate);
									}
									meetingDto.setRecordingEnd(r.getRecording_end());
									meetingDto.setFileType(r.getFile_type());
									
									if(meetingDto.getFileType().equals("MP4")){
										meetingDto.setTopic(m.getTopic()+" [VIDEO]");
									}else if(meetingDto.getFileType().equals("M4A")){
										meetingDto.setTopic(m.getTopic()+" [AUDIO]");
									}
									
									meetingDto.setFileSize(r.getFile_size());
									meetingDto.setDownloadUrl(r.getDownload_url());
									meetingDto.setStatus(r.getStatus());

									File file = new File(root_path+m.getTopic().replaceAll(" ", "_").replaceAll("/","_"));
									if(file.exists()){
										if(meetingDto.getFileType().equals("MP4")){
											file = new File(root_path+m.getTopic().replaceAll(" ", "_").replaceAll("/","_")+"/"+r.getId()+".mp4");
										}else if(meetingDto.getFileType().equals("M4A")){
											file = new File(root_path+m.getTopic().replaceAll(" ", "_").replaceAll("/","_")+"/"+r.getId()+".mp4");
										}

										if(!file.exists()){
											file.createNewFile();	
										}
									}else{
										file.mkdir();
										if(meetingDto.getFileType().equals("MP4")){
											file = new File(root_path+m.getTopic().replaceAll(" ", "_").replaceAll("/","_")+"/"+r.getId()+".mp4");
										}else if(meetingDto.getFileType().equals("M4A")){
											file = new File(root_path+m.getTopic().replaceAll(" ", "_").replaceAll("/","_")+"/"+r.getId()+".mp4");
										}
										file.createNewFile();
									}


									if(meetingDto.getFileType().equals("MP4") || meetingDto.getFileType().equals("M4A")){ // start download and delete
										long fileSize = file.length();
										if(!(meetingDto.getFileSize() == fileSize)){
											saveFileFromUrlWithJavaIO(root_path+m.getTopic().replaceAll(" ", "_").replaceAll("/","_")+"/"+r.getId()+".mp4",r.getDownload_url());
											fileSize = file.length();
											if(meetingDto.getFileSize() == fileSize){
												int startIndex = meetingDto.getTopic().indexOf("(");
												int endIndex = meetingDto.getTopic().indexOf(")");
												if(startIndex >0 && endIndex >0){
													meetingDto.setBatchId(meetingDto.getTopic().substring(startIndex+1, endIndex).toUpperCase());
												}else{
													meetingDto.setBatchId("Un-defined");
												}
												meetingDto.setServerUrl("assets/"+m.getTopic().replaceAll(" ", "_").replaceAll("/","_")+"/"+r.getId()+".mp4");

												meetingDto.setMeetingId(getNextSequenceId("meeting",mongoTemplate));
												mongoTemplate.save(meetingDto,"meeting"); 
											}
											
											if(meetingDto.getFileSize() == fileSize){
												url = new URL("https://api.zoom.us/v1/recording/delete?api_key=TiwlS58sRDmLJBGVVDInjA&api_secret=bsK1vW5fjMBRmykjtnkMkyVXchqKIh0CdIRX&meeting_id="+URLEncoder.encode(meetingDto.getUuid(), "UTF-8" )+"&file_id="+URLEncoder.encode(meetingDto.getRecordingId(), "UTF-8" ));
												conn = (HttpURLConnection) url.openConnection();
												conn.setRequestMethod("POST");
												conn.setRequestProperty("Accept", "application/json");

												if (conn.getResponseCode() != 200) {
													throw new RuntimeException("Failed : HTTP error code : "
															+ conn.getResponseCode());
												}

												br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
												while ((output = br.readLine()) != null) {

												}
											}
										}
									} 
								}
							}
						}  
					}	
				}
				conn.disconnect();
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}



	public static void saveFileFromUrlWithJavaIO(String fileName, String fileUrl)
			throws MalformedURLException, IOException {
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			in = new BufferedInputStream(new URL(fileUrl).openStream());
			fout = new FileOutputStream(fileName);

			byte data[] = new byte[1024];
			int count;
			while ((count = in.read(data, 0, 1024)) != -1) {
				fout.write(data, 0, count);
			}
		} finally {
			if (in != null)
				in.close();
			if (fout != null)
				fout.close();
		}
	}


	public static Long getNextSequenceId(String key,MongoTemplate mongoTemplate) throws Exception {

		Query query = new Query(Criteria.where("id").is(key));
		Update update = new Update();
		update.inc("seq", 1);

		FindAndModifyOptions options = new FindAndModifyOptions();
		options.returnNew(true);

		Sequence seqId = (Sequence) mongoTemplate.findAndModify(query, update, options, Sequence.class);

		if (seqId == null) {
			throw new Exception("Unable to get sequence id for key : " + key);
		}

		return seqId.getSeq();
	}




	public String readKey(String propertiesFilename, String key) throws IOException{
		if (propertiesFilename != null && !propertiesFilename.trim().isEmpty() && key != null && !key.trim().isEmpty()) {
			try{
				objClassLoader = getClass().getClassLoader();
				objFileInputStream = new FileInputStream(objClassLoader.getResource(propertiesFilename).getFile());
				commonProperties.load(objFileInputStream);
				return String.valueOf(commonProperties.get(key)).trim();
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			finally{
				objFileInputStream.close();
			}
		}
		return null;
	}

}




