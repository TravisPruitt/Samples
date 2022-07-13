package com.disney.xband.xbrc.ui.action;

import java.util.LinkedList;
import java.util.List;

import com.disney.xband.xbrc.lib.entity.GuestInfo;
import com.disney.xband.xbrc.ui.db.Data;
import com.opensymphony.xwork2.ActionSupport;

public class ShowQueueAjaxAction extends BaseAction {

	public class GuestInfoDisplay {
		GuestInfo guest;
		Integer topPos;
		Integer leftPos;
		String image;
		
		public GuestInfoDisplay(GuestInfo guest, Double hMult, Double vMult, Integer vOffset, Integer hOffset) {
			this.guest = guest;
			this.leftPos = (int)(guest.getX() * hMult) + hOffset;
			this.topPos = (int)(guest.getY() * vMult) + vOffset;
			if (guest.getHasxPass())
				this.image = "xguest.png";
			else
				this.image = "guest.png";
		}

		public GuestInfo getGuest() {
			return guest;
		}

		public void setGuest(GuestInfo guest) {
			this.guest = guest;
		}

		public Integer getTopPos() {
			return topPos;
		}		

		public Integer getLeftPos() {
			return leftPos;
		}
		
		public String getImage() {
			return image;
		}
	}
	
	private List<GuestInfoDisplay> guestList;
	
	@Override
	public String execute() throws Exception {
		
		List<GuestInfo> guests = Data.GetGuests();
		guestList = new LinkedList<GuestInfoDisplay>();		
		for (GuestInfo guest : guests) {
			guestList.add(new GuestInfoDisplay(guest, ShowQueueAction.hMult, ShowQueueAction.vMult, ShowQueueAction.vOffset, ShowQueueAction.hOffset));
		}
		
		return super.execute();
	}
	
	public List<GuestInfoDisplay> getGuestList() {
		return guestList;
	}
}
