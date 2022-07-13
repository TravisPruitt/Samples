package com.disney.socket.library;

import java.util.EventListener;


public interface SwitchBoardMessageListener extends EventListener {

	public void switchBoardMessage(SwitchBoardMessageEvent evnt);

}
