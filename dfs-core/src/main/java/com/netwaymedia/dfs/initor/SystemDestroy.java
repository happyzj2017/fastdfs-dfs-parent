package com.netwaymedia.dfs.initor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Service;

/**
 * not working....!!!
 *
 */
@Service
public class SystemDestroy implements ApplicationListener<ContextStoppedEvent> {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void onApplicationEvent(ContextStoppedEvent event) {
		logger.debug("contexted stopped !");
	}

}
