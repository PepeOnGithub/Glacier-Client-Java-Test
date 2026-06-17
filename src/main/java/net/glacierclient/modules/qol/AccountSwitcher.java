package net.glacierclient.modules.qol;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

import java.util.ArrayList;
import java.util.List;

public class AccountSwitcher extends GlacierMod {

    private final NumberSetting maxAccounts = new NumberSetting("Max Accounts", "Maximum stored accounts", 1, 10, 5);
    private final BooleanSetting requireConfirmation = new BooleanSetting("Require Confirmation", "Confirm before switching", true);
    private final BooleanSetting showAvatar = new BooleanSetting("Show Avatar", "Show account avatars", true);
    private final BooleanSetting autoSave = new BooleanSetting("Auto Save", "Auto-save account list", true);

    private final List<String> accounts = new ArrayList<>();

    public AccountSwitcher() {
        super("Account Switcher", "Switch between multiple Minecraft accounts", Category.QOL);
        addSettings(maxAccounts, requireConfirmation, showAvatar, autoSave);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {
        if (autoSave.getValue()) saveAccounts();
    }

    @Override
    public void onTick() {}

    public void addAccount(String uuid) {
        if (accounts.size() < (int)(double) maxAccounts.getValue() && !accounts.contains(uuid)) {
            accounts.add(uuid);
        }
    }

    private boolean pendingConfirm = false;

    public void switchTo(String uuid) {
        // Require Confirmation: the first call arms confirmation, the second performs the switch.
        if (requireConfirmation.getValue() && !pendingConfirm) { pendingConfirm = true; return; }
        pendingConfirm = false;
        // Account switching via auth API
    }

    /** Avatar URL for an account, or null when avatars are disabled. */
    public String getAvatarUrl(String uuid) {
        return showAvatar.getValue() ? "https://crafatar.com/avatars/" + uuid : null;
    }

    private void saveAccounts() {
        // Save account list to config
    }

    public List<String> getAccounts() { return accounts; }
}
