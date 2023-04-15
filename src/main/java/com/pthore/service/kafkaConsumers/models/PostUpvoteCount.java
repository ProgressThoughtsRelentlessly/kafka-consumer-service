package com.pthore.service.kafkaConsumers.models;

public class PostUpvoteCount extends AConsumerObject<PostUpvoteCount> {
	
	private String postId;
	private Long upvoteCount;
	
	
	
	@Override
	public String toString() {
		return "PostUpvoteCount [postId=" + postId + ", upvoteCount=" + upvoteCount + "]";
	}
	public String getPostId() {
		return postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public Long getUpvoteCount() {
		return upvoteCount;
	}
	public void setUpvoteCount(Long upvoteCount) {
		this.upvoteCount = upvoteCount;
	}
	@Override
	public int compareTo(PostUpvoteCount o) {
		
		return Long.compare(this.upvoteCount, o.upvoteCount);
	}
	
	
	
}
