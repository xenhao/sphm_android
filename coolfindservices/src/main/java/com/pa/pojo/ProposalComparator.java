package com.pa.pojo;

import java.util.Comparator;

public class ProposalComparator implements Comparator<Proposal>
{
   

	@Override
	public int compare(Proposal left, Proposal right) {
		// TODO Auto-generated method stub
	      
	        if( Double.parseDouble(left.total) < Double.parseDouble(right.total)){
	        	return -1;
	        }else
	        	if( Double.parseDouble(left.total) > Double.parseDouble(right.total)){
		        	return 1;
		        }else{
		        	return 0;
		        }
	}
}
