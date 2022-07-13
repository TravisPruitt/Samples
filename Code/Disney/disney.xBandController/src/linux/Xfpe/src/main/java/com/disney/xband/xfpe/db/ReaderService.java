package com.disney.xband.xfpe.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xbrc.lib.entity.Reader;

public interface ReaderService {
	
	public Reader find(long id) throws SQLException;
	public Reader find(String readerId) throws SQLException;
	
	public Collection<Reader> findAll() throws SQLException;
	
	public Collection<Reader> findByLocation(Location location) throws SQLException;
	
	public void delete(Reader reader) throws SQLException;
	
	public HashMap<Location,Collection<Reader>> getByLocation();
	
	public Collection<Reader> findUnlinked() throws Exception;
	
	public void unlinkReader(Long id) throws IllegalArgumentException, SQLException;
}
