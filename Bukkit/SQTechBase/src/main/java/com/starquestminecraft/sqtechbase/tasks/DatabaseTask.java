package com.starquestminecraft.sqtechbase.tasks;

import java.sql.Connection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import com.starquestminecraft.bukkit.StarQuest;
import com.starquestminecraft.sqtechbase.SQTechBase;
import com.starquestminecraft.sqtechbase.database.DatabaseInterface;
import com.starquestminecraft.sqtechbase.database.SQLDatabase;

public class DatabaseTask extends Thread{

	@SuppressWarnings("deprecation")
	public void run() {
		
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		
		scheduler.scheduleAsyncRepeatingTask(SQTechBase.getPluginMain(), new Runnable() {
			
			@Override
			public void run() {

				System.out.print("SQTechBase: Saved to database");
				
				try(Connection con = StarQuest.getDatabaseConnection()) {
					
					SQLDatabase.clearMachines(con, SQTechBase.config.getString("server name"));
					SQLDatabase.clearGUIBlocks(con, SQTechBase.config.getString("server name"));
					
					DatabaseInterface.saveObjects();
					
					for (Player player : Bukkit.getOnlinePlayers()) {
						
						DatabaseInterface.updateOptions(player);
						
					}
					
				} catch (Exception e) {

					e.printStackTrace();
					
				}
				
			}
			
		}, 0, 6000);
		
	}
	
}
