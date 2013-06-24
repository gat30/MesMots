package com.aopds.user;

import com.aopds.aopdsData.domain.User;

public interface AopdsUserChangedListener {
	
	public void onUserChanged(User user);
	
}
