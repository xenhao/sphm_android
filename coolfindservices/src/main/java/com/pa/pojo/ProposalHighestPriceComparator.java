package com.pa.pojo;

import java.util.Comparator;

public class ProposalHighestPriceComparator implements Comparator<Proposal>
{
   

	@Override
	public int compare(Proposal left, Proposal right) {
	        if( Double.parseDouble(left.total) > Double.parseDouble(right.total)){
	        	return -1;
	        }else
	        	if( Double.parseDouble(left.total) < Double.parseDouble(right.total)){
		        	return 1;
		        }else{
		        	return 0;
		        }
	}
}
