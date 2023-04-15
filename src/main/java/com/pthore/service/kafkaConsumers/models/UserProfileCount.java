package com.pthore.service.kafkaConsumers.models;

public class UserProfileCount extends AConsumerObject<UserProfileCount>{
	private String userEmail;
	private Long viewCount;
	
	@Override
	public String toString() {
		return "UserProfileCount [userEmail=" + userEmail + ", viewCount=" + viewCount + "]";
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public Long getViewCount() {
		return viewCount;
	}

	public void setViewCount(Long viewCount) {
		this.viewCount = viewCount;
	}

	@Override
	public int compareTo(UserProfileCount o) {
		
		return Long.compare(this.viewCount, o.viewCount);
	}

}
