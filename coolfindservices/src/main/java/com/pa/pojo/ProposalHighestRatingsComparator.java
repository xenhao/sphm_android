package com.pa.pojo;

import java.util.Comparator;

public class ProposalHighestRatingsComparator implements Comparator<Proposal>
{
   

	@Override
	public int compare(Proposal left, Proposal right) {
	        if( Double.parseDouble(left.co_overall_rating) > Double.parseDouble(right.co_overall_rating)){
	        	return -1;
	        }else
	        	if( Double.parseDouble(left.co_overall_rating) < Double.parseDouble(right.co_overall_rating)){
		        	return 1;
		        }else{
		        	return 0;
		        }
	}
}
