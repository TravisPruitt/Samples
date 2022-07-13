package com.disney.xband.idms.dao;

import com.disney.xband.idms.lib.model.oneview.Celebration;
import com.disney.xband.idms.lib.model.oneview.CelebrationGuestPost;
import com.disney.xband.idms.lib.model.oneview.CelebrationGuestPut;
import com.disney.xband.idms.lib.model.oneview.CelebrationPost;
import com.disney.xband.idms.lib.model.oneview.CelebrationPut;

public interface CelebrationDAO 
{
	public long SaveCelebration(CelebrationPost celebration) throws DAOException;

	public boolean UpdateCelebration(CelebrationPut celebration) throws DAOException;

	public boolean DeleteCelebration(long celebrationId) throws DAOException;

	public Celebration GetCelebration(String identifierType, String identifierValue) throws DAOException;

	public boolean AddCelebrationGuest(CelebrationGuestPost celebrationGuest) throws DAOException;

	public boolean UpdateCelebrationGuest(CelebrationGuestPut celebrationGuest) throws DAOException;

	public boolean DeleteCelebrationGuest(CelebrationGuestPut celebrationGuest) throws DAOException;
}
