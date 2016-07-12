package com.pa.pojo;

public class PendingRating {
	public PendingRating() {
	};

	public String rated;
	public RatingData rating_data;

	public static class RatingData {
		public RatingData() {
		};

		public String merchant_username, request_title, serial,
				preferred_time1_start,company_name;
	}

}
