package nl.tychovi.stonks.managers;

import nl.tychovi.stonks.Stonks;
import nl.tychovi.stonks.gui.ExampleGui;
import nl.tychovi.stonks.gui.Gui;
import nl.tychovi.stonks.gui.InvitesGui;

import java.util.ArrayList;

public class GuiManager extends SpigotModule {
    private ArrayList<Gui> guiList = new ArrayList<>();

    public GuiManager(Stonks plugin) {
        super("guiManager", plugin);

        guiList.add(new ExampleGui());
        guiList.add(new InvitesGui());

        for(Gui gui : guiList) {
            plugin.getServer().getPluginManager().registerEvents(gui, plugin);
        }
    }

    public Gui getGui(String name) {
        for(Gui gui : guiList) {
            if(gui.getTitle().equals(name)) {
                return gui;
            }
        }
        return null;
    }
}
