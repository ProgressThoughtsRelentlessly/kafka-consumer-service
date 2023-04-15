package com.pthore.service.kafkaConsumers.models;

public class SearchedKeywordCount extends AConsumerObject<SearchedKeywordCount> {
	private String keyword;
	private Long usageCount;
	
	@Override
	public String toString() {
		return "SearchedKeyword [keyword=" + keyword + ", usageCount=" + usageCount + "]";
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Long getUsageCount() {
		return usageCount;
	}
	public void setUsageCount(Long usageCount) {
		this.usageCount = usageCount;
	}
	@Override
	public int compareTo(SearchedKeywordCount o) {
		
		return Long.compare(this.usageCount, o.usageCount);
	}
	
}
