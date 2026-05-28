package net.glacierclient.modules.expanded.social;

import net.glacierclient.core.module.GlacierMod;
import net.glacierclient.core.module.Category;
import net.glacierclient.core.settings.BooleanSetting;
import net.glacierclient.core.settings.NumberSetting;

public class ReportBotAssist extends GlacierMod {

    private final BooleanSetting autoCollect = new BooleanSetting("Auto Collect", "Automatically collect evidence on /report", false);
    private final BooleanSetting autoScreenshot = new BooleanSetting("Auto Screenshot", "Take screenshot when report is issued", false);
    private final BooleanSetting packageEvidence = new BooleanSetting("Package Evidence", "Bundle all evidence into a zip file", false);
    private final NumberSetting chatHistoryLines = new NumberSetting("Chat History Lines", "Number of chat lines to include in report", 30, 10, 100);

    public ReportBotAssist() {
        super("Report Assist", "Auto-collect evidence when /report is used", Category.QOL);
        addSettings(autoCollect, autoScreenshot, packageEvidence, chatHistoryLines);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public boolean isAutoCollect() { return autoCollect.getValue(); }
    public boolean isAutoScreenshot() { return autoScreenshot.getValue(); }
    public boolean isPackageEvidence() { return packageEvidence.getValue(); }
    public int getChatHistoryLines() { return (int)(double) chatHistoryLines.getValue(); }
}
