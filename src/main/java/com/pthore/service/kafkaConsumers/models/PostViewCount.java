package com.pthore.service.kafkaConsumers.models;

public class PostViewCount extends AConsumerObject<PostViewCount> {
	
	private String postId;
	private Long postViewCount;
	
	public PostViewCount() {
	}
	
	@Override
	public String toString() {
		return "PostCount [" + postViewCount + "]";
	}

	public PostViewCount(String string, Long l) {
		this.postId = string;
		this.postViewCount = l;
	}
	public String getPostId() {
		return postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public Long getPostViewCount() {
		return postViewCount;
	}
	public void setPostViewCount(Long postViewCount) {
		this.postViewCount = postViewCount;
	}
	@Override
	public int compareTo(PostViewCount o) {
		return Long.compare(this.postViewCount, o.postViewCount);
	}
	
	
	
}
