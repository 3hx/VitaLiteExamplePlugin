package com.tonic.woodcutter;

import ch.qos.logback.core.util.LocationUtil;
import com.google.inject.Provides;
import com.sun.jdi.Locatable;
import com.tonic.Logger;
import com.tonic.api.entities.NpcAPI;
import com.tonic.api.entities.PlayerAPI;
import com.tonic.api.entities.TileObjectAPI;
import com.tonic.api.game.ClientScriptAPI;
import com.tonic.api.widgets.BankAPI;
import com.tonic.api.widgets.InventoryAPI;
import com.tonic.api.widgets.MagicAPI;
import com.tonic.api.widgets.WidgetAPI;
import com.tonic.data.TileObjectEx;
import com.tonic.data.magic.MagicCast;
import com.tonic.data.magic.Spell;
import com.tonic.data.magic.SpellBook;
import com.tonic.data.magic.spellbooks.Lunar;
import com.tonic.queries.NpcQuery;
import com.tonic.queries.TileObjectQuery;
import com.tonic.services.breakhandler.BreakHandler;
import com.tonic.util.ClickManagerUtil;
import com.tonic.util.Location;
import com.tonic.util.VitaPlugin;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@PluginDescriptor(
        name = "Vita Wood Chopper Pro",
        description = "A sample VitaLite Plugin",
        tags = {"vita", "sample", "woodcutter", "wood", "chopper", "pro"}
)
public class ExamplePlugin extends VitaPlugin
{
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private Client client;

    @Inject
    private BreakHandler breakHandler;
    private SidePanel panel;
    private NavigationButton navButton;
    private ExamplePluginConfig config;
    private int tickCount = 0;
    @Provides
    ExamplePluginConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ExamplePluginConfig.class);
    }

    @Override
    protected void startUp()
    {
        panel = injector.getInstance(SidePanel.class);

        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");

        navButton = NavigationButton.builder()
                .tooltip("Example Vita Woodcutting Plugin")
                .icon(icon)
                .priority(5)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(navButton);

        breakHandler.register(this);
    }

    @Override
    protected void shutDown()
    {
        clientToolbar.removeNavigation(navButton);
        panel.shutdown();

        breakHandler.unregister(this);
    }

    @Override
    public void loop()
    {
        if(panel == null || !panel.isRunning())
            return;

        if(breakHandler.isBreaking(this))
            return;

        Player local = client.getLocalPlayer();
        if(!PlayerAPI.isIdle(local))
            return;

//        delays
        tickCount++;


        if (tickCount % 3 == 0) {
            return;
        }

//        DropStrategy strategy = panel.getSelectedStrategy();
//
//        if(strategy.process())
//        {
//            return;
//        }



        if (InventoryAPI.getEmptySlots() > 24 && !BankAPI.isOpen()) {
            Logger.console("here0");
            this.bank();
            return;

        }


        if (InventoryAPI.count("Giant seaweed") == 2) {
            Logger.console("here1");

            if (SpellBook.getCurrent() == SpellBook.LUNAR){
                Logger.console("herex1");
                MagicAPI.cast(Lunar.SUPERGLASS_MAKE);
                Logger.console("herex2");

                return;

            }


        } else if (InventoryAPI.contains("Molten glass")) {
            Logger.console("here2");

            this.depositRequiredItems();
        } else
            Logger.console("here3");
        this.withdrawRequiredItems();
        }


    public void bank() {

        TileObjectEx chest = TileObjectAPI.search().withName("Bank chest").nearest();

        ClickManagerUtil.queueClickBox(chest);
        TileObjectAPI.interact(chest, "Use");


    }

    public void withdrawRequiredItems(){
        if (BankAPI.isOpen()) {
            BankAPI.withdraw("Giant seaweed", 2, false);
            BankAPI.withdraw("Bucket of sand", 12, false);

//          Close bank
            ClientScriptAPI.runScript(29);
            ClientScriptAPI.closeNumericInputDialogue();



        }
    }

    public void depositRequiredItems(){
        if (!BankAPI.isOpen()) {
            this.bank();
        }

        if (BankAPI.isOpen()) {
            BankAPI.deposit("Molten glass", 17);
            //          Close bank
            ClientScriptAPI.runScript(29);
            ClientScriptAPI.closeNumericInputDialogue();

            }

    }


        public void startBreaks()
    {
        breakHandler.start(this);
    }

    public void stopBreaks()
    {
        breakHandler.stop(this);
    }
}