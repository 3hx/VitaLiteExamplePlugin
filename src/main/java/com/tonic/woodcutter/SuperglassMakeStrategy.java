package com.tonic.woodcutter;

import com.tonic.api.widgets.InventoryAPI;
import com.tonic.api.widgets.MagicAPI;
import com.tonic.data.ItemContainerEx;
import com.tonic.data.ItemEx;
import com.tonic.data.magic.SpellBook;
import com.tonic.data.magic.spellbooks.Lunar;
import com.tonic.util.ClickManagerUtil;
import net.runelite.api.gameval.InventoryID;

import java.util.List;
import java.util.function.Predicate;

public enum SuperglassMakeStrategy
{
    DROP_FULL(v -> InventoryAPI.isFull()),
    DROP_EACH(v -> InventoryAPI.contains("Logs"))
    ;

    private final Predicate<Void> condition;

    SuperglassMakeStrategy(Predicate<Void> condition)
    {
        this.condition = condition;
    }

    public boolean process()
    {
        if(condition.test(null))
        {
            castSuperglassMake();
            return true;
        }
        return false;
    }

    private void castSuperglassMake() {
        if (SpellBook.getCurrent() ==  SpellBook.LUNAR) {
            MagicAPI.cast(Lunar.SUPERGLASS_MAKE);
        }

        }
}
