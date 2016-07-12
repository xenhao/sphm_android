package com.pa.common;

import com.pa.pojo.MolData;

public class PayHelper {
	// merhant name, app name, verify, key,pass
	private static String[] CONFIG_SG = { "pageadvisor", "pageadvisor",
			"770bfe21140e3bca52f2c261d880b6e4", "api_pageadvisor",
			"api_agap286rsv" };
	private static String[] CONFIG_MY = { "pageadvisormy", "pageadvisormy",
			"4e82daa41c31f3319763dbcba4bdddb6", "api_pageadvisormy",
			"api_pgadmy134" };

	
	private static String[] CONFIG_SANDBOX = { "pageadvisormy_Dev", "pageadvisormy_Dev",
		"f978084e79c48c7b2c6ff96994ae575b", "api_pageadvisormy_Dev",
		"api_page1320adv*" };

	
	public static MolData getMolData(String country){
		country=country.toLowerCase();
		if("singapore".equals(country)||"sgd".equals(country)){
			return new MolData(CONFIG_SG);
		}else
		if("malaysia".equals(country)||"myr".equals(country)){
			return new MolData(CONFIG_MY);
			
		}
		else
			if("sandbox".equals(country)){
				return new MolData(CONFIG_SANDBOX);
				
			}
		return null;
	}
	
	
}
