package net.runelite.client.plugins.microbot.util.bank;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.bank.models.BankItemWidget;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static net.runelite.client.plugins.microbot.util.Global.*;
import static net.runelite.client.plugins.microbot.util.globval.VarbitIndices.BANK_WITHDRAW_QUANTITY;

public class Rs2Bank {

    public static int widgetId;
    public static int itemId;
    public static  Widget itemWidget;
    public static int identifier;
    public static List<Widget> bankItems = new ArrayList<>();
    public static CopyOnWriteArrayList<Widget> inventoryItems = new CopyOnWriteArrayList<>();

    public static GameObject objectToBank = null;

    public static boolean depositXContains(String itemName, int amount) {
        Microbot.status = "Deposit " + amount + " -> " + itemName;
        if (!isOpen()) return false;
        if (!Inventory.hasItemContains(itemName)) return true;
        Widget item = Inventory.findItemContains(itemName);
        if (item == null) return false;
        setBankOptionX(amount);
        boolean action = Rs2Menu.doAction("Deposit-" + amount, item.getBounds());
        sleep(600, 1000);
        return action;
    }

    private static void setBankOptionX(int amount) {
        if (Microbot.getVarbitValue(BANK_WITHDRAW_QUANTITY) != 3 || Microbot.getVarbitValue(3960) != amount) {
            Widget withdrawX = Rs2Widget.getWidget(786466);
            if (withdrawX != null) {
                Rs2Menu.setOption("Set custom quantity");
                Microbot.getMouse().move(withdrawX.getBounds());
                sleep(150, 250);
                Microbot.getMouse().click(withdrawX.getBounds());
                sleep(1000);
                VirtualKeyboard.typeString(Integer.toString(amount));
                VirtualKeyboard.enter();
                sleep(400, 600);
            }
        }
    }

    public static boolean depositAll(String itemName) {
        Microbot.status = "Deposit all " + itemName;
        if (!isOpen()) return false;
        if (!Inventory.hasItem(itemName)) return true;
        Widget item = Inventory.findItem(itemName);
        if (item == null) return false;
        if(Rs2Widget.getChildWidgetSpriteID(786468,0) != 1150){Rs2Widget.clickWidget(786468);}
        sleep(600, 1000);
        boolean action = Rs2Menu.doAction("Deposit-all", item.getBounds());
        sleep(600, 1000);
        return action;
    }
    public static boolean depositAll(int itemID) {
        Microbot.status = "Deposit all " + itemID;
        if (!isOpen()) return false;
        if (!Inventory.hasItem(itemID)) return true;
        Widget item = Inventory.findItem(itemID);
        if (item == null) return false;
        if(Rs2Widget.getChildWidgetSpriteID(786468,0) != 1150){Rs2Widget.clickWidget(786468);}
        sleep(600, 1000);
        boolean action = Rs2Menu.doAction("Deposit-all", item.getBounds());
        sleep(600, 1000);
        return action;
    }

    public static boolean depositAllContains(String itemName) {
        Microbot.status = "Deposit all " + itemName;
        if (!isOpen()) return false;
        if (!Inventory.hasItemContains(itemName)) return true;
        Widget item = Inventory.findItemContains(itemName);
        if (item == null) return false;
        if(Rs2Widget.getChildWidgetSpriteID(786468,0) != 1150){Rs2Widget.clickWidget(786468);}
        sleep(600, 1000);
        boolean action = Rs2Menu.doAction("Deposit-all", item.getBounds());
        sleep(600, 1000);
        return action;
    }

    public static void closeBank() {
        Microbot.status = "Closing bank";
        if (!isOpen()) return;
        Rs2Widget.clickChildWidget(786434, 11);
        sleepUntilOnClientThread(() -> Rs2Widget.getWidget(786445) == null);
    }

    public static boolean isOpen() {
        Microbot.status = "Checking if bank is open";
        if (Rs2Widget.hasWidget("Please enter your PIN")) {
            Microbot.getNotifier().notify("[ATTENTION] Please enter your bankpin so the script can continue.");
            sleep(5000);
            return false;
        }
        return Rs2Widget.findWidget("Rearrange mode", null) != null;
    }

    @Deprecated(since = "Use isOpen instead", forRemoval = true)
    public static boolean isBankOpen() {
        Microbot.status = "Checking if bank is open";
        if (Rs2Widget.hasWidget("Please enter your PIN")) {
            Microbot.getNotifier().notify("[ATTENTION] Please enter your bankpin so the script can continue.");
            sleep(5000);
            return false;
        }
        return Rs2Widget.findWidget("Rearrange mode", null) != null;
    }

    public static boolean openBank() {
        Microbot.status = "Opening bank";
        try {
            if (Inventory.isUsingItem())
                Microbot.getMouse().click();
            if (isOpen()) return true;
            NPC npc = Rs2Npc.getNpc("banker");
            if (npc == null) return false;
            boolean action = Rs2Menu.doAction("bank", npc.getCanvasTilePoly());
            if (action) {
                sleepUntil(() -> isOpen() || Rs2Widget.hasWidget("Please enter your PIN"), 5000);
                sleep(600, 1000);
                return true;
            }
            return false;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }
    public static boolean openCertainBanker(int bankerID) {
        Microbot.status = "Opening bank";
        try {
            if (Inventory.isUsingItem())
                Microbot.getMouse().click();
            if (isOpen()) return true;
            NPC npc = Rs2Npc.getNpc(bankerID);
            if (npc == null) return false;
            boolean action = Rs2Menu.doAction("bank", npc.getCanvasTilePoly() );
            if (action) {
                sleepUntil(() -> isOpen() || Rs2Widget.hasWidget("Please enter your PIN"), 5000);
                sleep(600, 1000);
                return true;
            }
            return false;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }
    public static boolean openCertainBank(TileObject bank) {
        Microbot.status = "Opening bank";
        try {
            if (Inventory.isUsingItem())
                Microbot.getMouse().click();
            if (isOpen()) return true;
            if (bank == null) return false;
            boolean action = Rs2Menu.doAction(new String[]{"use","bank"}, bank.getCanvasTilePoly().getBounds());
            if (action) {
                sleepUntil(() -> isOpen() || Rs2Widget.hasWidget("Please enter your PIN"), 5000);
                sleep(600, 1000);
                return true;
            }
            return false;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }public static boolean openCertainBank(NPC banker) {
        Microbot.status = "Opening bank";
        try {
            if (Inventory.isUsingItem())
                Microbot.getMouse().click();
            if (isOpen()) return true;
            if (banker == null) return false;
            boolean action = Rs2Menu.doAction("bank", banker.getCanvasTilePoly());
            if (action) {
                sleepUntil(() -> isOpen() || Rs2Widget.hasWidget("Please enter your PIN"), 5000);
                sleep(600, 1000);
                return true;
            }
            return false;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    public static boolean scrollTo(Widget widget) {
        Microbot.status = "Searching for item";
        if (Microbot.getClient().isClientThread()) {
            Microbot.getNotifier().notify("Could not scrollTo item because the code is ran on the main thread!");
            return false;
        }
        Widget mainWindow = Rs2Widget.getWidget(786445);
        int calc;
        Point point = new Point((int) mainWindow.getBounds().getCenterX(), (int) mainWindow.getBounds().getCenterY());
        Microbot.getMouse().move(point);
        do {
            calc = widget.getRelativeY() - mainWindow.getScrollY();

            if (calc >= 0 && calc < 500) break;

            point = new Point((int) mainWindow.getBounds().getCenterX(), (int) mainWindow.getBounds().getCenterY());

            if (calc > 0) {
                Microbot.getMouse().scrollDown(point);
            } else if (calc < 0) {
                Microbot.getMouse().scrollUp(point);
            }

            sleep(100, 300);
            mainWindow = Rs2Widget.getWidget(786445);

        } while (calc <= 0 || calc > 500);

        return true;
    }

    public static boolean withdrawItem(String itemName) {
        return withdrawItem(false, itemName);
    }

    public static boolean withdrawItem(boolean checkInventory, String itemName) {
        Microbot.status = "Withdrawing one " + itemName;
        if (checkInventory && Inventory.hasItem(itemName)) return true;
        if (Inventory.isFull()) return false;
        if (!isOpen()) {
            openBank();
        }
        Widget widget = Rs2Widget.findWidgetExact(itemName);
        if (widget == null) return false;
        if (widget.getItemQuantity() <= 0) return false;
        if (Microbot.getVarbitValue(BANK_WITHDRAW_QUANTITY) != 0) {
            Rs2Widget.clickWidget(786460);
            sleep(1000);
        }
        if (scrollTo(widget)) {
            Rs2Menu.doAction("Withdraw-1", widget.getBounds());
            sleep(100, 1000);
            return true;
        }
        return false;
    }

    public static boolean withdrawItemX(boolean checkInventory, String itemName, int amount) {
        Microbot.status = "Withdrawing " + amount + " " + itemName;
        if (checkInventory && Inventory.hasItem(itemName)) return true;
        if (!isOpen()) {
            openBank();
        }
        Widget widget = Rs2Widget.findWidgetExact(itemName);
        if (widget == null) return false;
        if (widget.getItemQuantity() <= 0) return false;
        setBankOptionX(amount);
        if (scrollTo(widget)) {
            Rs2Menu.doAction("Withdraw-" + amount, widget.getBounds());
            sleep(600, 1000);
            return true;
        }
        return false;
    }

    public static boolean withdrawItemAll(boolean checkInventory, String itemName) {
        Microbot.status = "Withdrawing All " + itemName;
        if (checkInventory && Inventory.hasItem(itemName)) return true;
        if (Inventory.isFull()) return false;
        if (!isOpen()) return false;
        Widget widget = Rs2Widget.findWidgetExact(itemName);
        if (widget == null) return false;
        if (widget.getItemQuantity() <= 0) return false;
        if (Microbot.getVarbitValue(BANK_WITHDRAW_QUANTITY) != 4) {
            Rs2Widget.clickWidget(786468);
            sleep(1000);
        }
        if (scrollTo(widget)) {
            Rs2Menu.doAction("Withdraw-All", widget.getBounds());
            sleep(600, 1000);
            return true;
        }
        return false;
    }

    public static void withdrawItems(String... itemNames) {
        if (isOpen()) {
            for (String itemName : itemNames) {
                withdrawItem(itemName);
            }
        }
    }

    public static void withdrawItems(boolean checkInventory, String... itemNames) {
        if (isOpen()) {
            for (String itemName : itemNames) {
                withdrawItem(checkInventory, itemName);
            }
        }
    }

    public static void withdrawItemsAll(String... itemNames) {
        withdrawItemsAll(false, itemNames);
    }

    public static void withdrawItemsAll(boolean checkInventory, String... itemNames) {
        if (isOpen()) {
            for (String itemName :
                    itemNames) {
                withdrawItemAll(checkInventory, itemName);
            }
        }
    }

    public static boolean useBank() {
        if (isOpen()) return true;
        GameObject bank = Rs2GameObject.findBank();
        if (bank == null) {
            GameObject chest = Rs2GameObject.findChest();
            if (chest == null) return false;
            Rs2GameObject.interact(chest, "use");
        } else {
            Rs2GameObject.interact(bank, "bank");
        }
        sleepUntil(Rs2Bank::isOpen);
        return true;
    }

    public static void useBank(String action) {
        Microbot.status = "Banking";
        GameObject bank = Rs2GameObject.findBank(action);
        if (bank == null) return;
        if (Rs2Menu.hasAction(bank.getCanvasTilePoly(), "Bank")) {
            Rs2Menu.doAction("Bank", bank.getClickbox());
            sleepUntil(Rs2Bank::isOpen, 5000);
        } else if (Rs2Menu.hasAction(bank.getCanvasTilePoly(), "Use")) { //for chests
            Rs2Menu.doAction("Use", bank.getClickbox());
            sleepUntil(Rs2Bank::isOpen, 5000);
        }
    }

    public static boolean depositAll() {
        Microbot.status = "Deposit all";
        if (Inventory.count() == 0) return true;
        Widget widget = Rs2Widget.findWidget(SpriteID.BANK_DEPOSIT_INVENTORY, null);
        if (widget != null) {
            Microbot.getMouse().click(widget.getBounds());
            sleep(1000, 2000);
            return true;
        }
        return false;
    }

    public static boolean depositEquipment(){
        Microbot.status = "Deposit equipment";
        if (Inventory.count() == 0) return true;
        Widget widget = Rs2Widget.findWidget(SpriteID.BANK_DEPOSIT_EQUIPMENT, null);
        if (widget != null) {
            Microbot.getMouse().click(widget.getBounds());
            sleep(1000, 2000);
            return true;
        }
        return false;
    }

    public static boolean hasItem(String itemName) {
        Microbot.status = "Looking for " + itemName + " in the bank";
        return Rs2Widget.findWidgetExact(itemName) != null && Rs2Widget.findWidgetExact(itemName).getItemQuantity() > 0;
    }

    public static boolean walkToBank() {
        BankLocation bankLocation = getNearestBank();
        Microbot.getWalker().walkTo(bankLocation.getWorldPoint());
        return bankLocation.getWorldPoint().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) <= 4;
    }

    public static boolean withdrawItemsRequired(List<ItemRequirement> itemsRequired) {
        List<ItemRequirement> itemsMissing = new ArrayList<>();
        for (ItemRequirement item: itemsRequired) {
            if (!Inventory.hasItemAmount(item.getId(), item.getQuantity()) && !Inventory.hasItemAmountStackable(item.getName(), item.getQuantity())) {
                itemsMissing.add(item);
            }
        }
        if (!itemsMissing.isEmpty()) {
            for (ItemRequirement item : itemsMissing) {
                Rs2Bank.withdrawItemX(true, item.getName(), item.getQuantity());
            }
        }
        return itemsMissing.isEmpty();
    }

    public static boolean hasItems(List<ItemRequirement> itemsRequired) {
        List<ItemRequirement> itemsMissing = new ArrayList<>();
        for (ItemRequirement item: itemsRequired) {
            if (!Inventory.hasItemAmount(item.getId(), item.getQuantity()) && !Inventory.hasItemAmountStackable(item.getName(), item.getQuantity())) {
                itemsMissing.add(item);
            }
        }
        return itemsMissing.isEmpty();
    }

    public static BankLocation getNearestBank() {
        BankLocation nearest = null;
        double dist = Double.MAX_VALUE;
        int y = Microbot.getClient().getLocalPlayer().getWorldLocation().getY();
        boolean isInCave = Microbot.getClient().getLocalPlayer().getWorldLocation().getY() > 9000;
        if (isInCave) {
            y -= 6300; //minus -6300 to set y to the surface
        }
        WorldPoint local = new WorldPoint(Microbot.getClient().getLocalPlayer().getWorldLocation().getX(), y, Microbot.getClient().getPlane());
        for (BankLocation bankLocation : BankLocation.values())
        {
            double currDist = local.distanceTo2D(bankLocation.getWorldPoint());
            if (nearest == null || currDist < dist)
            {
                dist = currDist;
                nearest = bankLocation;
            }
        }
        return nearest;
    }

    private static void handleWithdrawFast(int id, int idx) {
        widgetId = 786445;
        itemId = id;
        identifier = idx;
        itemWidget = bankItems.stream().filter(x -> x.getItemId() == itemId).findFirst().orElse(null);
        if (itemWidget == null) return;
        Microbot.getMouse().clickFast(1, 1);
        sleep(100);
        widgetId = 0;
        itemId = 0;
    }

    private static void handleWearItemFast(int id, int idx)
    {
        widgetId = 983043;
        itemId = id;
        identifier = idx;
        itemWidget = inventoryItems.stream().filter(x -> x.getItemId() == itemId).findFirst().orElse(null);
        if (itemWidget == null) return;
        Microbot.getMouse().clickFast(1, 1);
        sleep(100);
        widgetId = 0;
        itemId = 0;
    }

    public static void withdrawFast(int id) {
        handleWithdrawFast(id, 1);
    }

    public static void withdrawFast(int id, int amount) {
        for (int i = 0; i < amount; i++) {
            handleWithdrawFast(id, 1);
        }
    }

    public static void withdrawAllFast(int id) {
        handleWithdrawFast(id, 8);
    }

    public static void wearItem(int id) {
        handleWearItemFast(id, 9);
    }

    public static void withdrawAndEquipFast(int id) {
        if (Rs2Equipment.hasEquipped(id)) return;
        withdrawFast(id);
        sleepUntil(() -> inventoryItems.stream().anyMatch(x -> x.getItemId() == id));
        wearItem(id);
    }

    public static void withdrawAllAndEquipFast(int id) {
        if (Rs2Equipment.hasEquipped(id)) return;
        withdrawAllFast(id);
        sleepUntil(() -> inventoryItems.stream().anyMatch(x -> x.getItemId() == id));
        wearItem(id);
    }

    public static void handleMenuSwapper(MenuEntry menuEntry) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (widgetId == 0 || itemWidget == null) return;
        int idx = itemWidget.getIndex();
        Rs2Reflection.setItemId(menuEntry, itemId);
        menuEntry.setOption("Withdraw-1");
        menuEntry.setIdentifier(identifier);
        menuEntry.setParam0(idx);
        menuEntry.setParam1(widgetId);
        menuEntry.setTarget("");
        menuEntry.setType(MenuAction.CC_OP);
    }

    public static void storeBankItemsInMemory(ItemContainerChanged e) {
        if (e.getContainerId() == 95) {
            int i = 0;
            bankItems.clear();
            for (Item item : e.getItemContainer().getItems()) {
                if(item==null){
                    i++;
                    continue;
                }
                if(Microbot.getItemManager().getItemComposition(item.getId()).getPlaceholderTemplateId()==14401){
                    i++;
                    continue;
                }
                bankItems.add(new BankItemWidget(Microbot.getItemManager().getItemComposition(item.getId()).getName(),item.getId(),item.getQuantity(),i));
                i++;
            }
        }
    }

    public static void storeInventoryItemsInMemory(ItemContainerChanged e) {
        if (e.getContainerId() == 93) {
            int i = 0;
            inventoryItems.clear();
            for (Item item : e.getItemContainer().getItems()) {
                if(item==null){
                    i++;
                    continue;
                }
                inventoryItems.add(new BankItemWidget(Microbot.getItemManager().getItemComposition(item.getId()).getName(),item.getId(),item.getQuantity(),i));
                i++;
            }
        }
    }
    public static void withdrawAllDriftNet() {
        widgetId = 20250629;
        itemId = ItemID.DRIFT_NET;
        identifier = 4;
        Microbot.getMouse().clickFast(1, 1);
        sleep(100);
        widgetId = 0;
        itemId = 0;
    }
}
