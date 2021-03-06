package com.whirlwindgames.dibujaron.sqempire.database.object;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.starquestminecraft.bukkit.StarQuest;
import com.starquestminecraft.bukkit.util.BungeeUtil;
import com.whirlwindgames.dibujaron.sqempire.Empire;
import com.whirlwindgames.dibujaron.sqempire.SQEmpire;
import com.whirlwindgames.dibujaron.sqempire.database.EmpireDB;
import com.whirlwindgames.dibujaron.sqempire.util.AsyncUtil;
import com.whirlwindgames.dibujaron.sqempire.util.RSReader;
import com.whirlwindgames.dibujaron.sqempire.util.SuperPS;

public class EmpirePlayer {
	private UUID id;
	private String name;
	private int empire;
	private long lastSeen;
	public Date lastChanged;

	private static HashMap<UUID, EmpirePlayer> cache = new HashMap<UUID, EmpirePlayer>();
	
	public static EmpirePlayer getOnlinePlayer(Player p){
		EmpirePlayer ep = cache.get(p.getUniqueId());
		if(ep == null){
			throw new NullPointerException("Requested online EmpirePlayer but none exists!");
		}
		return ep;
	}
	
	public static EmpirePlayer getFromUUID(UUID uuid){
		EmpirePlayer ep = cache.get(uuid);
		if(ep == null){
			throw new NullPointerException("Requested EmpirePlayer but none exists!");
		}
		return ep;
	}
	
	public void setEmpire(Empire e){
		empire = e.getID();
		publishData();
	}
	public static void loadPlayerData(final Player player) {
		// TODO Auto-generated method stub
		AsyncUtil.runAsync(new Runnable(){
			public void run(){
				final EmpirePlayer p = new EmpirePlayer(player.getUniqueId(), true);
				AsyncUtil.runSync(new Runnable(){
					public void run(){
						cache.put(player.getUniqueId(), p);
						System.out.println("Cache add: " + cache.size());
						if(p.empire != 0 && p.lastSeen == 0){
							//this person has an empire but was not generated by the normal system
							//must be from EmpireBuilder
							AsyncUtil.runAsync(new Runnable(){
								public void run(){
									p.lastSeen = System.currentTimeMillis();
									p.publishData();
								}
							});
							
							AsyncUtil.runSyncLater(new Runnable(){
								public void run(){
									player.sendMessage("[EmpireBuilder] you've been assigned empire " + Empire.fromID(p.empire).getName() + "!");
									StarQuest.getVaultEconomy().depositPlayer(player, 10000);
									if(p.empire == Empire.ARATOR.getID()){
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pp user " + player.getName() + " addgroup Arator0");
										BungeeUtil.sendPlayer(player, "AratorSystem", "AratorSystem", 2598, 100, 1500);
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
												"eb janesudo Aratorians, please welcome your newest member " + player.getName() + "!");
									} else if(p.empire == Empire.REQUIEM.getID()){
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pp user " + player.getName() + " addgroup Requiem0");
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
												"eb janesudo Requiem, please welcome your newest member " + player.getName() + "!");
									} else if(p.empire == Empire.YAVARI.getID()){
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pp user " + player.getName() + " addgroup Yavari0");
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
												"eb janesudo Yavari, please welcome your newest member " + player.getName() + "!");
									}
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pp user " + player.getUniqueId() + " removegroup Guest");
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playersendempire " + player.getName());
								}
							}, 20L);
						}
						p.publishData();
					}
				});
				
			}
		});
	}
	public static void unloadPlayerData(Player player) {
		cache.remove(player.getUniqueId());
		System.out.println("Cache remove: " + cache.size());
	}
	public EmpirePlayer(UUID u){
		this(u, true);
	}
	
	String updateCommand = "INSERT INTO minecraft.empire_player(uuid,lname,empire,lastSeen,lastChanged)"
			+ "VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE "
			+ "uuid=?,lname=?,empire=?,lastSeen=?,lastChanged=?";

	private EmpirePlayer(UUID u, boolean requestData){
		id = u;
		lastSeen = 0;
		if(requestData){
			requestData();
		}
	}
	
	public static List<EmpirePlayer> getPlayersOnlineRecently(long ticks){
		long earliestAcceptableLogoff = System.currentTimeMillis() - ticks;
		String query = "SELECT * from minecraft.empire_player WHERE lastSeen" + " > " + earliestAcceptableLogoff;
		RSReader rs = new RSReader(EmpireDB.requestData(query));
		List<EmpirePlayer> retval = new ArrayList<EmpirePlayer>();
		while(rs.next()){
			UUID u = UUID.fromString(rs.getString("uuid"));
			EmpirePlayer player = new EmpirePlayer(u, false);
			player.readData(rs);
			retval.add(player);
		}
		return retval;
	}
	
	public Empire getEmpire(){
		return Empire.fromID(empire);
	}
	
	private void requestData(){
		AsyncUtil.crashIfNotAsync();
		String query = "SELECT * from minecraft.empire_player WHERE uuid=\"" + id.toString() + "\"";
		RSReader rs = new RSReader(EmpireDB.requestData(query));

		if(rs.next()){
			readData(rs);
		} else {
			fillDefaultData();
		}
	}
	
	private void readData(RSReader rs){
		name = rs.getString("lname");
		empire = rs.getInt("empire");
		lastSeen = rs.getLong("lastSeen");
		lastChanged = rs.getDate("lastChanged");
	}
	
	private void fillDefaultData(){
		empire = 0;
		lastSeen = System.currentTimeMillis();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -2);
		
		lastChanged = cal.getTime();
	}
	
	public void publishData(){
		//AsyncUtil.crashIfNotAsync();
		SuperPS ps = new SuperPS(EmpireDB.prepareStatement(updateCommand),5);
		ps.setDuplicate(1,id.toString());
		ps.setDuplicate(2,name);
		ps.setDuplicate(3,empire);
		
		lastSeen = System.currentTimeMillis();
		
		ps.setDuplicate(4, lastSeen);
		ps.setDuplicate(5, new java.sql.Date(lastChanged.getTime()));
		ps.executeAndClose();
	}
	
	public UUID getUUID() {
		
		return id;
		
	}
}
