package me.jtjj222.zoom;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Zoom extends JavaPlugin implements Listener{
 	
	Material magicItem = Material.BOW;
	Boolean leftMouseButton;
	
	//Stores <playername,number of times they have clicked>
	//If they click once, zoom in a small amount. Twice, zoom in a bit more. ... 5 times, zoom out.
	public HashMap<String,Integer> playersZoomedIn = new HashMap<String,Integer>();
		
	public void onEnable() {
		
		this.saveDefaultConfig();
		
		this.getServer().getPluginManager().registerEvents(this, this);
		
		String configMagicItem = this.getConfig().getString("MagicItem");
		Material m = Material.getMaterial(configMagicItem);
		if (m==null) {
			getLogger().log(Level.INFO, "Could not find item " + configMagicItem + ". Using BOW instead.");
			m = Material.BOW;
		}
		magicItem = m;
		
		String configLeftMouseButton = this.getConfig().getString("Mouse_Button");
		if (configLeftMouseButton.contains("left")) leftMouseButton = true;
		else if (configLeftMouseButton.contains("right")) leftMouseButton = false;
		else {
			getLogger().log(Level.INFO, "Could not find mouse button " + configLeftMouseButton + ". Using right instead.");
			leftMouseButton = false;
		}
		
	}
	
	
	@EventHandler (priority=EventPriority.MONITOR)
	public void onPlayerDamagedByEntity (EntityDamageEvent e) {
		if (playersZoomedIn.isEmpty()) return;
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (playersZoomedIn.containsKey(p.getName())) {
				playersZoomedIn.remove(p.getName());
				removeZoom(p);
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		
		Action airAction = leftMouseButton ? Action.LEFT_CLICK_AIR : Action.RIGHT_CLICK_AIR;
		Action blockAction = leftMouseButton ? Action.LEFT_CLICK_BLOCK : Action.RIGHT_CLICK_BLOCK;
		
		if (e.getAction() == airAction || e.getAction() == blockAction) {
			if (e.getMaterial() == magicItem) {
								
				if (playersZoomedIn.containsKey(e.getPlayer().getName()) ) {
					
					//They have used the command before
					int timesClicked = playersZoomedIn.get(e.getPlayer().getName());
					
					if (timesClicked >= 4) {
						playersZoomedIn.remove(e.getPlayer().getName());
						removeZoom(e.getPlayer());
						return;
					}
					
					playersZoomedIn.put(e.getPlayer().getName(), timesClicked + 1);
					
					if (timesClicked == 3) zoom4(e.getPlayer());
					else if (timesClicked == 2) zoom3(e.getPlayer());
					else if (timesClicked == 1) zoom2(e.getPlayer());
					
				}
				
				else {
					
					playersZoomedIn.put(e.getPlayer().getName(), 1);
					zoom1(e.getPlayer());
				}
				
			}
		}	
	}
	
	public void zoom1(Player p) {
		p.removePotionEffect(PotionEffectType.SLOW);
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,20000, 3));
	}
	
	public void zoom2(Player p) {
		p.removePotionEffect(PotionEffectType.SLOW);
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,20000, 8));		
	}
	
	public void zoom3(Player p) {
		p.removePotionEffect(PotionEffectType.SLOW);
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,20000, 10));		
	}
	
	public void zoom4(Player p) {
		p.removePotionEffect(PotionEffectType.SLOW);
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,20000, 12));
	}	
	
	private void removeZoom(Player p) {
		p.removePotionEffect(PotionEffectType.SLOW);		
	}
}